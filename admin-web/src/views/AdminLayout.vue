<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">大创管理后台</span>
        <span v-else>大创</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#1e293b"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/competitions">
          <el-icon><Trophy /></el-icon>
          <template #title>大赛管理</template>
        </el-menu-item>
        <el-menu-item index="/projects">
          <el-icon><Document /></el-icon>
          <template #title>项目审核</template>
        </el-menu-item>
        <el-menu-item index="/review">
          <el-icon><EditPen /></el-icon>
          <template #title>专家评审</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/announcements">
          <el-icon><Bell /></el-icon>
          <template #title>公告管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32">{{ userStore.userName?.[0] || 'U' }}</el-avatar>
              <span class="username">{{ userStore.userName || '用户' }}</span>
              <el-tag size="small" type="info" style="margin-left:8px">{{ userStore.role }}</el-tag>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const titleMap = {
  '/dashboard': '仪表盘',
  '/competitions': '大赛管理',
  '/competitions/new': '新建大赛',
  '/projects': '项目审核',
  '/projects/review': '项目评审',
  '/review': '专家评审',
  '/users': '用户管理',
  '/announcements': '公告管理'
}
const activeMenu = computed(() => {
  if (route.path.startsWith('/competitions')) return '/competitions'
  if (route.path.startsWith('/projects')) return '/projects'
  return route.path
})
const currentTitle = computed(() => titleMap[route.path] || '')

function handleCommand(cmd) {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' }).then(() => {
      userStore.logout()
      ElMessage.success('已退出')
      router.push('/login')
    }).catch(() => {})
  }
}

function handleResize() {
  isCollapse.value = window.innerWidth < 768
}
onMounted(handleResize)
onUnmounted(() => window.removeEventListener('resize', handleResize))
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.sidebar {
  background: #1e293b;
  transition: width 0.2s;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  background: #0f172a;
  white-space: nowrap;
}
.sidebar :deep(.el-menu) {
  border-right: none;
}
.sidebar :deep(.el-menu-item.is-active) {
  background: #3b82f6 !important;
  color: #fff !important;
}
.header {
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #475569;
}
.collapse-btn:hover {
  color: #3b82f6;
}
.title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.username {
  margin-left: 8px;
  color: #334155;
}
.main-content {
  background: #f1f5f9;
  padding: 20px;
  overflow-y: auto;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.15s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
