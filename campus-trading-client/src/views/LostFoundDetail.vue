<template>
  <div class="lfd-page">
    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="!detail" class="empty-state">
      <h2>信息不存在</h2>
      <button class="btn-back" @click="$router.push('/lostfound')">返回列表</button>
    </div>
    <div v-else class="lfd-container">
      <!-- 面包屑 -->
      <div class="lfd-breadcrumb">
        <router-link to="/">首页</router-link> /
        <router-link to="/lostfound">失物招领</router-link> /
        <span>{{ detail.title }}</span>
      </div>

      <div class="lfd-main">
        <!-- 图片 -->
        <div class="lfd-image" v-if="detail.imageUrl">
          <img :src="detail.imageUrl" :alt="detail.title" @error="onImgError" />
        </div>

        <!-- 信息卡片 -->
        <div class="lfd-card">
          <div class="lfd-header">
            <h2>{{ detail.title }}</h2>
            <span class="lfd-cat" :class="detail.category">{{ detail.category === 'lost' ? '寻物' : '招领' }}</span>
          </div>

          <div class="lfd-meta">
            <div class="lfd-meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              <span>{{ detail.location || '未提供' }}</span>
            </div>
            <div class="lfd-meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              <span>{{ detail.userName || '匿名' }}</span>
            </div>
            <div class="lfd-meta-item">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              <span>{{ detail.createTime }}</span>
            </div>
          </div>

          <div class="lfd-contact" v-if="detail.contact">
            <strong>联系方式：</strong>{{ detail.contact }}
          </div>

          <div class="lfd-desc" v-if="detail.description">
            <strong>详细描述：</strong>
            <p>{{ detail.description }}</p>
          </div>

          <div class="lfd-actions">
            <button class="lfd-btn" @click="openChat">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg>
              联系TA
            </button>
            <button class="lfd-btn-outline" @click="reportModalRef?.open()">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/></svg>
              举报
            </button>
          </div>
        </div>
      </div>
    </div>

    <ChatModal
      ref="chatRef"
      :item-id="detail?.lostFoundId || 0"
      :receiver-id="detail?.userId || 0"
      :contact-name="detail?.userName || ''"
      :item-title="detail?.title || ''"
      source-type="lostfound"
    />
    <ReportModal ref="reportModalRef" target-type="lostfound" :target-id="detail?.lostFoundId || 0" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getLostFoundDetail } from '../api/lostfound'
import ChatModal from '../components/ChatModal.vue'
import ReportModal from '../components/ReportModal.vue'

const route = useRoute()
const detail = ref(null)
const loading = ref(true)
const chatRef = ref(null)
const reportModalRef = ref(null)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getLostFoundDetail(route.params.id)
    detail.value = res.data || null
  } catch { detail.value = null }
  finally { loading.value = false }
}

function openChat() {
  chatRef.value?.open()
}

function onImgError(e) {
  e.target.style.display = 'none'
}

onMounted(() => {
  fetchDetail()
  // 从"我的消息"跳过来时自动打开聊天
  const raw = sessionStorage.getItem('openChat')
  if (raw) {
    try {
      const data = JSON.parse(raw)
      if (String(data.itemId) === String(route.params.id) && data.sourceType === 'lostfound') {
        sessionStorage.removeItem('openChat')
        setTimeout(() => chatRef.value?.open(), 300)
      }
    } catch { sessionStorage.removeItem('openChat') }
  }
})
watch(() => route.params.id, fetchDetail)
</script>

<style scoped>
.lfd-page { background: #f5f5f5; min-height: calc(100vh - 56px); padding: 24px 0; }
.lfd-container { max-width: 900px; margin: 0 auto; padding: 0 16px; }

.lfd-breadcrumb {
  font-size: 0.8125rem; color: var(--color-muted); margin-bottom: 16px;
}
.lfd-breadcrumb a { color: var(--color-muted); text-decoration: none; }
.lfd-breadcrumb a:hover { color: var(--color-primary); }

.lfd-main { display: flex; gap: 24px; align-items: flex-start; }

.lfd-image {
  width: 400px; flex-shrink: 0; border-radius: var(--radius-md);
  overflow: hidden; background: var(--color-surface);
}
.lfd-image img { width: 100%; height: 360px; object-fit: cover; }

.lfd-card {
  flex: 1; background: #fff; border-radius: var(--radius-md);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04); padding: 24px;
}
.lfd-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.lfd-header h2 { font-size: 1.25rem; font-weight: 700; color: var(--color-ink); margin: 0; flex: 1; }
.lfd-cat { font-size: 0.75rem; padding: 4px 12px; border-radius: var(--radius-sm); }
.lfd-cat.lost { background: var(--color-warning-light); color: var(--color-warning); }
.lfd-cat.found { background: #e8edf8; color: var(--color-primary); }

.lfd-meta { display: flex; flex-direction: column; gap: 8px; margin-bottom: 16px; }
.lfd-meta-item { display: flex; align-items: center; gap: 8px; font-size: 0.875rem; color: var(--color-muted); }

.lfd-contact { font-size: 0.875rem; color: var(--color-ink); margin-bottom: 12px; padding: 10px; background: var(--color-surface); border-radius: var(--radius-sm); }
.lfd-desc { margin-bottom: 20px; }
.lfd-desc p { font-size: 0.875rem; color: var(--color-muted); line-height: 1.7; margin: 8px 0 0; }

.lfd-actions { display: flex; gap: 12px; }
.lfd-btn {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 28px; background: var(--color-primary); color: #fff;
  border: none; border-radius: var(--radius-md); font-size: 0.9375rem;
  font-family: var(--font-family); cursor: pointer;
  transition: background var(--duration-fast);
}
.lfd-btn:hover { background: var(--color-primary-hover); }
.lfd-btn-outline {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 28px; background: var(--color-bg); color: var(--color-muted);
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  font-size: 0.9375rem; font-family: var(--font-family); cursor: pointer;
  transition: all var(--duration-fast);
}
.lfd-btn-outline:hover { border-color: var(--color-danger); color: var(--color-danger); }

.loading-state { text-align: center; color: var(--color-muted); padding: var(--space-xxl); }
.empty-state { text-align: center; padding: var(--space-xxl); color: var(--color-muted); }
.empty-state h2 { margin-bottom: 16px; color: var(--color-ink); }
.btn-back { padding: 10px 24px; background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-md); cursor: pointer; font-family: var(--font-family); }

@media (max-width: 768px) {
  .lfd-main { flex-direction: column; }
  .lfd-image { width: 100%; }
  .lfd-image img { height: 240px; }
}
</style>
