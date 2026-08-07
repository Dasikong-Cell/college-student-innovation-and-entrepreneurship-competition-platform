<template>
  <div class="page-card">
    <div class="page-header">
      <h3>项目审核</h3>
    </div>

    <div class="filter-bar">
      <el-select v-model="filters.competitionId" placeholder="全部大赛" clearable style="width:180px" @change="load">
        <el-option v-for="c in competitions" :key="c.id" :label="c.title" :value="c.id" />
      </el-select>
      <el-select v-model="filters.status" placeholder="全部状态" clearable style="width:150px" @change="load">
        <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="搜索项目/团队/负责人" style="width:260px" clearable @keyup.enter="load" @clear="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" :icon="Search" @click="load">搜索</el-button>
    </div>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column prop="title" label="项目名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="teamName" label="团队名称" width="140" />
      <el-table-column prop="leaderName" label="负责人" width="100" />
      <el-table-column prop="competitionName" label="所属大赛" width="160" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="170">
        <template #default="{ row }">{{ formatDate(row.submitTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="viewDetail(row)">查看</el-button>
          <el-button size="small" link type="success" @click="openReview(row)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="load"
        @size-change="load"
      />
    </div>

    <el-dialog v-model="detailVisible" title="项目详情" width="720px">
      <div v-if="current" class="detail-wrap">
        <h3>{{ current.title }}</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="团队">{{ current.teamName }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ current.leaderName }}（{{ current.leaderPhone }}）</el-descriptions-item>
          <el-descriptions-item label="所属大赛">{{ current.competitionName }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDate(current.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(current.status)">{{ statusLabel(current.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="指导教师">{{ current.teacher || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目简介" :span="2">{{ current.summary || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <el-dialog v-model="reviewVisible" title="项目审核" width="520px">
      <div class="review-tip">审核「<b>{{ current?.title }}</b>」</div>
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.decision">
            <el-radio value="APPROVE">通过</el-radio>
            <el-radio value="REJECT">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.reason" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { page as fetchPage, get, reviewDecision } from '../api/project'
import { page as competitionPage } from '../api/competition'

const loading = ref(false)
const list = ref([])
const competitions = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const filters = reactive({ competitionId: null, status: '', keyword: '' })

const statusOptions = [
  { value: 'DRAFT', label: '草稿' },
  { value: 'SUBMITTED', label: '待审核' },
  { value: 'REVIEWING', label: '审核中' },
  { value: 'APPROVED', label: '已通过' },
  { value: 'REJECTED', label: '已驳回' }
]

const detailVisible = ref(false)
const reviewVisible = ref(false)
const current = ref(null)
const submitting = ref(false)
const reviewForm = reactive({ decision: 'APPROVE', reason: '' })

function statusLabel(s) {
  return statusOptions.find(o => o.value === s)?.label || s || '-'
}
function statusTagType(s) {
  const map = { DRAFT: 'info', SUBMITTED: 'warning', REVIEWING: '', APPROVED: 'success', REJECTED: 'danger' }
  return map[s] || ''
}
function formatDate(d) {
  if (!d) return '-'
  return d.replace('T', ' ').slice(0, 16)
}

async function load() {
  loading.value = true
  try {
    const params = { page: pageNum.value, size: pageSize.value }
    if (filters.competitionId) params.competitionId = filters.competitionId
    if (filters.status) params.status = filters.status
    if (filters.keyword) params.keyword = filters.keyword
    const res = await fetchPage(params)
    const d = res.data || res
    list.value = d.records || d.list || []
    total.value = d.total || 0
  } finally {
    loading.value = false
  }
}

async function loadCompetitions() {
  try {
    const res = await competitionPage({ page: 1, size: 200 })
    const d = res.data || res
    competitions.value = d.records || d.list || []
  } catch {}
}

async function viewDetail(row) {
  current.value = row
  try {
    const res = await get(row.id)
    current.value = res.data || res
  } catch {}
  detailVisible.value = true
}

function openReview(row) {
  current.value = row
  reviewForm.decision = 'APPROVE'
  reviewForm.reason = ''
  reviewVisible.value = true
}

async function submitReview() {
  submitting.value = true
  try {
    await reviewDecision(current.value.id, reviewForm)
    ElMessage.success('审核完成')
    reviewVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  load()
  loadCompetitions()
})
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
.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
.review-tip {
  margin-bottom: 12px;
  color: #475569;
}
.detail-wrap h3 {
  margin-top: 0;
}
</style>
