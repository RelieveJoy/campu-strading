<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人中心</h1>
    </div>

    <div class="profile-card">
      <!-- ── Avatar Section ── -->
      <div class="avatar-section">
        <div class="avatar-circle">{{ (infoForm.username || '?').charAt(0) }}</div>
        <div class="avatar-info">
          <span class="avatar-name">{{ infoForm.username || '未设置姓名' }}</span>
          <span class="avatar-id">学号 {{ infoForm.studentId }}</span>
        </div>
      </div>

      <div class="tab-switch">
        <button class="tab-btn" :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">个人信息</button>
        <button class="tab-btn" :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">修改密码</button>
        <div class="tab-indicator" :class="{ right: activeTab === 'password' }"></div>
      </div>

      <!-- ── Info Form ── -->
      <form v-show="activeTab === 'info'" @submit.prevent="updateInfo" class="profile-form">
        <div class="field">
          <label class="field-label">学号</label>
          <input type="text" class="field-input" :value="infoForm.studentId" disabled />
        </div>
        <div class="field">
          <label class="field-label" for="profile-name">姓名 <span class="required">*</span></label>
          <input id="profile-name" v-model="infoForm.username" type="text" class="field-input" placeholder="请输入姓名" />
          <span class="field-error" v-if="infoErrors.username">{{ infoErrors.username }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="profile-phone">手机号 <span class="required">*</span></label>
          <input id="profile-phone" v-model="infoForm.phone" type="tel" class="field-input" placeholder="请输入手机号" />
          <span class="field-error" v-if="infoErrors.phone">{{ infoErrors.phone }}</span>
        </div>
        <button type="submit" class="btn-submit" :disabled="infoLoading">
          <span v-if="infoLoading" class="spinner"></span>
          保存修改
        </button>
      </form>

      <!-- ── Password Form ── -->
      <form v-show="activeTab === 'password'" @submit.prevent="updatePassword" class="profile-form">
        <div class="field">
          <label class="field-label" for="profile-old-pw">旧密码 <span class="required">*</span></label>
          <div class="pw-wrap">
            <input :id="oldShow ? 'profile-old-pw-text' : 'profile-old-pw'" v-model="pwdForm.oldPassword"
              :type="oldShow ? 'text' : 'password'" class="field-input" placeholder="请输入旧密码" autocomplete="current-password" />
            <button type="button" class="pw-toggle" @click="oldShow = !oldShow">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <span class="field-error" v-if="pwdErrors.oldPassword">{{ pwdErrors.oldPassword }}</span>
        </div>
        <div class="field">
          <label class="field-label" for="profile-new-pw">新密码 <span class="required">*</span></label>
          <div class="pw-wrap">
            <input :id="newShow ? 'profile-new-pw-text' : 'profile-new-pw'" v-model="pwdForm.newPassword"
              :type="newShow ? 'text' : 'password'" class="field-input" placeholder="至少6位新密码" autocomplete="new-password" />
            <button type="button" class="pw-toggle" @click="newShow = !newShow">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
          <span class="field-error" v-if="pwdErrors.newPassword">{{ pwdErrors.newPassword }}</span>
        </div>
        <button type="submit" class="btn-submit" :disabled="pwdLoading">
          <span v-if="pwdLoading" class="spinner"></span>
          修改密码
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, editPassword } from '../api/user'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')

const activeTab = ref('info')
const infoLoading = ref(false)
const pwdLoading = ref(false)
const oldShow = ref(false)
const newShow = ref(false)
const userId = ref(null)

const infoForm = reactive({ studentId: '', username: '', phone: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const infoErrors = reactive({ username: '', phone: '' })
const pwdErrors = reactive({ oldPassword: '', newPassword: '' })

function clearErrors(e) { Object.keys(e).forEach(k => e[k] = '') }

function validateInfo() {
  clearErrors(infoErrors)
  let ok = true
  if (!infoForm.username.trim()) { infoErrors.username = '请输入姓名'; ok = false }
  if (!infoForm.phone.trim()) { infoErrors.phone = '请输入手机号'; ok = false }
  return ok
}

function validatePwd() {
  clearErrors(pwdErrors)
  let ok = true
  if (!pwdForm.oldPassword) { pwdErrors.oldPassword = '请输入旧密码'; ok = false }
  if (!pwdForm.newPassword || pwdForm.newPassword.length < 6) { pwdErrors.newPassword = '新密码至少6位'; ok = false }
  return ok
}

async function updateInfo() {
  if (!validateInfo()) return
  infoLoading.value = true
  try {
    await updateUserInfo(userId.value, infoForm)
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    user.username = infoForm.username
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('保存成功')
  } catch (e) { /* handled */ }
  finally { infoLoading.value = false }
}

async function updatePassword() {
  if (!validatePwd()) return
  pwdLoading.value = true
  try {
    await editPassword(pwdForm)
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
  } catch (e) { /* handled */ }
  finally { pwdLoading.value = false }
}

async function fetchProfile() {
  if (!loggedIn.value) { requireLogin(); return }
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = user.id
  if (!userId.value) return
  try {
    const res = await getUserInfo(userId.value)
    Object.assign(infoForm, {
      studentId: res.data.studentId || '',
      username: res.data.username || '',
      phone: res.data.phone || '',
    })
  } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchProfile() })
onMounted(fetchProfile)
</script>

<style scoped>
.profile-page { max-width: 540px; margin: 0 auto; padding: var(--space-lg); }

.page-header { margin-bottom: var(--space-lg); }
.page-header h1 { font-size: var(--font-display-size); font-weight: var(--font-display-weight); color: var(--color-ink); }

.profile-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-lg); padding: var(--space-xl);
}

/* Avatar */
.avatar-section {
  display: flex; align-items: center; gap: var(--space-md);
  padding-bottom: var(--space-lg); border-bottom: 1px solid var(--color-divider);
  margin-bottom: var(--space-lg);
}
.avatar-circle {
  width: 64px; height: 64px; border-radius: 50%;
  background: var(--color-primary); color: var(--color-primary-text);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.5rem; font-weight: 700; flex-shrink: 0;
}
.avatar-info { display: flex; flex-direction: column; gap: 2px; }
.avatar-name { font-size: var(--font-headline-size); font-weight: 600; color: var(--color-ink); }
.avatar-id { font-size: var(--font-label-size); color: var(--color-muted); }

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
.profile-form { display: flex; flex-direction: column; gap: var(--space-md); }

.field { display: flex; flex-direction: column; gap: 4px; }
.field-label {
  font-size: var(--font-label-size); font-weight: 500;
  color: var(--color-ink); letter-spacing: 0.02em;
}
.required { color: var(--color-danger); }

.field-input {
  width: 100%; padding: 10px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-sm); font-size: var(--font-body-size);
  color: var(--color-ink); background: var(--color-bg); font-family: var(--font-family);
  outline: none; transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}
.field-input::placeholder { color: oklch(0.65 0.005 28); }
.field-input:focus { border-color: var(--color-primary); box-shadow: var(--focus-ring); }
.field-input:disabled { background: var(--color-surface); color: var(--color-muted); cursor: not-allowed; }
.field-error { font-size: 0.75rem; color: var(--color-danger); }

.pw-wrap { position: relative; display: flex; }
.pw-wrap .field-input { padding-right: 42px; }
.pw-toggle {
  position: absolute; right: 8px; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: var(--color-muted); cursor: pointer;
  padding: 4px; display: flex;
}
.pw-toggle:hover { color: var(--color-ink); }

.btn-submit {
  padding: 10px 0; background: var(--color-primary); color: var(--color-primary-text);
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
</style>
