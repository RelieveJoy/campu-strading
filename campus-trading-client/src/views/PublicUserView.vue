<template>
  <div class="public-page">
    <div v-if="loading" class="loading-state">加载中...</div>
    <div v-else-if="!profile" class="empty-state">
      <h2>用户不存在</h2>
      <button class="btn-primary" @click="$router.push('/')">返回首页</button>
    </div>
    <div v-else class="public-layout">
      <div class="public-main">
      <!-- 头像区 -->
      <div class="profile-card">
        <div class="avatar-wrap">
          <div class="avatar">{{ (profile.username || '?')[0] }}</div>
        </div>
        <div class="profile-info">
          <h2 class="profile-name">{{ profile.username }}</h2>
          <div class="profile-meta">
            <span>{{ profile.totalItems || 0 }} 在售</span>
            <span class="meta-sep">|</span>
            <span>{{ profile.totalSold || 0 }} 已售</span>
            <span class="meta-sep">|</span>
            <span>
              <RatingStars v-if="profile.overallRating > 0" :average-rating="profile.overallRating" :review-count="profile.ratedItemCount" mode="readonly" />
              <span v-else>暂无评分</span>
            </span>
          </div>
        </div>
      </div>

      <!-- Tab 栏 -->
      <div class="tab-bar">
        <button class="tab-item" :class="{ active: activeTab === 'items' }" @click="switchTab('items')">
          宝贝<span class="tab-underline" v-if="activeTab === 'items'"></span>
        </button>
        <button class="tab-item" :class="{ active: activeTab === 'reviews' }" @click="switchTab('reviews')">
          信用及评价<span class="tab-underline" v-if="activeTab === 'reviews'"></span>
        </button>
      </div>

      <!-- 宝贝 -->
      <template v-if="activeTab === 'items'">
        <div v-if="itemsLoading" class="loading-state">加载中...</div>
        <div v-else-if="items.length === 0" class="empty-box">
          <p>暂无商品</p>
        </div>
        <div v-else>
          <div class="goods-grid">
            <GoodsCard v-for="item in items" :key="item.id || item.itemId" :goods="item" />
          </div>
          <div class="pagination-wrap" v-if="itemsTotal > pageSize">
            <el-pagination :current-page="itemsPage" :page-size="pageSize" :total="itemsTotal"
              layout="prev, pager, next" @current-change="onPageChange" background />
          </div>
        </div>
      </template>

      <!-- 信用及评价 -->
      <template v-if="activeTab === 'reviews'">
        <div v-if="reviewsLoading" class="loading-state">加载中...</div>
        <div v-else-if="reviews.length === 0" class="empty-box">
          <p>暂无评价</p>
        </div>
        <div v-else>
          <div class="review-list">
            <div v-for="r in reviews" :key="r.reviewId" class="review-card">
              <div class="review-top">
                <span class="reviewer-avatar">{{ (r.reviewerName || '?')[0] }}</span>
                <div class="reviewer-info">
                  <span class="reviewer-name">{{ r.reviewerName }}</span>
                  <span class="review-time">{{ r.createTime }}</span>
                </div>
                <span class="review-stars">
                  <i v-for="i in 5" :key="i" class="bi" :class="i <= r.rating ? 'bi-star-fill' : 'bi-star'" :style="{ color: i <= r.rating ? '#ffb800' : '#ddd', fontSize: '0.75rem' }"></i>
                </span>
              </div>
              <p class="review-content" v-if="r.content">{{ r.content }}</p>
              <span class="review-item-title" v-if="r.itemTitle">商品：{{ r.itemTitle }}</span>
            </div>
          </div>
          <div class="pagination-wrap" v-if="reviewsTotal > reviewPageSize">
            <el-pagination :current-page="reviewsPage" :page-size="reviewPageSize" :total="reviewsTotal"
              layout="prev, pager, next" @current-change="onReviewsPageChange" background />
          </div>
        </div>
      </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getUserProfile, getUserItems } from '../api/user'
import { getUserReviewList } from '../api/review'
import GoodsCard from '../components/GoodsCard.vue'
import RatingStars from '../components/RatingStars.vue'

const route = useRoute()
const userId = () => Number(route.params.id)

const activeTab = ref('items')
const profile = ref(null)
const loading = ref(true)

const items = ref([])
const itemsLoading = ref(true)
const itemsTotal = ref(0)
const itemsPage = ref(1)
const pageSize = 8

const reviews = ref([])
const reviewsLoading = ref(true)
const reviewsTotal = ref(0)
const reviewsPage = ref(1)
const reviewPageSize = 10

async function fetchProfile() {
  loading.value = true
  try {
    const res = await getUserProfile(userId())
    profile.value = res.data || null
  } catch { profile.value = null }
  finally { loading.value = false }
}

async function fetchItems(status, page = 1) {
  itemsLoading.value = true
  try {
    const params = { page, pageSize }
    if (status != null) params.status = status
    const res = await getUserItems(userId(), params)
    const data = res.data
    items.value = (data?.records || data || []).map(normalizeItem)
    itemsTotal.value = data?.total || 0
    itemsPage.value = page
  } catch { items.value = []; itemsTotal.value = 0 }
  finally { itemsLoading.value = false }
}

