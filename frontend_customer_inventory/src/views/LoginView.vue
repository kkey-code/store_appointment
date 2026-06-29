<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="visual-card">
        <div class="brand-mark">店</div>
        <h1>门店资料后台</h1>
        <p>客户档案 / 库存管理</p>
      </div>
    </section>
    <section class="login-panel">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <h2>后台登录</h2>
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" size="large" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" size="large" show-password clearable />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { login } from '@/api/modules'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456'
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const user = await login(form.username, form.password)
    localStorage.setItem('store_admin_token', user.token)
    localStorage.setItem('store_admin_name', user.userName || user.username || form.username)
    localStorage.setItem('store_admin_role', user.role || '')
    ElMessage.success('登录成功')
    router.replace('/customers')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  background: #111827;
}

.login-visual {
  position: relative;
  display: flex;
  align-items: flex-end;
  padding: 72px;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(17, 24, 39, 0.94), rgba(15, 118, 110, 0.82)),
    repeating-linear-gradient(90deg, rgba(255, 255, 255, 0.07) 0, rgba(255, 255, 255, 0.07) 1px, transparent 1px, transparent 96px);
}

.visual-card {
  color: #fff;
}

.brand-mark {
  width: 52px;
  height: 52px;
  display: grid;
  place-items: center;
  margin-bottom: 18px;
  border: 1px solid rgba(204, 251, 241, 0.42);
  border-radius: 8px;
  color: #ccfbf1;
  background: rgba(255, 255, 255, 0.1);
  font-size: 24px;
  font-weight: 800;
}

.visual-card h1 {
  margin: 0 0 14px;
  font-size: 46px;
  letter-spacing: 0;
}

.visual-card p {
  margin: 0;
  color: #d7f7ef;
  font-size: 18px;
}

.login-panel {
  display: flex;
  align-items: center;
  padding: 48px;
  background: #f5f7fb;
}

.login-panel form {
  width: 100%;
  padding: 34px;
  border: 1px solid #dde4ee;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.12);
}

.login-panel h2 {
  margin: 0 0 28px;
  color: #111827;
  font-size: 26px;
  letter-spacing: 0;
}

.login-button {
  width: 100%;
}
</style>
