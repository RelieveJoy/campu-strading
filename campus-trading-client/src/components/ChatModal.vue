<template>
  <teleport to="body">
    <transition name="modal-fade">
      <div class="chat-overlay" v-if="visible" @click.self="close">
        <div class="chat-panel">
          <!-- 头部 -->
          <div class="chat-header">
            <div class="chat-header-left">
              <span class="chat-avatar">{{ sellerName?.charAt(0) || '?' }}</span>
              <div>
                <h6 class="chat-seller">{{ sellerName }}</h6>
                <small class="chat-item-title">{{ itemTitle }}</small>
              </div>
            </div>
            <button class="chat-close" @click="close">&times;</button>
          </div>

          <!-- 消息列表 -->
          <div class="chat-body" ref="bodyRef">
            <div v-if="loading" class="chat-loading">加载中...</div>
            <template v-else>
              <div v-if="messages.length === 0" class="chat-empty">
                暂无消息，发送第一条消息开始沟通吧
              </div>
              <div
                v-for="msg in messages"
                :key="msg.id || msg.messageId"
                class="chat-msg"
                :class="{ mine: msg.senderId === myId }"
              >
                <div class="msg-bubble">
                  <span class="msg-meta">
                    <span class="msg-sender" v-if="msg.senderId !== myId && msg.senderName">{{ msg.senderName }}</span>
                    <span class="msg-time">{{ formatTime(msg.createTime || msg.createdAt) }}</span>
                  </span>
                  <p class="msg-text">{{ msg.content }}</p>
                </div>
              </div>
            </template>
          </div>

          <!-- 输入框 -->
          <div class="chat-footer">
            <input
              ref="inputRef"
              v-model="text"
              type="text"
              class="chat-input"
              placeholder="输入消息..."
              @keyup.enter="send"
              :disabled="sending"
            />
            <button class="chat-send" @click="send" :disabled="!text.trim() || sending">
              <span v-if="sending" class="spinner"></span>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, nextTick, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { getMessages, sendMessage, markRead } from '../api/chat'

const props = defineProps({
  itemId: { type: [Number, String], required: true },
  receiverId: { type: [Number, String], required: true },
  sellerName: { type: String, default: '卖家' },
  itemTitle: { type: String, default: '' },
})

const emit = defineEmits(['close'])
const visible = ref(false)
const loading = ref(false)
const sending = ref(false)
const messages = ref([])
const text = ref('')
const bodyRef = ref(null)
const inputRef = ref(null)
let stompClient = null

const myId = (() => {
  try { return JSON.parse(localStorage.getItem('user'))?.id || 0 } catch { return 0 }
})()

const token = (() => {
  return localStorage.getItem('token') || ''
})()

async function open() {
  visible.value = true
  await fetchMessages()
  connectWebSocket()
  // 打开聊天时标记对方发来的消息为已读
  markRead(props.itemId).catch(() => {})
}

function close() {
  disconnectWebSocket()
  visible.value = false
  emit('close')
}

// ── WebSocket ──
function connectWebSocket() {
  if (stompClient?.active) return
  if (!token) return

  const wsUrl = `${window.location.protocol}//${window.location.hostname}:8080/ws`
  const socket = new SockJS(`${wsUrl}?token=${token}`)
  stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient.subscribe(`/topic/chat.${myId}.${props.itemId}`, (msg) => {
        try {
          const body = JSON.parse(msg.body)
          if (body.senderId !== myId) {
            messages.value.push(body)
            nextTick(() => scrollToBottom())
          }
        } catch { /* ignore */ }
      })
    },
    onStompError: () => { /* 静默 */ },
  })
  stompClient.activate()
}

function disconnectWebSocket() {
  if (stompClient?.active) {
    stompClient.deactivate()
  }
  stompClient = null
}

onUnmounted(() => disconnectWebSocket())

// ── REST ──
async function fetchMessages() {
  loading.value = true
  try {
    const res = await getMessages(props.itemId, Number(props.receiverId))
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
  } catch { messages.value = [] }
  finally { loading.value = false }
}

async function send() {
  const content = text.value.trim()
  if (!content || sending.value) return
  sending.value = true
  try {
    const res = await sendMessage({ itemId: props.itemId, receiverId: Number(props.receiverId), content })
    text.value = ''
    // 用服务端返回的完整消息替换乐观插入
    const msg = res.data || {
      id: Date.now(), senderId: myId, content,
      createTime: new Date().toISOString(),
    }
    messages.value.push(msg)
    await nextTick()
    scrollToBottom()
  } catch { ElMessage.error('发送失败') }
  finally {
    sending.value = false
    await nextTick()
    inputRef.value?.focus()
  }
}

