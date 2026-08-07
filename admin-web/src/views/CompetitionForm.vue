<template>
  <div class="page-card">
    <div class="page-header">
      <h3>{{ isEdit ? '编辑大赛' : '新建大赛' }}</h3>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-form v-loading="loading" ref="formRef" :model="form" :rules="rules" label-width="110px" class="form-area">
      <el-form-item label="大赛名称" prop="title">
        <el-input v-model="form.title" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="大赛描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="5" maxlength="2000" show-word-limit />
      </el-form-item>
      <el-form-item label="大赛类别" prop="category">
        <el-select v-model="form.category" placeholder="请选择" style="width:100%">
          <el-option label="创新创业" value="创新创业" />
          <el-option label="互联网+" value="互联网+" />
          <el-option label="挑战杯" value="挑战杯" />
          <el-option label="学科竞赛" value="学科竞赛" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图">
        <el-input v-model="form.coverImage" placeholder="图片URL（可选）" />
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
      </el-form-item>
      <el-form-item label="报名开始" prop="registerStart">
        <el-date-picker v-model="form.registerStart" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
      </el-form-item>
      <el-form-item label="报名截止" prop="registerEnd">
        <el-date-picker v-model="form.registerEnd" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { get, create, update } from '../api/competition'

const route = useRoute()
const router = useRouter()

const isEdit = !!route.query.id
const formRef = ref()
const loading = ref(false)
const saving = ref(false)

const form = reactive({
  title: '',
  description: '',
  category: '',
  coverImage: '',
  startTime: '',
  endTime: '',
  registerStart: '',
  registerEnd: ''
})

const rules = {
  title: [{ required: true, message: '请输入大赛名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入大赛描述', trigger: 'blur' }],
  category: [{ required: true, message: '请选择大赛类别', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  registerStart: [{ required: true, message: '请选择报名开始时间', trigger: 'change' }],
  registerEnd: [{ required: true, message: '请选择报名截止时间', trigger: 'change' }]
}

async function loadDetail() {
  if (!isEdit) return
  loading.value = true
  try {
    const res = await get(route.query.id)
    Object.assign(form, res.data || res)
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit) {
      await update(route.query.id, form)
      ElMessage.success('更新成功')
    } else {
      await create(form)
      ElMessage.success('创建成功')
    }
    goBack()
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push('/competitions')
}

onMounted(loadDetail)
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
.form-area {
  max-width: 720px;
}
</style>
