<template>
  <div class="app-shell">
    <!-- ── Sidebar ── -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-brand" @click="$router.push('/')">
        <span class="brand-icon">🎒</span>
        <span class="brand-text" v-show="!sidebarCollapsed">操场集市</span>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/" class="nav-item" exact-active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <span v-show="!sidebarCollapsed">首页</span>
        </router-link>

        <router-link to="/publish" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>
          </svg>
          <span v-show="!sidebarCollapsed">发布商品</span>
        </router-link>

        <router-link to="/stats" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
          </svg>
          <span v-show="!sidebarCollapsed">数据统计</span>
        </router-link>

        <div class="nav-divider" v-show="!sidebarCollapsed"></div>

        <router-link to="/my-items" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
          </svg>
          <span v-show="!sidebarCollapsed">我的发布</span>
        </router-link>

        <router-link to="/orders" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
          </svg>
          <span v-show="!sidebarCollapsed">我的订单</span>
        </router-link>

        <router-link to="/my-favorites" class="nav-item" active-class="active">
          <svg class="nav-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
          </svg>
          <span v-show="!sidebarCollapsed">我的收藏</span>
        </router-link>
      </nav>

      <button class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed"
        :title="sidebarCollapsed ? '展开菜单' : '收起菜单'">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
          :style="{ transform: sidebarCollapsed ? 'scaleX(-1)' : '' }">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
    </aside>

    <!-- ── Main content ── -->
    <div class="main-area">
      <header class="topbar">
        <div class="topbar-left">
          <slot name="topbar-left" />
        </div>
        <div class="topbar-right">
          <template v-if="loggedIn">
            <div class="user-menu">
              <span class="user-avatar">{{ userName.charAt(0) }}</span>
              <span class="user-name">{{ userName }}</span>
              <svg class="chevron" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 12 15 18 9"/>
              </svg>
              <div class="user-dropdown">
                <button class="dropdown-item" @click="goProfile">个人中心</button>
                <div class="dropdown-divider"></div>
                <button class="dropdown-item danger" @click="handleLogout">退出登录</button>
              </div>
            </div>
          </template>
          <template v-else>
            <span class="guest-hint">未登录</span>
            <button class="btn-login" @click="openLogin">登录</button>
          </template>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>

  <LoginModal ref="loginModalRef" @login-success="onLoginSuccess" />
</template>

<script setup>
import { ref, computed, provide } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/user'
import LoginModal from './LoginModal.vue'

const route = useRoute()
const router = useRouter()

const loginModalRef = ref(null)
const sidebarCollapsed = ref(false)

const loggedIn = ref(!!localStorage.getItem('token'))
const userName = ref(getUserName())

function getUserName() {
  try { return JSON.parse(localStorage.getItem('user')).username || '同学' } catch (e) { return '同学' }
}

function openLogin() { loginModalRef.value?.open() }

function onLoginSuccess(user) {
  loggedIn.value = true
  userName.value = user.username || getUserName()
}

function goProfile() {
  if (!loggedIn.value) { openLogin(); return }
  router.push('/profile')
}

async function handleLogout() {
  try { await logout() } catch (e) { /* ignore */ }
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  loggedIn.value = false
  userName.value = '同学'
  ElMessage.success('已退出登录')
  router.push('/')
}

const authPaths = ['/publish', '/my-items', '/orders', '/my-favorites', '/profile']
provide('loggedIn', loggedIn)
provide('requireLogin', () => {
  if (!loggedIn.value) {
    openLogin()
    return false
  }
  return true
})

// ── Guard: protect auth-required routes ──
router.beforeEach((to) => {
  if (authPaths.some(p => to.path.startsWith(p)) && !loggedIn.value) {
    openLogin()
    return false
  }
})
</script>

<style scoped>
/* ── Shell ── */
.app-shell {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg);
}

/* ── Sidebar ── */
.sidebar {
  width: var(--sidebar-width);
  min-width: var(--sidebar-width);
  background: var(--color-surface);
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--color-divider);
  transition: width var(--duration-normal) var(--ease-standard),
              min-width var(--duration-normal) var(--ease-standard);
  position: sticky;
  top: 0;
  height: 100vh;
  z-index: var(--z-sticky);
}

