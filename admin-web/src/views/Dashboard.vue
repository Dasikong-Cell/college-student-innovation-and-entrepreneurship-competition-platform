<template>
  <div v-loading="loading" class="dashboard">
    <el-row :gutter="20">
      <el-col v-for="card in statCards" :key="card.key" :xs="12" :sm="12" :md="6">
        <div class="stat-card" :style="{ borderTopColor: card.color }">
          <div class="stat-info">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">{{ card.value }}</div>
          </div>
          <div class="stat-icon" :style="{ background: card.color + '22', color: card.color }">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :xs="24" :md="16">
        <div class="chart-card">
          <div class="chart-title">各大赛项目数量</div>
          <div ref="barRef" class="chart-area"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="8">
        <div class="chart-card">
          <div class="chart-title">项目状态分布</div>
          <div ref="pieRef" class="chart-area"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { stats as competitionStats } from '../api/competition'
import { page as projectPage } from '../api/project'

const loading = ref(false)
const barRef = ref()
const pieRef = ref()
let barChart = null
let pieChart = null

const data = reactive({
  competitions: 0,
  projects: 0,
  experts: 0,
  todayReviews: 0,
  competitionProjectMap: [],
  statusDistribution: []
})

const statCards = computed(() => [
  { key: 'competitions', label: '大赛总数', value: data.competitions, color: '#3b82f6', icon: 'Trophy' },
  { key: 'projects', label: '项目总数', value: data.projects, color: '#10b981', icon: 'Document' },
  { key: 'experts', label: '专家数量', value: data.experts, color: '#f59e0b', icon: 'User' },
  { key: 'reviews', label: '今日评审', value: data.todayReviews, color: '#ef4444', icon: 'EditPen' }
])

async function loadData() {
  loading.value = true
  try {
    const res = await competitionStats()
        const d = res.data || res
    data.competitions = d.total || 0
    try { data.projects = (await projectPage({page:1, size:1})).data?.total || 0 } catch {}
    data.experts = d.byCategory ? Object.values(d.byCategory).reduce((a,b)=>a+b,0) : 0
    data.todayReviews = 0
    data.competitionProjectMap = []
    data.statusDistribution = []
  } catch {
    data.competitions = 0
    data.projects = 0
    data.experts = 0
    data.todayReviews = 0
  } finally {
    loading.value = false
    await nextTick()
    renderCharts()
  }
}

function renderCharts() {
  if (!barRef.value || !pieRef.value) return
  barChart = echarts.init(barRef.value)
  pieChart = echarts.init(pieRef.value)

  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { top: 20, right: 20, bottom: 40, left: 40 },
    xAxis: {
      type: 'category',
      data: data.competitionProjectMap.map(c => c.name || c.title || ''),
      axisLabel: { interval: 0, rotate: 20, fontSize: 11 }
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: data.competitionProjectMap.map(c => c.count || c.value || 0),
      itemStyle: { color: '#3b82f6', borderRadius: [4, 4, 0, 0] },
      barWidth: '50%'
    }]
  })

  const statusColors = { '待审核': '#f59e0b', '审核中': '#3b82f6', '已通过': '#10b981', '已驳回': '#ef4444', '草稿': '#94a3b8' }
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: true,
      label: { show: true, formatter: '{b}\n{d}%' },
      data: data.statusDistribution.map(s => ({
        name: s.name || s.status,
        value: s.count || s.value || 0,
        itemStyle: { color: statusColors[s.name || s.status] || '#3b82f6' }
      }))
    }]
  })
}

function handleResize() {
  barChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border-top: 4px solid;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  margin-bottom: 20px;
}
.stat-label {
  color: #64748b;
  font-size: 14px;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-top: 6px;
}
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 12px;
}
.chart-area {
  height: 320px;
}
</style>
