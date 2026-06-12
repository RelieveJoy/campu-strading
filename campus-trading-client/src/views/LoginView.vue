<template>
  <div class="login-page">
    <!-- ── Decorative Background ── -->
    <div class="decor-bg">
      <div class="decor-circle c1"></div>
      <div class="decor-circle c2"></div>
      <div class="decor-circle c3"></div>
    </div>

    <!-- ── Card ── -->
    <div class="auth-card">
      <div class="card-header">
        <div class="logo-mark">🎒</div>
        <h1>操场集市</h1>
        <p class="subtitle">淘到好物，遇见校友</p>
      </div>

      <div class="tab-switch">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >登录</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >注册</button>
        <div class="tab-indicator" :class="{ right: activeTab === 'register' }"></div>
      </div>

      <!-- ── Login Form ── -->
      <form v-show="activeTab === 'login'" class="auth-form" @submit.prevent="handleLogin">
        <div class="field">
          <label class="field-label" for="login-id">学号</label>
          <input id="login-id" v-model="loginForm.studentId" type="text" class="field-input"
            placeholder="请输入学号" autocomplete="username" />
          <span class="field-error" v-if="loginErrors.studentId">{{ loginErrors.studentId }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="login-pw">密码</label>
          <div class="pw-wrap">
            <input :id="loginShow ? 'login-pw-text' : 'login-pw'" v-model="loginForm.password"
              :type="loginShow ? 'text' : 'password'" class="field-input"
              placeholder="请输入密码" autocomplete="current-password" />
            <button type="button" class="pw-toggle" @click="loginShow = !loginShow"
              :aria-label="loginShow ? '隐藏密码' : '显示密码'">
              <svg v-if="loginShow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M1 1l22 22"/>
                <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <span class="field-error" v-if="loginErrors.password">{{ loginErrors.password }}</span>
        </div>
        <button type="submit" class="btn-submit" :disabled="loginLoading">
          <span v-if="loginLoading" class="spinner"></span>
          {{ loginLoading ? '登录中...' : '登 录' }}
        </button>
      </form>

      <!-- ── Register Form ── -->
      <form v-show="activeTab === 'register'" class="auth-form" @submit.prevent="handleRegister">
        <div class="field">
          <label class="field-label" for="reg-id">学号</label>
          <input id="reg-id" v-model="registerForm.studentId" type="text" class="field-input"
            placeholder="请输入学号" autocomplete="off" />
          <span class="field-error" v-if="registerErrors.studentId">{{ registerErrors.studentId }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="reg-name">姓名</label>
          <input id="reg-name" v-model="registerForm.username" type="text" class="field-input"
            placeholder="请输入真实姓名" autocomplete="name" />
          <span class="field-error" v-if="registerErrors.username">{{ registerErrors.username }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="reg-phone">手机号</label>
          <input id="reg-phone" v-model="registerForm.phone" type="tel" class="field-input"
            placeholder="请输入手机号" autocomplete="tel" />
          <span class="field-error" v-if="registerErrors.phone">{{ registerErrors.phone }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="reg-pw">密码</label>
          <div class="pw-wrap">
            <input :id="regShow ? 'reg-pw-text' : 'reg-pw'" v-model="registerForm.password"
              :type="regShow ? 'text' : 'password'" class="field-input"
              placeholder="至少6位密码" autocomplete="new-password" />
            <button type="button" class="pw-toggle" @click="regShow = !regShow"
              :aria-label="regShow ? '隐藏密码' : '显示密码'">
              <svg v-if="regShow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M1 1l22 22"/>
                <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <span class="field-error" v-if="registerErrors.password">{{ registerErrors.password }}</span>
        </div>
        <button type="submit" class="btn-submit" :disabled="registerLoading">
          <span v-if="registerLoading" class="spinner"></span>
          {{ registerLoading ? '注册中...' : '注 册' }}
        </button>
      </form>

      <p class="card-footer">
        <router-link to="/" class="back-link">← 先逛逛，稍后再登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/user'

const router = useRouter()
const activeTab = ref('login')
const loginShow = ref(false)
const regShow = ref(false)
const loginLoading = ref(false)
const registerLoading = ref(false)

const loginForm = reactive({ studentId: '', password: '' })
const registerForm = reactive({ studentId: '', username: '', phone: '', password: '' })
const loginErrors = reactive({ studentId: '', password: '' })
const registerErrors = reactive({ studentId: '', username: '', phone: '', password: '' })

function clearErrors(errors) {
  Object.keys(errors).forEach(k => errors[k] = '')
}

function validateLogin() {
  clearErrors(loginErrors)
  let ok = true
  if (!loginForm.studentId.trim()) { loginErrors.studentId = '请输入学号'; ok = false }
  if (!loginForm.password) { loginErrors.password = '请输入密码'; ok = false }
  return ok
}

function validateRegister() {
  clearErrors(registerErrors)
  let ok = true
  if (!registerForm.studentId.trim()) { registerErrors.studentId = '请输入学号'; ok = false }
  if (!registerForm.username.trim()) { registerErrors.username = '请输入姓名'; ok = false }
  if (!registerForm.phone.trim()) { registerErrors.phone = '请输入手机号'; ok = false }
  if (!registerForm.password || registerForm.password.length < 6) {
    registerErrors.password = '密码至少6位'; ok = false
  }
  return ok
}

async function handleLogin() {
  if (!validateLogin()) return
  loginLoading.value = true
  try {
    const res = await login({ studentId: loginForm.studentId, password: loginForm.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) { /* interceptor handles */ }
  finally { loginLoading.value = false }
}

async function handleRegister() {
  if (!validateRegister()) return
  registerLoading.value = true
  try {
    await register({ ...registerForm })
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.studentId = registerForm.studentId
    registerForm.password = ''
    registerForm.phone = ''
    registerForm.username = ''
  } catch (e) { /* interceptor handles */ }
  finally { registerLoading.value = false }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-surface);
  padding: var(--space-lg);
  position: relative;
  overflow: hidden;
}

/* ── Decorative circles ── */
.decor-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.decor-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.18;
}

.c1 {
  width: 500px;
  height: 500px;
  background: var(--color-primary-light);
  top: -200px;
  right: -100px;
}

.c2 {
  width: 300px;
  height: 300px;
  background: var(--color-accent-light);
  bottom: -100px;
  left: -80px;
}

.c3 {
  width: 150px;
  height: 150px;
  background: var(--color-primary-light);
  top: 50%;
  left: 10%;
}

/* ── Auth Card ── */
.auth-card {
  position: relative;
  width: 100%;
  max-width: 420px;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: 40px var(--space-xl) var(--space-xl);
  box-shadow: var(--shadow-ambient-mid);
}

.card-header {
  text-align: center;
  margin-bottom: var(--space-lg);
}

.logo-mark {
  font-size: 2.5rem;
  margin-bottom: var(--space-sm);
}

.card-header h1 {
  font-size: var(--font-display-size);
  font-weight: var(--font-display-weight);
  color: var(--color-ink);
  margin-bottom: var(--space-xs);
}

.subtitle {
  font-size: var(--font-body-size);
  color: var(--color-muted);
}

/* ── Tab Switch ── */
.tab-switch {
  display: flex;
  position: relative;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: 3px;
  margin-bottom: var(--space-lg);
}

.tab-btn {
  flex: 1;
  padding: 8px 0;
  border: none;
  background: transparent;
  color: var(--color-muted);
  font-size: var(--font-body-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
  position: relative;
  z-index: 1;
  transition: color var(--duration-fast);
  border-radius: calc(var(--radius-md) - 2px);
}

.tab-btn.active {
  color: var(--color-ink);
}

.tab-indicator {
  position: absolute;
  top: 3px;
  left: 3px;
  width: calc(50% - 3px);
  height: calc(100% - 6px);
  background: var(--color-bg);
  border-radius: calc(var(--radius-md) - 2px);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: transform var(--duration-normal) var(--ease-standard);
}

.tab-indicator.right {
  transform: translateX(100%);
}

/* ── Form ── */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.field-label {
  font-size: var(--font-label-size);
  font-weight: var(--font-label-weight);
  color: var(--color-ink);
  letter-spacing: 0.02em;
}

.field-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-body-size);
  color: var(--color-ink);
  background: var(--color-bg);
  font-family: var(--font-family);
  outline: none;
  transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}

.field-input::placeholder {
  color: oklch(0.65 0.005 28);
}

.field-input:focus {
  border-color: var(--color-primary);
  box-shadow: var(--focus-ring);
}

.field-error {
  font-size: 0.75rem;
  color: var(--color-danger);
  min-height: 1.2em;
}

.pw-wrap {
  position: relative;
  display: flex;
}

.pw-wrap .field-input {
  padding-right: 42px;
}

.pw-toggle {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--color-muted);
  cursor: pointer;
  padding: 4px;
  display: flex;
}

.pw-toggle svg {
  width: 18px;
  height: 18px;
}

.pw-toggle:hover {
  color: var(--color-ink);
}

/* ── Submit Button ── */
.btn-submit {
  padding: 11px 0;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 600;
  font-family: var(--font-family);
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
  margin-top: var(--space-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
}

.btn-submit:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── Footer ── */
.card-footer {
  text-align: center;
  margin-top: var(--space-lg);
  padding-top: var(--space-md);
  border-top: 1px solid var(--color-divider);
}

.back-link {
  font-size: var(--font-label-size);
  color: var(--color-muted);
  text-decoration: none;
  transition: color var(--duration-fast);
}

.back-link:hover {
  color: var(--color-primary);
}

@media (max-width: 480px) {
  .auth-card {
    padding: var(--space-lg) var(--space-md);
  }
}
</style>
