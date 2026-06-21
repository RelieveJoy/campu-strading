<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <nav class="topbar" ref="topbarRef">
      <div class="topbar-inner">
        <!-- Brand -->
        <router-link to="/" class="topbar-brand">
          <span class="brand-icon">🎒</span>
          <span class="brand-text">操场集市</span>
        </router-link>

        <!-- 搜索框 -->
        <form class="topbar-search" @submit.prevent="doSearch">
          <div class="search-input-group">
            <input
              v-model="searchQuery"
              type="search"
              class="search-input"
              placeholder="搜索商品..."
              autocomplete="off"
            />
            <button type="submit" class="search-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
              </svg>
            </button>
          </div>
        </form>

        <!-- Nav links -->
        <div class="topbar-nav" :class="{ open: mobileOpen }">
          <router-link to="/goods" class="nav-link" @click="mobileOpen = false">逛一逛</router-link>
          <router-link to="/lostfound" class="nav-link" @click="mobileOpen = false">失物招领</router-link>

          <!-- 未登录 -->
          <template v-if="!loggedIn">
            <button class="nav-login-btn" @click="openLogin">登录</button>
          </template>

          <!-- 已登录 -->
          <template v-else>
            <div class="user-menu" ref="menuRef">
              <button class="user-trigger" @click="menuOpen = !menuOpen">
                <span class="user-avatar">{{ userName.charAt(0) }}</span>
                <span class="user-name">{{ userName }}</span>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14" :class="{ rotated: menuOpen }"><polyline points="6 9 12 15 18 9"/></svg>
              </button>
              <div class="user-dropdown" v-show="menuOpen">
                <router-link to="/publish" @click="menuOpen = false">发布商品</router-link>
                <router-link to="/my-items" @click="menuOpen = false">我的商品</router-link>
                <router-link to="/orders" @click="menuOpen = false">我的订单</router-link>
                <router-link to="/my-favorites" @click="menuOpen = false">我的收藏</router-link>
                <router-link to="/messages" @click="menuOpen = false">我的消息</router-link>
                <router-link to="/profile" @click="menuOpen = false">个人信息</router-link>
                <router-link to="/stats" @click="menuOpen = false">数据统计</router-link>
                <hr />
                <a href="#" @click.prevent="handleLogout">退出登录</a>
              </div>
            </div>
          </template>
        </div>

        <!-- 移动端汉堡 -->
        <button class="hamburger" @click="mobileOpen = !mobileOpen" :aria-label="mobileOpen ? '关闭菜单' : '打开菜单'">
          <svg v-if="!mobileOpen" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
    </nav>

    <!-- 公告栏 (仅首页显示) -->
    <AnnouncementBar :announcements="announcements" v-if="route.path === '/' || route.path === ''" />

    <!-- 页面内容 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <FooterBar />

    <!-- 登录弹窗 -->
    <LoginModal ref="loginModalRef" @login-success="onLoginSuccess" />

    <!-- 移动端遮罩 -->
    <div class="mobile-overlay" v-if="mobileOpen" @click="mobileOpen = false"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, provide } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/user'
import AnnouncementBar from '../components/AnnouncementBar.vue'
import FooterBar from '../components/FooterBar.vue'
import LoginModal from './LoginModal.vue'

const router = useRouter()
const route = useRoute()

// ── 公告 ──
const announcements = ref([
  { title: '欢迎使用操场集市', content: '本平台专为校园二手物品交易打造，请遵守交易规则，诚信交易！' }
])

// ── 搜索 ──
const searchQuery = ref('')
function doSearch() {
  const q = searchQuery.value.trim()
  if (q) {
    router.push({ path: '/goods', query: { q } })
  } else {
    router.push('/goods')
  }
}

// ── 登录状态 ──
const loggedIn = ref(!!localStorage.getItem('token'))
const userName = ref(getUserName())
const loginModalRef = ref(null)

function getUserName() {
  try { return JSON.parse(localStorage.getItem('user')).username || '同学' } catch { return '同学' }
}

function openLogin() { loginModalRef.value?.open() }

function onLoginSuccess(user) {
  loggedIn.value = true
  userName.value = user?.username || getUserName()
}

async function handleLogout() {
  try { await logout() } catch { /* ignore */ }
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  loggedIn.value = false
  userName.value = '同学'
  menuOpen.value = false
  ElMessage.success('已退出登录')
  router.push('/')
}

// ── 用户菜单 ──
const menuOpen = ref(false)
const menuRef = ref(null)

function closeMenu(e) {
  if (menuRef.value && !menuRef.value.contains(e.target)) menuOpen.value = false
}
onMounted(() => document.addEventListener('click', closeMenu))
onUnmounted(() => document.removeEventListener('click', closeMenu))

// ── 移动端 ──
const mobileOpen = ref(false)
watch(() => route.path, () => { mobileOpen.value = false })

// ── 路由守卫 ──
const authPaths = ['/publish', '/my-items', '/orders', '/my-favorites', '/profile', '/stats', '/messages']
provide('loggedIn', loggedIn)
provide('requireLogin', () => {
  if (!loggedIn.value) { openLogin(); return false }
  return true
})

