<template>
  <div class="detail-page">
    <!-- ── Loading ── -->
    <div v-if="loading" class="detail-loading">
      <div class="skeleton-detail">
        <div class="skel-image"></div>
        <div class="skel-info">
          <div class="skel-line w-60 lg"></div>
          <div class="skel-line w-30 xl"></div>
          <div class="skel-line w-40"></div>
          <div class="skel-line w-80"></div>
          <div class="skel-line w-20"></div>
        </div>
      </div>
    </div>

    <!-- ── Error ── -->
    <div v-else-if="!detail" class="empty-state">
      <h2>商品不存在</h2>
      <p>该商品可能已下架或删除</p>
      <button class="btn-primary" @click="$router.push('/')">返回首页</button>
    </div>

    <!-- ── Content ── -->
    <template v-else>
      <button class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
        返回
      </button>

      <div class="detail-grid">
        <!-- ── Image ── -->
        <div class="detail-gallery">
          <div class="main-image">
            <img v-if="detail.imageUrl" :src="detail.imageUrl" :alt="detail.title" />
            <div v-else class="img-placeholder">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/>
                <path d="m21 15-5-5L5 21"/>
              </svg>
              <span>暂无图片</span>
            </div>
          </div>
          <!-- Status overlay on image -->
          <span v-if="detail.status === 2" class="status-badge sold">已售出</span>
          <span v-else-if="detail.status === 0" class="status-badge offline">已下架</span>
        </div>

        <!-- ── Info ── -->
        <div class="detail-info">
          <div class="info-header">
            <span class="category-tag">{{ detail.categoryName || '未分类' }}</span>
            <h1 class="detail-title">{{ detail.title }}</h1>
          </div>

          <div class="price-block">
            <span class="price-current">¥{{ detail.price }}</span>
            <span v-if="detail.originalPrice" class="price-original">原价 ¥{{ detail.originalPrice }}</span>
          </div>

          <!-- ── Seller Card ── -->
          <div class="seller-card">
            <span class="seller-avatar">{{ (detail.sellerName || '?').charAt(0) }}</span>
            <div class="seller-info">
              <span class="seller-name">{{ detail.sellerName }}</span>
              <span class="seller-phone" v-if="detail.sellerPhone">{{ detail.sellerPhone }}</span>
            </div>
          </div>

          <div class="meta-row">
            <span><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="meta-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>{{ detail.createTime }}</span>
            <span><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="meta-icon"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>{{ detail.viewCount || 0 }} 次浏览</span>
          </div>

          <!-- ── Description ── -->
          <div class="desc-section" v-if="detail.description">
            <h3>商品描述</h3>
            <p>{{ detail.description }}</p>
          </div>
          <div class="desc-section empty-desc" v-else>
            <p class="muted-text">卖家暂无描述</p>
          </div>

          <!-- ── Actions ── -->
          <div class="action-bar" v-if="!isOwner">
            <button
              class="btn-fav"
              :class="{ favorited: isFaved }"
              @click="toggleFavorite"
              :disabled="favLoading"
            >
              <svg viewBox="0 0 24 24" :fill="isFaved ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
              </svg>
              {{ isFaved ? '已收藏' : '收藏' }}
            </button>
            <button
              class="btn-buy"
              :disabled="detail.status !== 1 || buyLoading"
              @click="handleBuy"
            >
              <span v-if="buyLoading" class="spinner"></span>
              {{ detail.status === 1 ? '立即购买' : detail.status === 2 ? '已售出' : '已下架' }}
            </button>
          </div>
          <div class="action-bar owner-hint" v-else>
            <span class="owner-tag">这是我发布的商品</span>
            <button class="btn-edit" @click="$router.push(`/edit/${detail.itemId}`)">编辑商品</button>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getItemDetail } from '../api/item'
import { createOrder } from '../api/order'
import { addFavorite, checkFavorite, deleteFavorite, getFavorites } from '../api/favorite'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')
const route = useRoute()
const router = useRouter()

const detail = ref(null)
const loading = ref(true)
const isFaved = ref(false)
const favLoading = ref(false)
const buyLoading = ref(false)
const isOwner = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getItemDetail(route.params.id)
    detail.value = res.data
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id === detail.value.sellerId) isOwner.value = true
  } catch (e) {
    detail.value = null
  } finally {
    loading.value = false
  }
}

async function checkFav() {
  try {
    const res = await checkFavorite(route.params.id)
    isFaved.value = res.data
  } catch (e) { /* ignore */ }
}

async function toggleFavorite() {
  if (!loggedIn.value) { requireLogin(); return }
  favLoading.value = true
  try {
    if (isFaved.value) {
      const res = await getFavorites()
      const fav = res.data.find((f) => f.itemId === detail.value.itemId)
      if (fav) await deleteFavorite(fav.favoriteId)
      ElMessage.success('已取消收藏')
    } else {
      await addFavorite({ itemId: detail.value.itemId, userId: 0 })
      ElMessage.success('收藏成功')
    }
    isFaved.value = !isFaved.value
  } catch (e) { /* handled */ }
  finally { favLoading.value = false }
}

async function handleBuy() {
  if (!loggedIn.value) { requireLogin(); return }
  if (detail.value.status !== 1) return
  buyLoading.value = true
  try {
    await createOrder({ itemId: detail.value.itemId })
    ElMessage.success('下单成功！请在我的订单中查看')
    await fetchDetail()
  } catch (e) { /* handled */ }
  finally { buyLoading.value = false }
}

onMounted(() => {
  fetchDetail()
  checkFav()
})
</script>

