<template>
  <div class="page-card">
    <div class="page-header">
      <h3>公告管理</h3>
      <el-button type="primary" :icon="Plus" @click="openDialog()">发布公告</el-button>
    </div>

    <el-table v-loading="loading" :data="list" stripe border>
      <el-table-column prop="title" label="标题" min-width="240" show-overflow-tooltip>
        <template #default="{ row }">
          <el-icon v-if="row.top" style="color:#f59e0b;margin-right:4px"><StarFilled /></el-icon>
          {{ row.title }}
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ row.category || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishedAt" label="发布时间" width="170">
        <template #default="{ row }">{{ formatDate(row.publishedAt || row.createdAt) }}</template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布人" width="120" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" link type="warning" @click="handleToggleTop(row)">
            {{ row.top ? '取消置顶' : '置顶' }}
          </el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width:100%" placeholder="请选择">
            <el-option label="通知公告" value="通知公告" />
            <el-option label="赛事信息" value="赛事信息" />
            <el-option label="政策文件" value="政策文件" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.top" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" maxlength="5000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { page as fetchPage, create, update, remove } from '../api/announcement'

const loading = ref(false)
const list = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const saving = ref(false)
const form = reactive({ id: null, title: '', category: '', top: false, content: '' })

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
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

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    Object.assign(form, { ...row })
  } else {
    Object.assign(form, { id: null, title: '', category: '', top: false, content: '' })
  }
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit.value) {
      await update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await create(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除公告「${row.title}」？`, '提示', { type: 'error' })
  await remove(row.id)
  ElMessage.success('删除成功')
  load()
}

async function handleToggleTop(row) {
  await update(row.id, { ...row, top: !row.top })
  ElMessage.success('操作成功')
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
.page-header h3 { margin: 0; color: #1e293b; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
