<template>
  <div class="page-card">
    <div class="page-header">
      <h3>项目评审</h3>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-loading="loading" v-if="project" class="review-layout">
      <div class="detail-section">
        <h2>{{ project.title }}</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="团队">{{ project.teamName }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ project.leaderName }}</el-descriptions-item>
          <el-descriptions-item label="所属大赛">{{ project.competitionName }}</el-descriptions-item>
          <el-descriptions-item label="指导教师">{{ project.teacher || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ formatDate(project.submitTime) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(project.status)">{{ statusLabel(project.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目简介" :span="2">{{ project.summary || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="attachments.length" class="section-title">附件</div>
        <div class="attachments">
          <div v-for="(f, i) in attachments" :key="i" class="attachment-item">
            <el-icon><Document /></el-icon>
            <a :href="f.url || f" target="_blank">{{ f.name || getFileName(f.url || f) }}</a>
          </div>
        </div>
      </div>

      <div class="action-section">
        <el-card shadow="never">
          <template #header><b>审核决策</b></template>
          <el-form label-width="80px">
            <el-form-item label="结果">
              <el-radio-group v-model="decision">
                <el-radio value="APPROVE">通过</el-radio>
                <el-radio value="REJECT">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="原因">
              <el-input v-model="reason" type="textarea" :rows="4" placeholder="请输入审核原因/意见" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" :loading="submitting" @click="handleApprove">确认通过</el-button>
              <el-button type="danger" :loading="submitting" @click="handleReject">确认驳回</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { get, reviewDecision } from '../api/project'

const route = useRoute()
const router = useRouter()

const project = ref(null)
const loading = ref(false)
const submitting = ref(false)
const decision = ref('APPROVE')
const reason = ref('')

const attachments = computed(() => {
  if (!project.value) return []
  return project.value.attachments || project.value.files || []
})

function statusLabel(s) {
  const map = { DRAFT: '草稿', SUBMITTED: '待审核', REVIEWING: '审核中', APPROVED: '已通过', REJECTED: '已驳回' }
  return map[s] || s || '-'
}
function statusTagType(s) {
  const map = { DRAFT: 'info', SUBMITTED: 'warning', REVIEWING: '', APPROVED: 'success', REJECTED: 'danger' }
  return map[s] || ''
}
function formatDate(d) {
  if (!d) return '-'
  return d.replace('T', ' ').slice(0, 16)
}
function getFileName(url) {
  return url.split('/').pop() || '附件'
}

async function load() {
  loading.value = true
  try {
    const res = await get(route.query.id)
    project.value = res.data || res
  } finally {
    loading.value = false
  }
}

async function doReview(decisionVal) {
  await ElMessageBox.confirm(decisionVal === 'APPROVE' ? '确认通过此项目？' : '确认驳回此项目？', '提示', { type: 'warning' })
  submitting.value = true
  try {
    await reviewDecision(route.query.id, { decision: decisionVal, reason: reason.value })
    ElMessage.success('审核提交成功')
    router.push('/projects')
  } finally {
    submitting.value = false
  }
}
function handleApprove() { doReview('APPROVE') }
function handleReject() { doReview('REJECT') }

onMounted(load)
</script>

<style scoped>
.page-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h3 {
  margin: 0;
  color: #1e293b;
}
.review-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
}
@media (max-width: 900px) {
  .review-layout { grid-template-columns: 1fr; }
}
.section-title {
  font-weight: 600;
  margin: 20px 0 10px;
  color: #1e293b;
}
.attachments {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 4px;
}
.attachment-item a {
  color: #3b82f6;
  text-decoration: none;
}
</style>
