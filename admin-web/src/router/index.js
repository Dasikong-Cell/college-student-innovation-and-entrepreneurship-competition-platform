import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../views/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'competitions', name: 'CompetitionList', component: () => import('../views/CompetitionList.vue') },
      { path: 'competitions/new', name: 'CompetitionForm', component: () => import('../views/CompetitionForm.vue') },
      { path: 'projects', name: 'ProjectList', component: () => import('../views/ProjectList.vue') },
      { path: 'projects/review', name: 'ProjectReview', component: () => import('../views/ProjectReview.vue') },
      { path: 'review', name: 'ReviewPanel', component: () => import('../views/ReviewPanel.vue') },
      { path: 'users', name: 'UserManagement', component: () => import('../views/UserManagement.vue') },
      { path: 'announcements', name: 'AnnouncementList', component: () => import('../views/AnnouncementList.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
