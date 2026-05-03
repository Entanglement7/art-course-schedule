<template>
  <div class="login-container">
    <div class="login-content">
      <n-card class="login-card" :bordered="false">
        <div class="login-header">
          <h1 class="logo-text">艺术排课系统</h1>
          <p class="subtitle">Art Course Scheduling System</p>
        </div>

        <!-- 标签切换 -->
        <n-tabs v-model:value="activeTab" type="line" animated justify-content="space-evenly">
          <n-tab-pane name="login" tab="登录">
            <n-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
              <n-form-item path="username">
                <n-input
                  v-model:value="loginForm.username"
                  placeholder="请输入用户名"
                  :input-props="{ autocomplete: 'username' }"
                >
                  <template #prefix><n-icon :component="PersonOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item path="password">
                <n-input
                  v-model:value="loginForm.password"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入密码"
                  :input-props="{ autocomplete: 'current-password' }"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item>
                <n-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  block
                  @click="handleLogin"
                  :style="{ background: '#7C3AED', border: 'none' }"
                >
                  登录
                </n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>

          <n-tab-pane name="register" tab="注册">
            <n-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
              <n-form-item path="name">
                <n-input
                  v-model:value="registerForm.name"
                  placeholder="请输入姓名"
                >
                  <template #prefix><n-icon :component="PersonOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item path="username">
                <n-input
                  v-model:value="registerForm.username"
                  placeholder="请输入用户名（3-20位）"
                  :input-props="{ autocomplete: 'username' }"
                >
                  <template #prefix><n-icon :component="PersonCircleOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item path="password">
                <n-input
                  v-model:value="registerForm.password"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入密码（6-30位）"
                  :input-props="{ autocomplete: 'new-password' }"
                >
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item path="confirmPassword">
                <n-input
                  v-model:value="registerForm.confirmPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请再次输入密码"
                  :input-props="{ autocomplete: 'new-password' }"
                  @keyup.enter="handleRegister"
                >
                  <template #prefix><n-icon :component="LockClosedOutline" /></template>
                </n-input>
              </n-form-item>

              <n-form-item path="role" label="注册身份">
                <n-radio-group v-model:value="registerForm.role">
                  <n-space>
                    <n-radio value="student">学生</n-radio>
                    <n-radio value="teacher">教师</n-radio>
                  </n-space>
                </n-radio-group>
              </n-form-item>

              <n-form-item>
                <n-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  block
                  @click="handleRegister"
                  :style="{ background: '#7C3AED', border: 'none' }"
                >
                  注册
                </n-button>
              </n-form-item>
            </n-form>
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { PersonOutline, PersonCircleOutline, LockClosedOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { login as apiLogin, register as apiRegister } from '@/api/auth'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const activeTab = ref<'login' | 'register'>('login')
const loading = ref(false)

const loginFormRef = ref()
const loginForm = ref({ username: '', password: '' })
const loginRules = {
  username: { required: true, message: '请输入用户名', trigger: 'blur' },
  password: { required: true, message: '请输入密码', trigger: 'blur' },
}

const registerFormRef = ref()
const registerForm = ref({ name: '', username: '', password: '', confirmPassword: '', role: 'student' })
const registerRules = {
  name: { required: true, message: '请输入姓名', trigger: 'blur' },
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  confirmPassword: {
    required: true,
    trigger: 'blur',
    validator: (_rule: unknown, value: string) =>
      value === registerForm.value.password ? true : new Error('两次密码不一致'),
  },
}

async function handleLogin() {
  try {
    await loginFormRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const res = await apiLogin(loginForm.value) as any
    localStorage.setItem('token', res.token)
    userStore.setUserInfo(res.userInfo)
    message.success(`欢迎回来，${res.userInfo.name}`)
    router.push('/dashboard')
  } catch (err: any) {
    message.error(err.message || '登录失败')
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  try {
    await registerFormRef.value?.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const res = await apiRegister({
      name: registerForm.value.name,
      username: registerForm.value.username,
      password: registerForm.value.password,
      role: registerForm.value.role,
    }) as any
    localStorage.setItem('token', res.token)
    userStore.setUserInfo(res.userInfo)
    message.success('注册成功，欢迎加入！')
    router.push('/dashboard')
  } catch (err: any) {
    message.error(err.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.login-content {
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 40px 32px;
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.logo-text {
  font-size: 28px;
  font-weight: bold;
  background: #7C3AED;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px;
}

.subtitle {
  color: #6B7280;
  font-size: 14px;
  margin: 0;
  letter-spacing: 1px;
}

@media (max-width: 768px) {
  .login-content {
    max-width: 100%;
  }
  .login-card {
    padding: 32px 20px;
  }
  .logo-text {
    font-size: 24px;
  }
}
</style>
