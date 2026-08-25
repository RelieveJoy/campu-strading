<template>
  <div class="oauth-callback">正在处理 GitHub 登录...</div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  const token = route.query.token
  const id = route.query.id
  const studentId = route.query.studentId
  const username = route.query.username
  const error = route.query.error

  if (error) {
    ElMessage.error(error)
    router.replace('/login')
    return
  }

  if (token) {
    localStorage.setItem('token', token)
    // 和普通登录一样存完整登录态（前端 ProfileView/MyItems/MyOrders/ChatModal 都依赖 user.id）
    localStorage.setItem('user', JSON.stringify({
      id: id ? Number(id) : null,
      studentId: studentId || '',
      username: username || 'GitHub用户',
      token,
    }))
    ElMessage.success('GitHub 登录成功')
    router.replace('/')
  } else {
    ElMessage.error('登录失败：缺少 token')
    router.replace('/login')
  }
})
</script>

<style scoped>
.oauth-callback {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: #888;
  font-size: 14px;
}
</style>
