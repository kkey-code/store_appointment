<template>
  <div class="login-page">
    <section class="login-visual">
      <div class="visual-content">
        <h1>门店预约管理系统</h1>
        <p>客户、服务、预约、订单与经营数据一体化管理</p>
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
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 460px;
  background: #111827;
}

.login-visual {
  background:
    linear-gradient(120deg, rgba(17, 24, 39, 0.82), rgba(15, 118, 110, 0.54)),
    url("https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=1600&q=80");
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: flex-end;
  padding: 72px;
}

.visual-content {
  color: #fff;
}

.visual-content h1 {
  margin: 0 0 16px;
  font-size: 46px;
  letter-spacing: 0;
}

.visual-content p {
  margin: 0;
  font-size: 18px;
  color: #d7f7ef;
}

.login-panel {
  background: #f5f7fb;
  display: flex;
  align-items: center;
  padding: 48px;
}

.login-panel form {
  width: 100%;
  padding: 34px;
  background: #fff;
  border: 1px solid #dde4ee;
  border-radius: 8px;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.12);
}

.login-panel h2 {
  margin: 0 0 28px;
  font-size: 26px;
  color: #111827;
  letter-spacing: 0;
}

.login-button {
  width: 100%;
}
</style>