.sidebar.collapsed {
  width: 64px;
  min-width: 64px;
}

.sidebar-brand {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: var(--color-bg);
  border-bottom: 1px solid var(--color-divider);
  cursor: pointer;
  padding: 0 12px;
  flex-shrink: 0;
}

.brand-icon {
  font-size: 1.25rem;
  flex-shrink: 0;
}

.brand-text {
  color: var(--color-primary);
  font-size: var(--font-headline-size);
  font-weight: 700;
  white-space: nowrap;
  overflow: hidden;
}

/* ── Nav ── */
.sidebar-nav {
  flex: 1;
  padding: var(--space-sm) 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: var(--color-ink);
  font-size: 0.9375rem;
  font-weight: 500;
  text-decoration: none;
  transition: background var(--duration-fast) var(--ease-standard),
              color var(--duration-fast) var(--ease-standard);
  position: relative;
  white-space: nowrap;
  overflow: hidden;
}

.nav-item:hover {
  background: var(--color-divider);
}

.nav-item.active {
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 28px;
  background: var(--color-primary);
  border-radius: 0 3px 3px 0;
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-divider {
  height: 1px;
  background: var(--color-divider);
  margin: var(--space-sm) var(--space-md);
}

/* ── Sidebar Toggle ── */
.sidebar-toggle {
  padding: var(--space-md);
  background: none;
  border: none;
  border-top: 1px solid var(--color-divider);
  cursor: pointer;
  color: var(--color-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color var(--duration-fast);
  flex-shrink: 0;
}

.sidebar-toggle:hover {
  color: var(--color-ink);
}

.sidebar-toggle svg {
  width: 20px;
  height: 20px;
  transition: transform var(--duration-normal) var(--ease-standard);
}

/* ── Main Area ── */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ── Topbar ── */
.topbar {
  height: var(--topbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--space-lg);
  background: var(--color-bg);
  border-bottom: 1px solid var(--color-divider);
  flex-shrink: 0;
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
}

.topbar-left {
  flex: 1;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.guest-hint {
  color: var(--color-muted);
  font-size: var(--font-body-size);
}

.btn-login {
  padding: 6px 18px;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 500;
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
}

.btn-login:hover {
  background: var(--color-primary-hover);
}

/* ── User Menu ── */
.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: background var(--duration-fast);
  position: relative;
}

.user-menu:hover {
  background: var(--color-surface);
}

.user-menu:hover .user-dropdown {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--color-primary);
  color: var(--color-primary-text);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-label-size);
  font-weight: 600;
  flex-shrink: 0;
}

.user-name {
  font-size: var(--font-body-size);
  font-weight: 500;
  color: var(--color-ink);
}

.chevron {
  width: 16px;
  height: 16px;
  color: var(--color-muted);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 4px;
  min-width: 140px;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-ambient-mid);
  padding: var(--space-xs) 0;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-4px);
  transition: opacity var(--duration-fast) var(--ease-out),
              visibility var(--duration-fast),
              transform var(--duration-fast) var(--ease-out);
  z-index: var(--z-dropdown);
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 8px 16px;
  background: none;
  border: none;
  color: var(--color-ink);
  font-size: var(--font-body-size);
  text-align: left;
  cursor: pointer;
  transition: background var(--duration-fast);
}

.dropdown-item:hover {
  background: var(--color-surface);
}

.dropdown-item.danger {
  color: var(--color-danger);
}

.dropdown-divider {
  height: 1px;
  background: var(--color-divider);
  margin: var(--space-xs) 0;
}

/* ── Content ── */
.content {
  flex: 1;
  padding: var(--space-lg);
}

/* ── Page transitions ── */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity var(--duration-normal) var(--ease-out),
              transform var(--duration-normal) var(--ease-out);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    z-index: var(--z-modal);
    box-shadow: var(--shadow-ambient-high);
    transform: translateX(-100%);
  }

  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }

  .sidebar.collapsed {
    transform: translateX(-100%);
    width: var(--sidebar-width);
    min-width: var(--sidebar-width);
  }

  .content {
    padding: var(--space-md);
  }
}

@media (prefers-reduced-motion: reduce) {
  .page-fade-enter-active,
  .page-fade-leave-active {
    transition: opacity 0.01ms;
  }
}
</style>