function scrollToBottom() {
  if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const pad = (n) => String(n).padStart(2, '0')
  return `${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

defineExpose({ open, close })
</script>

<style scoped>
.chat-overlay {
  position: fixed; inset: 0; z-index: var(--z-modal);
  background: rgba(0,0,0,0.35); display: flex;
  align-items: center; justify-content: center;
  padding: var(--space-lg);
}
.chat-panel {
  width: 100%; max-width: 480px; max-height: 80vh;
  background: var(--color-bg); border-radius: var(--radius-lg);
  display: flex; flex-direction: column; overflow: hidden;
  box-shadow: var(--shadow-ambient-high);
}

/* Header */
.chat-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 16px; border-bottom: 1px solid var(--color-divider);
  background: var(--color-primary); color: #fff;
}
.chat-header-left { display: flex; align-items: center; gap: 10px; }
.chat-avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: rgba(255,255,255,0.25); display: flex;
  align-items: center; justify-content: center;
  font-weight: 700; font-size: 0.9375rem; color: #fff; flex-shrink: 0;
}
.chat-seller { margin: 0; font-size: 0.9375rem; font-weight: 600; }
.chat-item-title { opacity: 0.8; font-size: 0.75rem; }
.chat-close {
  background: none; border: none; color: #fff; font-size: 1.5rem;
  cursor: pointer; opacity: 0.8; line-height: 1;
}
.chat-close:hover { opacity: 1; }

/* Body */
.chat-body {
  flex: 1; overflow-y: auto; padding: 12px 16px;
  min-height: 200px; max-height: 400px; display: flex; flex-direction: column; gap: 8px;
}
.chat-loading, .chat-empty { text-align: center; color: var(--color-muted); font-size: 0.8125rem; padding: 24px; }

/* Messages */
.chat-msg { display: flex; }
.chat-msg.mine { justify-content: flex-end; }
.msg-bubble {
  max-width: 75%; padding: 8px 12px; border-radius: 14px;
  font-size: 0.875rem; line-height: 1.5;
}
.chat-msg:not(.mine) .msg-bubble {
  background: var(--color-surface); color: var(--color-ink);
  border-bottom-left-radius: 4px;
}
.chat-msg.mine .msg-bubble {
  background: var(--color-primary); color: #fff;
  border-bottom-right-radius: 4px;
}
.msg-text { margin: 0; word-break: break-word; }
.msg-meta {
  font-size: 0.625rem; opacity: 0.6; margin-bottom: 2px;
  display: flex; gap: 6px; align-items: center;
}
.msg-sender { font-weight: 500; opacity: 1; }
.msg-time { opacity: 1; }

/* Footer */
.chat-footer {
  display: flex; gap: 8px; padding: 10px 12px;
  border-top: 1px solid var(--color-divider); background: var(--color-surface);
}
.chat-input {
  flex: 1; padding: 8px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-full); font-size: 0.8125rem;
  font-family: var(--font-family); outline: none;
  background: var(--color-bg); color: var(--color-ink);
}
.chat-input:focus { border-color: var(--color-primary); }
.chat-send {
  width: 38px; height: 38px; border-radius: 50%;
  background: var(--color-primary); color: #fff; border: none;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; flex-shrink: 0;
  transition: background var(--duration-fast);
}
.chat-send:hover { background: var(--color-primary-hover); }
.chat-send:disabled { opacity: 0.5; cursor: not-allowed; }

.spinner {
  width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* Transition */
.modal-fade-enter-active { transition: opacity var(--duration-normal); }
.modal-fade-enter-active .chat-panel { transition: transform var(--duration-normal) var(--ease-out); }
.modal-fade-leave-active { transition: opacity var(--duration-fast); }
.modal-fade-enter-from { opacity: 0; }
.modal-fade-enter-from .chat-panel { transform: translateY(12px) scale(0.97); }
.modal-fade-leave-to { opacity: 0; }

@media (max-width: 500px) {
  .chat-panel { max-width: 100%; max-height: 90vh; border-radius: 0; }
  .chat-overlay { padding: 0; align-items: flex-end; }
}
</style>
