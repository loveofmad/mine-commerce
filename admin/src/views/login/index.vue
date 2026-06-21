<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-gradient"></div>
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">M</div>
        <h2 class="login-title">土特产商城</h2>
        <p class="login-subtitle">后台管理系统</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>请输入管理员账号密码</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/index'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/admin/auth/login', {
      username: form.username,
      password: form.password
    })
    userStore.setToken(res.data?.token || 'admin-token')
    userStore.setUserInfo({ username: form.username })
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '用户名或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%);
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 30% 50%, rgba(74, 144, 164, 0.15) 0%, transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(224, 122, 95, 0.1) 0%, transparent 40%);
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: #4A90A4;
  top: -200px;
  right: -100px;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: #6BB0C4;
  bottom: -150px;
  left: -100px;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: #E07A5F;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.login-card {
  width: 420px;
  padding: 48px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #4A90A4 0%, #6BB0C4 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  margin: 0 auto 20px;
  box-shadow: 0 8px 24px rgba(74, 144, 164, 0.35);
}

.login-title {
  margin: 0 0 6px 0;
  color: #1F2937;
  font-size: 28px;
  font-weight: 700;
}

.login-subtitle {
  margin: 0;
  color: #9CA3AF;
  font-size: 15px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #4A90A4 0%, #6BB0C4 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(74, 144, 164, 0.35);
}

.login-btn:hover {
  background: linear-gradient(135deg, #3A7A8E 0%, #5BA0B4 100%);
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  color: #9CA3AF;
  font-size: 13px;
}
</style>
