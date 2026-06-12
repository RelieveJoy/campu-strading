<template>
  <teleport to="body">
    <transition name="modal-fade">
      <div class="modal-overlay" v-if="visible" @click.self="close">
        <div class="modal-card">
          <button class="modal-close" @click="close" aria-label="关闭">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </button>

          <div class="modal-header">
            <span class="modal-logo">🎒</span>
            <h3>操场集市</h3>
          </div>

          <div class="tab-switch">
            <button class="tab-btn" :class="{ active: tab === 'login' }" @click="tab = 'login'">登录</button>
            <button class="tab-btn" :class="{ active: tab === 'register' }" @click="tab = 'register'">注册</button>
            <div class="tab-indicator" :class="{ right: tab === 'register' }"></div>
          </div>

          <form v-show="tab === 'login'" @submit.prevent="handleLogin" class="auth-form">
            <div class="field">
              <input v-model="loginForm.studentId" type="text" class="field-input"
                placeholder="学号" autocomplete="username" />
              <span class="field-error" v-if="loginErrors.studentId">{{ loginErrors.studentId }}</span>
            </div>
            <div class="field">
              <div class="pw-wrap">
                <input v-model="loginForm.password" :type="loginShow ? 'text' : 'password'"
                  class="field-input" placeholder="密码" autocomplete="current-password" />
                <button type="button" class="pw-toggle" @click="loginShow = !loginShow"
                  :aria-label="loginShow ? '隐藏密码' : '显示密码'">
                  <svg v-if="loginShow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M1 1l22 22"/></svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                </button>
              </div>
              <span class="field-error" v-if="loginErrors.password">{{ loginErrors.password }}</span>
            </div>
            <button type="submit" class="btn-submit" :disabled="loading">
              <span v-if="loading" class="spinner"></span>
              {{ loading ? '登录中...' : '登 录' }}
            </button>
          </form>

          <form v-show="tab === 'register'" @submit.prevent="handleRegister" class="auth-form">
            <div class="field">
              <input v-model="regForm.studentId" type="text" class="field-input"
                placeholder="学号" autocomplete="off" />
              <span class="field-error" v-if="regErrors.studentId">{{ regErrors.studentId }}</span>
            </div>
            <div class="field">
              <input v-model="regForm.username" type="text" class="field-input"
                placeholder="姓名" autocomplete="name" />
              <span class="field-error" v-if="regErrors.username">{{ regErrors.username }}</span>
            </div>
            <div class="field">
              <input v-model="regForm.phone" type="tel" class="field-input"
                placeholder="手机号" autocomplete="tel" />
              <span class="field-error" v-if="regErrors.phone">{{ regErrors.phone }}</span>
            </div>
            <div class="field">
              <div class="pw-wrap">
                <input v-model="regForm.password" :type="regShow ? 'text' : 'password'"
                  class="field-input" placeholder="密码（至少6位）" autocomplete="new-password" />
                <button type="button" class="pw-toggle" @click="regShow = !regShow"
                  :aria-label="regShow ? '隐藏密码' : '显示密码'">
                  <svg v-if="regShow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M1 1l22 22"/></svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                </button>
              </div>
              <span class="field-error" v-if="regErrors.password">{{ regErrors.password }}</span>
            </div>
            <button type="submit" class="btn-submit" :disabled="loading">
              <span v-if="loading" class="spinner"></span>
              {{ loading ? '注册中...' : '注 册' }}
            </button>
          </form>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/user'

const emit = defineEmits(['login-success'])
const visible = ref(false)
const tab = ref('login')
const loading = ref(false)
const loginShow = ref(false)
const regShow = ref(false)

const loginForm = reactive({ studentId: '', password: '' })
const regForm = reactive({ studentId: '', username: '', phone: '', password: '' })
const loginErrors = reactive({ studentId: '', password: '' })
const regErrors = reactive({ studentId: '', username: '', phone: '', password: '' })

function clearErrors(e) { Object.keys(e).forEach(k => e[k] = '') }

function open() { visible.value = true; tab.value = 'login' }
function close() { visible.value = false }

function validateLogin() {
  clearErrors(loginErrors)
  let ok = true
  if (!loginForm.studentId.trim()) { loginErrors.studentId = '请输入学号'; ok = false }
  if (!loginForm.password) { loginErrors.password = '请输入密码'; ok = false }
  return ok
}

function validateRegister() {
  clearErrors(regErrors)
  let ok = true
  if (!regForm.studentId.trim()) { regErrors.studentId = '请输入学号'; ok = false }
  if (!regForm.username.trim()) { regErrors.username = '请输入姓名'; ok = false }
  if (!regForm.phone.trim()) { regErrors.phone = '请输入手机号'; ok = false }
  if (!regForm.password || regForm.password.length < 6) { regErrors.password = '密码至少6位'; ok = false }
  return ok
}

