<template>
  <div class="messages-page">
    <div class="page-header">
      <h1>我的消息</h1>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-switch">
      <button class="tab-btn" :class="{ active: tab === 'chat' }" @click="tab = 'chat'">
        <i class="bi bi-chat-dots me-1"></i>聊天
        <span v-if="chatUnread" class="tab-badge">{{ chatUnread }}</span>
      </button>
      <button class="tab-btn" :class="{ active: tab === 'notif' }" @click="tab = 'notif'">
        <i class="bi bi-bell me-1"></i>订单通知
        <span v-if="notifUnread" class="tab-badge">{{ notifUnread }}</span>
      </button>
    </div>

    <!-- ── 聊天列表 ── -->
    <template v-if="tab === 'chat'">
      <div v-if="chatLoading" class="loading-state">加载中...</div>
      <div v-else-if="conversations.length === 0" class="empty-state">
        <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
          <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
          <path d="M40 42h40M40 58h30M40 74h20" stroke="var(--color-muted)" stroke-width="2.5" stroke-linecap="round"/>
        </svg>
        <p>暂无聊天消息</p>
      </div>
      <div v-else class="list-wrap">
        <router-link
          v-for="conv in conversations"
          :key="'chat-' + conv.itemId + '-' + conv.otherUserId"
          :to="`/item/${conv.itemId}`"
          class="list-item"
          :class="{ unread: conv.unreadCount > 0 }"
          @click="openChat(conv)"
        >
          <div class="item-avatar">{{ conv.otherUserName?.charAt(0) || '?' }}</div>
          <div class="item-info">
            <div class="item-top">
              <span class="item-name">{{ conv.otherUserName }}</span>
              <span class="item-time">{{ formatTime(conv.lastTime) }}</span>
            </div>
            <div class="item-bottom">
              <span class="item-preview">{{ conv.lastContent }}</span>
              <span v-if="conv.unreadCount > 0" class="unread-badge">{{ conv.unreadCount }}</span>
            </div>
            <div class="item-subtitle">商品：{{ conv.itemTitle || '未知' }}</div>
          </div>
        </router-link>
      </div>
    </template>

    <!-- ── 订单通知列表 ── -->
    <template v-if="tab === 'notif'">
      <div v-if="notifLoading" class="loading-state">加载中...</div>
      <div v-else-if="notifications.length === 0" class="empty-state">
        <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
          <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
          <path d="M35 40h50M35 58h40M35 76h30" stroke="var(--color-muted)" stroke-width="2.5" stroke-linecap="round"/>
        </svg>
        <p>暂无订单通知</p>
      </div>
      <div v-else class="notif-list">
        <div
          v-for="n in notifications"
          :key="'notif-' + (n.id || n.orderId) + '-' + n.type"
          class="notif-card"
          :class="[{ unread: !n.isRead }, n.type]"
          @click="goNotif(n)"
        >
          <!-- 类型标签 -->
          <div class="card-type-badge" :class="n.type">
            <i v-if="n.type === 'CREATED'" class="bi bi-bag-check me-1"></i>
            <i v-else-if="n.type === 'CONFIRMED'" class="bi bi-check2-all me-1"></i>
            <i v-else class="bi bi-x-circle me-1"></i>
            {{ typeLabel(n.type) }}
          </div>

          <!-- 主体 -->
          <div class="card-body-row">
            <p class="card-desc">{{ n.content }}</p>
          </div>

          <!-- 底部：价格 + 时间 + 操作提示 -->
          <div class="card-footer-row">
            <span class="notif-price" v-if="n.amount">¥{{ n.amount }}</span>
            <span class="notif-time">{{ formatTime(n.createTime || n.createdAt) }}</span>
            <span v-if="!n.isRead" class="unread-dot"></span>
            <span v-if="n.type === 'CREATED'" class="card-hint">去处理 →</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '../api/chat'
import { getNotifications, markAllRead } from '../api/notification'

const router = useRouter()
const tab = ref('chat')

// ── 聊天 ──
const conversations = ref([])
const chatLoading = ref(true)
const chatUnread = computed(() => conversations.value.reduce((s, c) => s + (c.unreadCount || 0), 0))

async function fetchConversations() {
  chatLoading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch { conversations.value = [] }
  finally { chatLoading.value = false }
}

function openChat(conv) {
  sessionStorage.setItem('openChat', JSON.stringify({
    itemId: conv.itemId,
    receiverId: conv.otherUserId,
    receiverName: conv.otherUserName,
  }))
}

// ── 订单通知 ──
const notifications = ref([])
const notifLoading = ref(true)
const notifUnread = computed(() => notifications.value.filter(n => !n.isRead).length)

async function fetchNotifications() {
  notifLoading.value = true
  try {
    const res = await getNotifications()
    notifications.value = res.data || []
    markAllRead().catch(() => {}) // 拉取后标记全部已读
  } catch {
    // 后端未实现时用空列表
    notifications.value = []
  }
  finally { notifLoading.value = false }
}

function typeLabel(type) {
  const map = { CREATED: '新订单', CONFIRMED: '已确认收货', CANCELLED: '已取消订单' }
  return map[type] || '订单动态'
}

