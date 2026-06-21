<template>
  <div class="messages-page">
    <div class="page-header">
      <h1>我的消息</h1>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-state">加载中...</div>

    <!-- Empty -->
    <div v-else-if="conversations.length === 0" class="empty-state">
      <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
        <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
        <path d="M40 42h40M40 58h30M40 74h20" stroke="var(--color-muted)" stroke-width="2.5" stroke-linecap="round"/>
      </svg>
      <p>暂无消息</p>
    </div>

    <!-- List -->
    <div v-else class="conversation-list">
      <router-link
        v-for="conv in conversations"
        :key="conv.itemId"
        :to="`/item/${conv.itemId}?chat=1`"
        class="conv-item"
        :class="{ unread: conv.unreadCount > 0 }"
        @click="openChatForItem(conv)"
      >
        <img
          :src="conv.itemImage || ''"
          class="conv-img"
          @error="onImgError"
        />
        <div class="conv-info">
          <div class="conv-top">
            <span class="conv-name">{{ conv.otherUserName }}</span>
            <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
          </div>
          <div class="conv-bottom">
            <span class="conv-preview">{{ conv.lastContent }}</span>
            <span v-if="conv.unreadCount > 0" class="unread-badge">{{ conv.unreadCount }}</span>
          </div>
          <div class="conv-item-title">商品：{{ conv.itemTitle || '未知' }}</div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getConversations } from '../api/chat'

const router = useRouter()
const conversations = ref([])
const loading = ref(true)

async function fetchConversations() {
  loading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch { conversations.value = [] }
  finally { loading.value = false }
}

function openChatForItem(conv) {
  // 存下来让 ItemDetail 自动打开聊天，同时告诉它接收者是谁
  sessionStorage.setItem('openChat', JSON.stringify({ itemId: conv.itemId, receiverId: conv.otherUserId }))
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  if (d.toDateString() === now.toDateString()) {
    return `${pad(d.getHours())}:${pad(d.getMinutes())}`
  }
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

function onImgError(e) {
  e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><rect fill="%23f5f6f8" width="100" height="100"/></svg>'
}

onMounted(fetchConversations)
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

.loading-state { text-align: center; color: var(--color-muted); padding: var(--space-xxl); }
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }
.empty-icon { width: 80px; height: 80px; margin-bottom: var(--space-md); display: block; margin-left: auto; margin-right: auto; }

/* ── Conversation List ── */
.conversation-list {
  display: flex; flex-direction: column; gap: 2px;
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  overflow: hidden; background: var(--color-bg);
}
.conv-item {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; text-decoration: none; color: var(--color-ink);
  transition: background var(--duration-fast);
  border-bottom: 1px solid var(--color-divider);
}
.conv-item:last-child { border-bottom: none; }
.conv-item:hover { background: var(--color-surface); }
.conv-item.unread { background: #f0f3fb; }

.conv-img {
  width: 48px; height: 48px; border-radius: var(--radius-sm);
  object-fit: cover; flex-shrink: 0; background: var(--color-surface);
}

.conv-info { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2px; }
.conv-name { font-weight: 600; font-size: 0.9375rem; }
.conv-time { font-size: 0.75rem; color: var(--color-muted); flex-shrink: 0; }

.conv-bottom { display: flex; justify-content: space-between; align-items: center; }
.conv-preview {
  font-size: 0.8125rem; color: var(--color-muted);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  max-width: 420px;
}
.unread-badge {
  background: var(--color-danger); color: #fff;
  font-size: 0.6875rem; font-weight: 600;
  min-width: 18px; height: 18px; line-height: 18px;
  text-align: center; border-radius: 9px; padding: 0 5px;
  flex-shrink: 0;
}
.conv-item-title {
  font-size: 0.75rem; color: var(--color-muted); margin-top: 2px;
}

@media (max-width: 500px) {
  .messages-page { padding: var(--space-md); }
  .conv-item { padding: 12px; }
}
</style>