async function handleLogin() {
  if (!validateLogin()) return
  loading.value = true
  try {
    const res = await login({ studentId: loginForm.studentId, password: loginForm.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    emit('login-success', res.data)
    close()
  } catch (e) { /* interceptor handles */ }
  finally { loading.value = false }
}

async function handleRegister() {
  if (!validateRegister()) return
  loading.value = true
  try {
    await register({ ...regForm })
    ElMessage.success('注册成功，请登录')
    tab.value = 'login'
    loginForm.studentId = regForm.studentId
    regForm.password = ''; regForm.phone = ''; regForm.username = ''
  } catch (e) { /* interceptor handles */ }
  finally { loading.value = false }
}

defineExpose({ open, close })
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: var(--z-modal-backdrop);
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--space-lg);
}

.modal-card {
  background: var(--color-bg);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  width: 100%;
  max-width: 400px;
  position: relative;
  box-shadow: var(--shadow-ambient-high);
}

.modal-close {
  position: absolute;
  top: var(--space-md);
  right: var(--space-md);
  background: none;
  border: none;
  color: var(--color-muted);
  cursor: pointer;
  padding: 4px;
  border-radius: var(--radius-sm);
  transition: color var(--duration-fast), background var(--duration-fast);
  display: flex;
}

.modal-close:hover { color: var(--color-ink); background: var(--color-surface); }
.modal-close svg { width: 18px; height: 18px; }

.modal-header { text-align: center; margin-bottom: var(--space-lg); }
.modal-logo { font-size: 2rem; display: block; margin-bottom: var(--space-sm); }
.modal-header h3 { font-size: var(--font-headline-size); font-weight: 600; color: var(--color-ink); }

/* Tab switch */
.tab-switch {
  display: flex; position: relative;
  background: var(--color-surface); border-radius: var(--radius-md);
  padding: 3px; margin-bottom: var(--space-lg);
}
.tab-btn {
  flex: 1; padding: 8px 0; border: none; background: transparent;
  color: var(--color-muted); font-size: var(--font-body-size); font-weight: 500;
  font-family: var(--font-family); cursor: pointer; position: relative; z-index: 1;
  transition: color var(--duration-fast); border-radius: calc(var(--radius-md) - 2px);
}
.tab-btn.active { color: var(--color-ink); }
.tab-indicator {
  position: absolute; top: 3px; left: 3px; width: calc(50% - 3px); height: calc(100% - 6px);
  background: var(--color-bg); border-radius: calc(var(--radius-md) - 2px);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: transform var(--duration-normal) var(--ease-standard);
}
.tab-indicator.right { transform: translateX(100%); }

/* Form */
.auth-form { display: flex; flex-direction: column; gap: var(--space-md); }
.field { display: flex; flex-direction: column; gap: 4px; }
.field-input {
  width: 100%; padding: 10px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-sm); font-size: var(--font-body-size);
  color: var(--color-ink); background: var(--color-bg); font-family: var(--font-family);
  outline: none; transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}
.field-input::placeholder { color: oklch(0.65 0.005 28); }
.field-input:focus { border-color: var(--color-primary); box-shadow: var(--focus-ring); }
.field-error { font-size: 0.75rem; color: var(--color-danger); min-height: 1.2em; }
.pw-wrap { position: relative; display: flex; }
.pw-wrap .field-input { padding-right: 42px; }
.pw-toggle {
  position: absolute; right: 8px; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: var(--color-muted); cursor: pointer;
  padding: 4px; display: flex;
}
.pw-toggle svg { width: 18px; height: 18px; }
.pw-toggle:hover { color: var(--color-ink); }

.btn-submit {
  padding: 11px 0; background: var(--color-primary); color: var(--color-primary-text);
  border: none; border-radius: var(--radius-md); font-size: var(--font-body-size);
  font-weight: 600; font-family: var(--font-family); cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
  display: flex; align-items: center; justify-content: center; gap: var(--space-sm);
  margin-top: var(--space-sm);
}
.btn-submit:hover:not(:disabled) { background: var(--color-primary-hover); }
.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }

.spinner {
  width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Modal transition */
.modal-fade-enter-active { transition: opacity var(--duration-normal) var(--ease-out); }
.modal-fade-enter-active .modal-card {
  transition: transform var(--duration-normal) var(--ease-out), opacity var(--duration-normal) var(--ease-out);
}
.modal-fade-leave-active { transition: opacity var(--duration-fast) var(--ease-out); }
.modal-fade-leave-active .modal-card {
  transition: transform var(--duration-fast) var(--ease-out), opacity var(--duration-fast);
}
.modal-fade-enter-from { opacity: 0; }
.modal-fade-enter-from .modal-card { transform: translateY(12px) scale(0.97); opacity: 0; }
.modal-fade-leave-to { opacity: 0; }
.modal-fade-leave-to .modal-card { transform: translateY(4px); opacity: 0; }
</style>
