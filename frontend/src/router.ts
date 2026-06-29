import { createRouter, createWebHistory } from 'vue-router'
import AdminLayout from '@/layout/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '工作台', icon: 'DataBoard' } },
        { path: 'employees', component: () => import('@/views/EmployeeView.vue'), meta: { title: '员工管理', icon: 'UserFilled' } },
        { path: 'customers', component: () => import('@/views/CustomerView.vue'), meta: { title: '客户管理', icon: 'User' } },
        { path: 'service-items', component: () => import('@/views/ServiceItemView.vue'), meta: { title: '服务项目', icon: 'Shop' } },
        { path: 'appointments', component: () => import('@/views/AppointmentView.vue'), meta: { title: '预约管理', icon: 'Calendar' } },
        { path: 'orders', component: () => import('@/views/OrderView.vue'), meta: { title: '订单管理', icon: 'Tickets' } },
        { path: 'statistics', component: () => import('@/views/StatisticsView.vue'), meta: { title: '数据统计', icon: 'TrendCharts' } }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('store_admin_token')
  if (!to.meta.public && !token) {
    return '/login'
  }
})

export default router
