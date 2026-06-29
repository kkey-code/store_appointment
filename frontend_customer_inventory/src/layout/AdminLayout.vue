<template>
  <el-container class="admin-shell">
    <el-aside width="220px" class="sidebar">
      <div class="brand">
        <el-icon><Shop /></el-icon>
        <span>门店资料后台</span>
      </div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div class="crumb">{{ currentTitle }}</div>
        <div class="user">
          <el-tag v-if="isReadonly" type="warning">只读</el-tag>
          <span>{{ userName }}</span>
          <el-button text type="primary" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menus = [
  { path: '/customers', title: '客户档案', icon: 'User' },
  { path: '/inventory', title: '库存管理', icon: 'Goods' }
]

const currentTitle = computed(() => String(route.meta.title || '客户档案'))
const userName = computed(() => localStorage.getItem('store_admin_name') || '管理员')
const isReadonly = computed(() => localStorage.getItem('store_admin_role') === 'readonly')

const logout = () => {
  localStorage.removeItem('store_admin_token')
  localStorage.removeItem('store_admin_name')
  localStorage.removeItem('store_admin_role')
  router.replace('/login')
}
</script>

<style scoped>
.admin-shell {
  min-height: 100vh;
  background: #f5f7fb;
}

.sidebar {
  background: linear-gradient(180deg, #111827 0%, #13201e 100%);
  color: #fff;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
}

.brand {
  height: 64px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  font-weight: 700;
  font-size: 17px;
  letter-spacing: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.brand .el-icon {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  color: #ccfbf1;
  background: rgba(20, 184, 166, 0.18);
}

.menu {
  border-right: 0;
  background: transparent;
  padding: 12px;
}

.menu :deep(.el-menu-item) {
  height: 44px;
  margin: 4px 0;
  border-radius: 8px;
  color: #cbd5e1;
  font-weight: 600;
}

.menu :deep(.el-menu-item .el-icon) {
  color: #94a3b8;
}

.menu :deep(.el-menu-item.is-active),
.menu :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(15, 118, 110, 0.9);
}

.menu :deep(.el-menu-item.is-active .el-icon),
.menu :deep(.el-menu-item:hover .el-icon) {
  color: #fff;
}

.topbar {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid #dde4ee;
  backdrop-filter: blur(10px);
}

.crumb {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.user {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #475467;
  font-weight: 600;
}

.main {
  padding: 0;
  background: #f5f7fb;
}
</style>
