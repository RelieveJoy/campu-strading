<template>
  <el-container class="app-layout">
    <el-aside width="220px" class="app-aside">
      <div class="logo" @click="$router.push('/')">校园二手交易</div>
      <el-menu :default-active="activeMenu" @select="handleMenuSelect" text-color="#bfcbd9" active-text-color="#409eff" background-color="#304156">
        <el-menu-item index="/">
          <el-icon><House /></el-icon> <span>主页</span>
        </el-menu-item>
        <el-menu-item index="/publish">
          <el-icon><Plus /></el-icon> <span>发布商品</span>
        </el-menu-item>
        <el-menu-item index="/stats">
          <el-icon><DataAnalysis /></el-icon> <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/my-items">
          <el-icon><Goods /></el-icon> <span>我的发布</span>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><Document /></el-icon> <span>我的订单</span>
        </el-menu-item>
        <el-menu-item index="/my-favorites">
          <el-icon><Star /></el-icon> <span>我的收藏</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-right">
          <template v-if="loggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-name">{{ userName }}</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <span class="guest-text">未登录</span>
            <el-button type="primary" size="small" @click="loginModal.open()" style="margin-left:10px">登录</el-button>
          </template>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <LoginModal ref="loginModal" @login-success="onLoginSuccess" />
</template>

<script setup>
import { ref, computed, provide } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Plus, DataAnalysis, Goods, Document, Star } from '@element-plus/icons-vue'
import { logout } from '../api/user'
import LoginModal from './LoginModal.vue'

const route = useRoute()
const router = useRouter()

const loginModal = ref(null)
const loggedIn = ref(!!localStorage.getItem('token'))
const userName = ref(getUserName())

function getUserName() {
  try { return JSON.parse(localStorage.getItem('user')).username || '用户' } catch (e) { return '' }
}

const activeMenu = computed(() => '/' + route.path.split('/')[1])

const onLoginSuccess = (user) => {
  loggedIn.value = true
  userName.value = user.username || getUserName()
}

const authPages = ['/publish', '/my-items', '/orders', '/my-favorites', '/profile']

const handleMenuSelect = (index) => {
  if (!loggedIn.value && authPages.includes(index)) {
    loginModal.value.open()
  } else {
    router.push(index)
  }
}

provide('loggedIn', loggedIn)
provide('requireLogin', () => {
  if (!loggedIn.value) {
    loginModal.value.open()
    return false
  }
  return true
})

const handleCommand = async (cmd) => {
  if (cmd === 'profile') {
    if (!loggedIn.value) { loginModal.value.open(); return }
    router.push('/profile')
  } else if (cmd === 'logout') {
    try { await logout() } catch (e) { /* ignore */ }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    loggedIn.value = false
    userName.value = ''
    ElMessage.success('已退出')
    router.push('/')
  }
}
</script>

<style scoped>
.app-layout { min-height:100vh; }
.app-aside { background:#304156; overflow-y:auto; }
.logo { height:60px; line-height:60px; text-align:center; color:#fff; font-size:18px; font-weight:bold; cursor:pointer; background:#2b3a4a; }
.app-header { background:#fff; display:flex; align-items:center; justify-content:flex-end; border-bottom:1px solid #e6e6e6; padding:0 20px; }
.header-right { display:flex; align-items:center; }
.user-name { color:#333; cursor:pointer; font-size:15px; }
.user-name:hover { color:#409eff; }
.guest-text { color:#999; font-size:14px; }
.el-menu { border-right:none; }
.el-menu-item { font-size:15px; }
.el-main { background:#f5f7fa; padding:0; }
</style>
