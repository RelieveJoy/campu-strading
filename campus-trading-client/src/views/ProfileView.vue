<template>
  <div class="profile-container">
    <div class="profile-header">
      <h2>个人中心</h2>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="个人信息" name="info">
        <el-form :model="infoForm" :rules="infoRules" ref="infoFormRef" label-width="80px" class="profile-form">
          <el-form-item label="学号">
            <el-input v-model="infoForm.studentId" disabled />
          </el-form-item>
          <el-form-item label="姓名" prop="username">
            <el-input v-model="infoForm.username" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="infoForm.phone" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateInfo" :loading="infoLoading">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="修改密码" name="password">
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" class="profile-form">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword" :loading="pwdLoading">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, editPassword } from '../api/user'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')

const activeTab = ref('info')
const infoFormRef = ref(null)
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)
const userId = ref(null)

const infoForm = reactive({ studentId: '', username: '', phone: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

const infoRules = {
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }]
}

const updateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  infoLoading.value = true
  try {
    await updateUserInfo(userId.value, infoForm)
    ElMessage.success('保存成功')
  } catch (e) { /* handled */ }
  finally { infoLoading.value = false }
}

const updatePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await editPassword(pwdForm)
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
  } catch (e) { /* handled */ }
  finally { pwdLoading.value = false }
}

const fetchProfile = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  userId.value = user.id
  if (!userId.value) return
  try {
    const res = await getUserInfo(userId.value)
    Object.assign(infoForm, { studentId: res.data.studentId, username: res.data.username, phone: res.data.phone })
  } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchProfile() })

onMounted(fetchProfile)
</script>

<style scoped>
.profile-container { max-width:600px; margin:0 auto; padding:20px; }
.profile-header { display:flex; align-items:center; gap:16px; margin-bottom:24px; }
.profile-header h2 { margin:0; }
.profile-form { padding:24px; background:#fff; border-radius:8px; max-width:440px; }
</style>
