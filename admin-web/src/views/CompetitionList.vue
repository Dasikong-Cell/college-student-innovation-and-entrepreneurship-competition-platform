<template>
  <div class="page-card">
    <div class="page-header">
      <h3>大赛管理</h3>
      <el-button type="primary" :icon="Plus" @click="goCreate">新建大赛</el-button>
    </div>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column prop="title" label="大赛名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="category" label="类别" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ row.category || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="registerStart" label="报名开始" width="170">
        <template #default="{ row }">{{ formatDate(row.registerStart) }}</template>
      </el-table-column>
      <el-table-column prop="registerEnd" label="报名截止" width="170">
        <template #default="{ row }">{{ formatDate(row.registerEnd) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="goEdit(row)">编辑</el-button>
          <el-button v-if="row.status !== 'published'" size="small" link type="success" @click="handlePublish(row)">发布</el-button>
          <el-button v-else size="small" link type="warning" @click="handleTakeDown(row)">下架</el-button>
          <el-button size="small" link type="danger" @click="handleDelete(row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { page as fetchPage, remove, publish, takeDown } from '../api/competition'

const router = useRouter()

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

function statusLabel(s) {
  const map = { draft: '草稿', published: '已发布', judging: '评审中', finished: '已结束' }
  return map[s] || s || '-'
}
function statusTagType(s) {
  const map = { draft: 'info', published: 'success', judging: 'warning', finished: '' }
  return map[s] || ''
}
function formatDate(d) {
  if (!d) return '-'
  return d.replace('T', ' ').slice(0, 16)
}

async function load() {
  loading.value = true
  try {
    const res = await fetchPage({ page: pageNum.value, size: pageSize.value })
    const d = res.data || res
    list.value = d.records || d.list || []
    total.value = d.total || 0
  } finally {
    loading.value = false
  }
}

function goCreate() {
  router.push('/competitions/new')
}
function goEdit(row) {
  router.push({ path: '/competitions/new', query: { id: row.id } })
}

async function handlePublish(row) {
  await ElMessageBox.confirm(`确认发布「${row.title}」？`, '提示', { type: 'warning' })
  await publish(row.id)
  ElMessage.success('发布成功')
  load()
}
async function handleTakeDown(row) {
  await ElMessageBox.confirm(`确认下架「${row.title}」？`, '提示', { type: 'warning' })
  await takeDown(row.id)
  ElMessage.success('下架成功')
  load()
}
async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除「${row.title}」？该操作不可恢复`, '提示', { type: 'error' })
  await remove(row.id)
  ElMessage.success('删除成功')
  load()
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
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
