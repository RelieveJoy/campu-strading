<template>
  <teleport to="body">
    <div class="modal-overlay" v-if="visible" @click.self="close">
      <div class="modal-box">
        <h3>校园二手交易平台</h3>
        <el-tabs v-model="tab" class="modal-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" @keyup.enter="handleLogin">
              <el-form-item prop="studentId">
                <el-input v-model="loginForm.studentId" placeholder="学号" />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%">登 录</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="注册" name="register">
            <el-form :model="regForm" :rules="regRules" ref="regFormRef">
              <el-form-item prop="studentId">
                <el-input v-model="regForm.studentId" placeholder="学号" />
              </el-form-item>
              <el-form-item prop="username">
                <el-input v-model="regForm.username" placeholder="姓名" />
              </el-form-item>
              <el-form-item prop="phone">
                <el-input v-model="regForm.phone" placeholder="手机号" />
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="regForm.password" type="password" placeholder="密码" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="success" @click="handleRegister" :loading="loading" style="width:100%">注 册</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
        <el-icon class="modal-close" @click="close"><Close /></el-icon>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Close } from '@element-plus/icons-vue'
import { login, register } from '../api/user'

const emit = defineEmits(['login-success'])
const visible = ref(false)
const tab = ref('login')
const loading = ref(false)
const loginFormRef = ref(null)
const regFormRef = ref(null)

const loginForm = reactive({ studentId: '', password: '' })
const regForm = reactive({ studentId: '', username: '', phone: '', password: '' })

const loginRules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const regRules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }]
}

const open = () => { visible.value = true; tab.value = 'login' }
const close = () => { visible.value = false }

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(loginForm)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    emit('login-success', res.data)
    close()
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}

const handleRegister = async () => {
  const valid = await regFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register(regForm)
    ElMessage.success('注册成功，请登录')
    tab.value = 'login'
    loginForm.studentId = regForm.studentId
  } catch (e) { /* handled by interceptor */ }
  finally { loading.value = false }
}

defineExpose({ open, close })
</script>

<style scoped>
.modal-overlay {
  position:fixed; inset:0; z-index:2000;
  background:rgba(0,0,0,0.4);
  backdrop-filter:blur(6px);
  display:flex; align-items:center; justify-content:center;
}
.modal-box {
  background:#fff; border-radius:12px; padding:32px; width:400px; position:relative;
  box-shadow:0 16px 48px rgba(0,0,0,0.2);
}
.modal-box h3 { text-align:center; margin-bottom:20px; color:#333; }
.modal-close { position:absolute; top:16px; right:16px; font-size:20px; cursor:pointer; color:#999; }
.modal-close:hover { color:#333; }
</style>
