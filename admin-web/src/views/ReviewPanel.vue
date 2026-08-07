<template>
  <div class="page-card">
    <div class="page-header">
      <h3>专家评审</h3>
    </div>

    <div v-if="list.length === 0 && !loading" class="empty-tip">暂无待评审项目</div>

    <el-row :gutter="16">
      <el-col v-for="item in list" :key="item.id" :xs="24" :md="12" :lg="8">
        <el-card class="review-card" shadow="hover">
          <div class="card-title">{{ item.title }}</div>
          <div class="card-sub">
            <span>{{ item.teamName }}</span>
            <el-tag size="small" :type="item.reviewed ? 'success' : 'warning'">{{ item.reviewed ? '已评审' : '待评审' }}</el-tag>
          </div>
          <el-descriptions :column="1" size="small" border style="margin-top:12px">
            <el-descriptions-item label="所属大赛">{{ item.competitionName }}</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ item.leaderName }}</el-descriptions-item>
          </el-descriptions>

          <el-form v-if="!item.reviewed" :model="item.form" label-width="72px" size="small" class="score-form">
            <el-form-item label="创新性">
              <el-input-number v-model="item.form.innovation" :min="0" :max="10" controls-position="right" />
            </el-form-item>
            <el-form-item label="可行性">
              <el-input-number v-model="item.form.feasibility" :min="0" :max="10" controls-position="right" />
            </el-form-item>
            <el-form-item label="团队">
              <el-input-number v-model="item.form.team" :min="0" :max="10" controls-position="right" />
            </el-form-item>
            <el-form-item label="展示">
              <el-input-number v-model="item.form.presentation" :min="0" :max="10" controls-position="right" />
            </el-form-item>
            <el-form-item label="总分">
              <span class="score-total">{{ scoreTotal(item.form) }}</span> / 40
            </el-form-item>
            <el-form-item label="评语">
              <el-input v-model="item.form.comment" type="textarea" :rows="2" placeholder="请输入评语" />
            </el-form-item>
            <el-button type="primary" size="small" :loading="item.submitting" style="width:100%" @click="submitItem(item)">提交评审</el-button>
          </el-form>
          <div v-else class="reviewed-info">
            <div>总分：<b>{{ item.scoreTotal ?? scoreTotal(item.form) }}</b> / 40</div>
            <div v-if="item.comment" class="comment-text">评语：{{ item.comment }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { byExpert, submitReview } from '../api/review'

const list = ref([])
const loading = ref(false)

function scoreTotal(f) {
  return (f.innovation || 0) + (f.feasibility || 0) + (f.team || 0) + (f.presentation || 0)
}

async function load() {
  loading.value = true
  try {
    const res = await byExpert()
    const d = res.data || res
    const arr = Array.isArray(d) ? d : (d.records || d.list || [])
    list.value = arr.map(item => ({
      ...item,
      reviewed: !!item.reviewed,
      submitting: false,
      form: item.form || {
        innovation: item.innovation ?? 0,
        feasibility: item.feasibility ?? 0,
        team: item.team ?? 0,
        presentation: item.presentation ?? 0,
        comment: item.comment || ''
      }
    }))
  } finally {
    loading.value = false
  }
}

async function submitItem(item) {
  item.submitting = true
  try {
    await submitReview({
      projectId: item.id,
      innovation: item.form.innovation,
      feasibility: item.form.feasibility,
      team: item.form.team,
      presentation: item.form.presentation,
      comment: item.form.comment
    })
    item.reviewed = true
    item.scoreTotal = scoreTotal(item.form)
    item.comment = item.form.comment
    ElMessage.success('评审提交成功')
  } finally {
    item.submitting = false
  }
}

onMounted(load)
</script>

<style scoped>
.page-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.page-header h3 {
  margin: 0 0 16px;
  color: #1e293b;
}
.review-card {
  margin-bottom: 16px;
}
.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-sub {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
}
.score-form {
  margin-top: 12px;
}
.score-form :deep(.el-form-item) {
  margin-bottom: 8px;
}
.score-total {
  color: #3b82f6;
  font-weight: 700;
  font-size: 18px;
}
.reviewed-info {
  margin-top: 12px;
  padding: 12px;
  background: #f0fdf4;
  border-radius: 4px;
  font-size: 13px;
  color: #166534;
}
.comment-text {
  margin-top: 6px;
  color: #475569;
}
.empty-tip {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}
</style>