function goNotif(n) {
  router.push('/orders')
}

// ── 通用 ──
function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  if (d.toDateString() === now.toDateString()) {
    return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  }
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
  fetchConversations()
  fetchNotifications()
})
</script>

<style scoped>
.messages-page {
  max-width: 680px;
  margin: 0 auto;
  padding: var(--space-lg);
}

.page-header { margin-bottom: var(--space-lg); }
.page-header h1 {
  font-size: var(--font-display-size);
  font-weight: var(--font-display-weight);
  color: var(--color-ink);
  margin: 0;
}

/* ── Tab Switch ── */
.tab-switch {
  display: flex; gap: 0; margin-bottom: var(--space-lg);
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  overflow: hidden; background: var(--color-surface);
}
.tab-btn {
  flex: 1; padding: 10px 0; border: none; background: transparent;
  color: var(--color-muted); font-size: 0.875rem; font-weight: 500;
  font-family: var(--font-family); cursor: pointer; position: relative;
  transition: all var(--duration-fast);
  display: flex; align-items: center; justify-content: center; gap: 4px;
}
.tab-btn.active {
  background: var(--color-bg); color: var(--color-primary);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.tab-badge {
  background: var(--color-danger); color: #fff;
  font-size: 0.625rem; font-weight: 700;
  min-width: 16px; height: 16px; line-height: 16px;
  text-align: center; border-radius: 8px; padding: 0 5px;
}

/* ── List ── */
.list-wrap {
  display: flex; flex-direction: column; gap: 2px;
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  overflow: hidden; background: var(--color-bg);
}
.list-item {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; text-decoration: none; color: var(--color-ink);
  transition: background var(--duration-fast); cursor: pointer;
  border-bottom: 1px solid var(--color-divider);
}
.list-item:last-child { border-bottom: none; }
.list-item:hover { background: var(--color-surface); }
.list-item.unread { background: #f0f3fb; }

/* Avatar / Icon */
.item-avatar {
  width: 44px; height: 44px; border-radius: 50%;
  background: var(--color-accent-light); color: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 1rem; flex-shrink: 0;
}

/* Info */
.item-info { flex: 1; min-width: 0; }
.item-top { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2px; }
.item-name { font-weight: 500; font-size: 0.875rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 360px; }
.item-time { font-size: 0.75rem; color: var(--color-muted); flex-shrink: 0; white-space: nowrap; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; }
.item-preview {
  font-size: 0.8125rem; color: var(--color-muted);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  max-width: 420px;
}
.item-subtitle { font-size: 0.75rem; color: var(--color-muted); margin-top: 2px; }

/* ── Notification Cards ── */
.notif-list {
  display: flex; flex-direction: column; gap: 10px;
}
.notif-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); padding: 14px 16px;
  cursor: pointer; transition: box-shadow var(--duration-fast), border-color var(--duration-fast);
  border-left: 4px solid var(--color-border);
  position: relative;
}
.notif-card:hover { box-shadow: var(--shadow-ambient-low); }
.notif-card.unread { background: #fafbff; }
.notif-card.CREATED { border-left-color: var(--color-primary); }
.notif-card.CONFIRMED { border-left-color: var(--color-success); }
.notif-card.CANCELLED { border-left-color: var(--color-muted); }

.card-type-badge {
  display: inline-flex; align-items: center;
  font-size: 0.6875rem; font-weight: 600; padding: 2px 10px;
  border-radius: var(--radius-full); margin-bottom: 8px;
}
.card-type-badge.CREATED { background: var(--color-accent-light); color: var(--color-primary); }
.card-type-badge.CONFIRMED { background: var(--color-success-light); color: var(--color-success); }
.card-type-badge.CANCELLED { background: var(--color-surface); color: var(--color-muted); }

.card-body-row { margin-bottom: 6px; }
.card-desc { margin: 0; font-size: 0.875rem; line-height: 1.5; color: var(--color-ink); }
.buyer-name { color: var(--color-primary); }
.card-sep { color: var(--color-muted); margin: 0 2px; }

.card-footer-row {
  display: flex; align-items: center; gap: 12px; margin-top: 6px;
}
.notif-price {
  font-size: 1.0625rem; font-weight: 700; color: var(--color-price);
}
.notif-time {
  font-size: 0.75rem; color: var(--color-muted);
}
.unread-dot {
  width: 7px; height: 7px; border-radius: 50%;
  background: var(--color-danger); flex-shrink: 0;
}
.card-hint {
  margin-left: auto; font-size: 0.75rem; color: var(--color-primary);
  font-weight: 500;
}

.unread-badge {
  background: var(--color-danger); color: #fff;
  font-size: 0.6875rem; font-weight: 600;
  min-width: 18px; height: 18px; line-height: 18px;
  text-align: center; border-radius: 9px; padding: 0 5px;
  flex-shrink: 0;
}

.loading-state { text-align: center; color: var(--color-muted); padding: var(--space-xxl); }
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }
.empty-icon { width: 80px; height: 80px; margin-bottom: var(--space-md); display: block; margin-left: auto; margin-right: auto; }

@media (max-width: 500px) {
  .messages-page { padding: var(--space-md); }
  .list-item { padding: 12px; }
}
</style>
