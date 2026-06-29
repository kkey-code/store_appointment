import { createRouter, createWebHashHistory } from 'vue-router'
import AdminLayout from '@/layout/AdminLayout.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: AdminLayout,
      redirect: '/customers',
      children: [
        { path: 'customers', component: () => import('@/views/CustomerView.vue'), meta: { title: '客户档案', icon: 'User' } },
        { path: 'inventory', component: () => import('@/views/InventoryView.vue'), meta: { title: '库存管理', icon: 'Goods' } }
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