<style scoped>
.detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0;
}

/* ── Back button ── */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 0;
  margin-bottom: var(--space-lg);
  background: none;
  border: none;
  color: var(--color-muted);
  font-size: var(--font-body-size);
  font-family: var(--font-family);
  cursor: pointer;
  transition: color var(--duration-fast);
}

.back-btn svg { width: 18px; height: 18px; }
.back-btn:hover { color: var(--color-ink); }

/* ── Loading Skeleton ── */
.skeleton-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-xl);
}

.skel-image {
  aspect-ratio: 4 / 3;
  background: var(--color-surface);
  border-radius: var(--radius-md);
  animation: shimmer 1.5s infinite;
}

.skel-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.skel-line {
  height: 16px;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  animation: shimmer 1.5s infinite;
}

.skel-line.lg { height: 24px; }
.skel-line.xl { height: 36px; }
.skel-line.w-20 { width: 20%; }
.skel-line.w-30 { width: 30%; }
.skel-line.w-40 { width: 40%; }
.skel-line.w-60 { width: 60%; }
.skel-line.w-80 { width: 80%; }

@keyframes shimmer {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ── Grid ── */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-xl);
  align-items: start;
}

/* ── Gallery ── */
.detail-gallery {
  position: relative;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--color-surface);
}

.main-image {
  aspect-ratio: 4 / 3;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  color: var(--color-muted);
  font-size: var(--font-body-size);
  background: var(--color-surface);
}

.img-placeholder svg {
  width: 56px;
  height: 56px;
}

.status-badge {
  position: absolute;
  top: var(--space-md);
  right: var(--space-md);
  padding: 4px 14px;
  border-radius: var(--radius-full);
  font-size: var(--font-label-size);
  font-weight: 600;
  backdrop-filter: blur(8px);
}

.status-badge.sold {
  background: rgba(0,0,0,0.7);
  color: white;
}

.status-badge.offline {
  background: rgba(255,255,255,0.85);
  color: var(--color-muted);
}

/* ── Info ── */
.detail-info {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.info-header {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.category-tag {
  display: inline-block;
  width: fit-content;
  padding: 3px 12px;
  background: var(--color-accent-light);
  color: var(--color-accent);
  border-radius: var(--radius-full);
  font-size: var(--font-label-size);
  font-weight: 500;
}

.detail-title {
  font-size: var(--font-display-size);
  font-weight: var(--font-display-weight);
  line-height: var(--font-display-line);
  color: var(--color-ink);
}

/* ── Price ── */
.price-block {
  display: flex;
  align-items: baseline;
  gap: var(--space-md);
}

.price-current {
  font-size: 2rem;
  font-weight: 700;
  color: var(--color-primary);
}

.price-original {
  font-size: var(--font-body-size);
  color: var(--color-muted);
  text-decoration: line-through;
}

/* ── Seller Card ── */
.seller-card {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  background: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-divider);
}

.seller-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-primary-light);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.125rem;
  flex-shrink: 0;
}

.seller-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.seller-name {
  font-weight: 600;
  color: var(--color-ink);
  font-size: var(--font-body-size);
}

.seller-phone {
  font-size: var(--font-label-size);
  color: var(--color-muted);
}

/* ── Meta Row ── */
.meta-row {
  display: flex;
  gap: var(--space-lg);
  font-size: var(--font-label-size);
  color: var(--color-muted);
}

.meta-row span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-icon {
  width: 14px;
  height: 14px;
}

/* ── Description ── */
.desc-section {
  padding: var(--space-md);
  background: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-divider);
}

.desc-section h3 {
  font-size: var(--font-title-size);
  font-weight: var(--font-title-weight);
  margin-bottom: var(--space-sm);
  color: var(--color-ink);
}

.desc-section p {
  font-size: var(--font-body-size);
  line-height: var(--font-body-line);
  color: var(--color-ink);
  white-space: pre-wrap;
}

.empty-desc {
  padding: var(--space-md);
}

.muted-text {
  color: var(--color-muted);
  font-style: italic;
}

/* ── Action Bar ── */
.action-bar {
  display: flex;
  gap: var(--space-md);
  margin-top: var(--space-sm);
}

.btn-fav {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 22px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background: var(--color-bg);
  color: var(--color-ink);
  font-size: var(--font-body-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
}

.btn-fav svg { width: 18px; height: 18px; }

.btn-fav:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-fav.favorited {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.btn-fav:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-buy {
  flex: 1;
  padding: 10px 0;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 600;
  font-family: var(--font-family);
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
}

.btn-buy:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.btn-buy:disabled {
  background: var(--color-surface);
  color: var(--color-muted);
  cursor: not-allowed;
}

.spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* ── Owner hint ── */
.owner-hint {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  background: var(--color-primary-light);
  border-radius: var(--radius-md);
}

.owner-tag {
  font-size: var(--font-label-size);
  color: var(--color-primary);
  font-weight: 500;
}

.btn-edit {
  margin-left: auto;
  padding: 6px 18px;
  background: var(--color-bg);
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm);
  font-size: var(--font-label-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.btn-edit:hover {
  background: var(--color-primary);
  color: var(--color-primary-text);
}

/* ── Empty state ── */
.empty-state {
  text-align: center;
  padding: var(--space-xxl) var(--space-lg);
}

.empty-state h2 {
  font-size: var(--font-headline-size);
  margin-bottom: var(--space-sm);
}

.empty-state p {
  color: var(--color-muted);
  margin-bottom: var(--space-lg);
}

.btn-primary {
  padding: 10px 28px;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

/* ── Responsive ── */
@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