router.beforeEach((to) => {
  if (authPaths.some(p => to.path.startsWith(p)) && !loggedIn.value) {
    openLogin()
    return false
  }
})
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* ── 顶栏 ── */
.topbar {
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  background: var(--color-primary);
  box-shadow: 0 1px 4px rgba(0,0,0,0.15);
  height: var(--topbar-height);
}
.topbar-inner {
  max-width: var(--content-max-width);
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 16px;
}

.topbar-brand {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: #fff;
  flex-shrink: 0;
}
.brand-icon { font-size: 1.3rem; }
.brand-text { font-weight: 700; font-size: 1.0625rem; white-space: nowrap; }

.topbar-search { width: 100%; max-width: 420px; margin: 0 auto; }
.search-input-group {
  display: flex;
  background: #fff;
  border-radius: var(--radius-sm);
  overflow: hidden;
}
.search-input {
  flex: 1; border: none; padding: 7px 12px;
  font-size: 0.8125rem; font-family: var(--font-family);
  outline: none; background: transparent; color: var(--color-ink);
}
.search-input::placeholder { color: #adb5bd; }
.search-btn {
  background: #fff; border: none; padding: 0 12px;
  color: var(--color-muted); cursor: pointer;
  display: flex; align-items: center;
  transition: color var(--duration-fast);
}
.search-btn:hover { color: var(--color-primary); }

.topbar-nav {
  display: flex; align-items: center; gap: 4px; flex-shrink: 0;
}
.nav-link {
  display: flex; align-items: center; gap: 5px;
  padding: 8px 12px; color: rgba(255,255,255,0.85);
  text-decoration: none; font-size: 0.8125rem; font-weight: 500;
  border-radius: var(--radius-sm);
  transition: background var(--duration-fast), color var(--duration-fast);
}
.nav-link:hover, .nav-link.router-link-exact-active { background: rgba(255,255,255,0.15); color: #fff; }

.nav-login-btn {
  padding: 6px 16px; background: transparent;
  border: 1.5px solid rgba(255,255,255,0.6); color: #fff;
  border-radius: var(--radius-sm); font-size: 0.8125rem;
  font-weight: 500; cursor: pointer; font-family: var(--font-family);
  transition: background var(--duration-fast), border-color var(--duration-fast);
  white-space: nowrap;
}
.nav-login-btn:hover { background: rgba(255,255,255,0.15); border-color: #fff; }

/* 用户菜单 */
.user-menu { position: relative; }
.user-trigger {
  display: flex; align-items: center; gap: 6px;
  padding: 4px 10px; background: rgba(255,255,255,0.12);
  border: none; border-radius: var(--radius-full);
  color: #fff; cursor: pointer; font-family: var(--font-family);
  font-size: 0.8125rem; transition: background var(--duration-fast);
}
.user-trigger:hover { background: rgba(255,255,255,0.2); }
.user-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: rgba(255,255,255,0.25); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8125rem; font-weight: 700;
}
.user-name { max-width: 80px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.user-trigger svg { transition: transform var(--duration-fast); }
.user-trigger svg.rotated { transform: rotate(180deg); }

.user-dropdown {
  position: absolute; top: calc(100% + 6px); right: 0;
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); box-shadow: var(--shadow-ambient-mid);
  min-width: 150px; overflow: hidden; z-index: var(--z-dropdown);
}
.user-dropdown a {
  display: block; padding: 10px 16px; color: var(--color-ink);
  text-decoration: none; font-size: 0.8125rem;
  transition: background var(--duration-fast);
}
.user-dropdown a:hover { background: var(--color-surface); }
.user-dropdown hr { border: none; border-top: 1px solid var(--color-divider); margin: 0; }

.hamburger { display: none; background: none; border: none; color: #fff; cursor: pointer; padding: 4px; }

/* 移动端遮罩 */
.mobile-overlay {
  display: none; position: fixed; inset: 0;
  z-index: calc(var(--z-sticky) - 1); background: rgba(0,0,0,0.35);
}

/* ── 主内容 ── */
.main-content { flex: 1; }

.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-standard);
}
.page-fade-enter-from, .page-fade-leave-to { opacity: 0; }

/* ── 响应式 ── */
@media (max-width: 768px) {
  .hamburger { display: flex; }
  .topbar-nav {
    position: fixed; top: var(--topbar-height); left: 0; right: 0; bottom: 0;
    background: #fff; flex-direction: column; align-items: stretch;
    padding: 16px; gap: 0;
    transform: translateX(100%);
    transition: transform var(--duration-normal) var(--ease-standard);
    z-index: var(--z-dropdown); overflow-y: auto;
  }
  .topbar-nav.open { transform: translateX(0); }
  .topbar-nav .nav-link {
    color: var(--color-ink); padding: 12px 16px;
    font-size: 0.9375rem; border-radius: var(--radius-md);
  }
  .topbar-nav .nav-link:hover { background: var(--color-surface); }
  .nav-login-btn {
    color: var(--color-primary); border-color: var(--color-primary);
    margin: 8px 16px; padding: 10px 0; text-align: center; font-size: 0.9375rem;
  }
  .user-menu { padding: 0 16px; }
  .user-trigger {
    color: var(--color-ink); background: var(--color-surface);
    width: 100%; justify-content: center; padding: 10px; font-size: 0.9375rem;
  }
  .user-dropdown { position: static; box-shadow: none; border: none; margin-top: 4px; }
  .mobile-overlay { display: block; }
  .topbar-search { max-width: none; }
}
</style>