async function fetchReviews(page = 1) {
  reviewsLoading.value = true
  try {
    const res = await getUserReviewList(userId(), { page, pageSize: reviewPageSize })
    const data = res.data
    reviews.value = data?.records || data || []
    reviewsTotal.value = data?.total || 0
    reviewsPage.value = page
  } catch { reviews.value = []; reviewsTotal.value = 0 }
  finally { reviewsLoading.value = false }
}

function normalizeItem(i) {
  return {
    ...i,
    id: i.id || i.itemId,
    image: i.image || i.imageUrl || i.itemImageUrl,
    title: i.title || i.name || i.itemTitle,
    price: i.price || i.itemPrice,
    condition: i.condition || i.quality,
    views: i.views || i.viewCount,
    location: i.location || i.address,
    averageRating: i.averageRating || 0,
    reviewCount: i.reviewCount || 0,
  }
}

function switchTab(tab) {
  activeTab.value = tab
  if (tab === 'items') fetchItems(1)
  else if (tab === 'reviews') fetchReviews(1)
}

function onPageChange(p) {
  fetchItems(1, p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function onReviewsPageChange(p) {
  fetchReviews(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  fetchProfile()
  fetchItems(1)
})

watch(() => route.params.id, () => {
  activeTab.value = 'items'
  itemsPage.value = 1
  reviewsPage.value = 1
  fetchProfile()
  fetchItems(1)
})
</script>

<style scoped>
/* ── 完全照搬 UserHomeView 右侧框架 ── */
.public-page { background: #f5f5f5; min-height: calc(100vh - 56px); }
.public-layout { max-width: 1600px; margin: 0 auto; padding: 20px 16px; }

.public-main {
  max-width: 1300px; margin: 0 auto;
  background: #fff; border-radius: var(--radius-md);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  padding: 0 24px 24px;
}

/* ── Profile Card ── */
.profile-card {
  display: flex; flex-direction: column; align-items: center;
  padding: 24px 0 16px;
}
.avatar-wrap { margin-bottom: 10px; }
.avatar {
  width: 96px; height: 96px; border-radius: 50%;
  background: var(--color-primary); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 2.5rem; font-weight: 700;
}
.profile-info { text-align: center; }
.profile-name { font-size: 1.375rem; font-weight: 700; color: var(--color-ink); margin: 0 0 6px 0; }
.profile-meta {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  font-size: 0.8125rem; color: var(--color-muted);
}
.meta-sep { color: var(--color-border); }

/* ── Tab Bar ── */
.tab-bar {
  display: flex; justify-content: center; gap: 0;
  border-bottom: 1px solid var(--color-divider);
  margin-bottom: 20px;
}
.tab-item {
  position: relative; padding: 12px 28px; border: none;
  background: transparent; color: var(--color-muted);
  font-size: 0.9375rem; font-weight: 500; cursor: pointer;
  font-family: var(--font-family);
  transition: color var(--duration-fast);
}
.tab-item:hover { color: var(--color-ink); }
.tab-item.active { color: var(--color-primary); font-weight: 600; }
.tab-underline {
  position: absolute; bottom: -1px; left: 50%; transform: translateX(-50%);
  width: 32px; height: 3px; border-radius: 2px; background: var(--color-primary);
}

/* ── Grid ── */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

/* ── Reviews ── */
.review-list { display: flex; flex-direction: column; gap: 2px; }
.review-card { padding: 14px 0; border-bottom: 1px solid var(--color-divider); }
.review-card:last-child { border-bottom: none; }
.review-top { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.reviewer-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--color-accent-light); color: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8125rem; font-weight: 700; flex-shrink: 0;
}
.reviewer-info { flex: 1; }
.reviewer-name { display: block; font-weight: 600; font-size: 0.875rem; color: var(--color-ink); }
.review-time { font-size: 0.75rem; color: var(--color-muted); }
.review-stars { display: flex; gap: 1px; flex-shrink: 0; }
.review-content { font-size: 0.875rem; color: var(--color-ink); line-height: 1.6; margin: 0; }
.review-item-title { font-size: 0.75rem; color: var(--color-muted); display: block; margin-top: 4px; }

/* ── States ── */
.loading-state { text-align: center; color: var(--color-muted); padding: var(--space-xxl); }
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }
.empty-box { text-align: center; padding: 48px; color: var(--color-muted); }
.empty-state h2 { font-size: 1.25rem; margin-bottom: var(--space-sm); color: var(--color-ink); }
.btn-primary { padding: 10px 28px; background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-md); font-size: 0.875rem; font-weight: 500; font-family: var(--font-family); cursor: pointer; }

.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; padding-bottom: 8px; }

@media (max-width: 768px) {
  .goods-grid { grid-template-columns: repeat(2, 1fr); }
  .public-main { margin-top: 0; border-radius: 0; }
}
</style>
