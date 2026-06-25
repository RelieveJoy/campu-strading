<template>
  <div class="home">
    <!-- ── 轮播图 ── -->
    <BannerCarousel :banners="banners" />

    <!-- ── 分类入口 ── -->
    <section class="section">
      <CategoryGrid :categories="categories" />
    </section>

    <!-- ── 最新上架 ── -->
    <section class="section">
      <div class="section-header">
        <h5 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" class="section-icon"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          最新上架
        </h5>
        <router-link to="/goods?sort=newest" class="section-more">
          查看更多
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
        </router-link>
      </div>

      <!-- Loading -->
      <div v-if="latestLoading" class="goods-grid">
        <div v-for="n in 8" :key="n" class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-lines">
            <div class="skeleton-line w-80"></div>
            <div class="skeleton-line w-40"></div>
          </div>
        </div>
      </div>

      <!-- Empty -->
      <div v-else-if="latestItems.length === 0" class="empty-mini">
        <p>暂无商品，<router-link to="/publish">去发布</router-link></p>
      </div>

      <!-- Grid -->
      <div v-else class="goods-grid">
        <GoodsCard v-for="item in latestItems" :key="item.id || item.itemId" :goods="item" />
      </div>
    </section>

    <!-- ── 热门商品 ── -->
    <section class="section bg-light">
      <div class="section-header">
        <h5 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" class="section-icon fire"><path d="M18.816 13.58c2.292 2.138 3.546 5.42 2.207 8.326-1.231 2.672-4.082 4.094-7.023 4.094-4.416 0-8-3.584-8-8 0-3.58 2.058-7.001 5.976-10.284l1.972 1.972c1.2 1.2 1.48 2.1 1.48 3 0 .829-.488 1.664-.976 1.828-.44.147-.78-.118-.78-.656 0-.196-.416-.488-.416-.488s-.822-.538-.878-1.074c-.1-.98.567-1.147.567-1.147 2.058.627 4.87 3.367 4.87 6.429z"/></svg>
          热门商品
        </h5>
        <router-link to="/goods?sort=hottest" class="section-more">
          查看更多
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
        </router-link>
      </div>

      <div v-if="hotLoading" class="goods-grid">
        <div v-for="n in 4" :key="n" class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-lines">
            <div class="skeleton-line w-80"></div>
            <div class="skeleton-line w-40"></div>
          </div>
        </div>
      </div>

      <div v-else-if="hotItems.length === 0" class="empty-mini">
        <p>暂无热门商品</p>
      </div>

      <div v-else class="goods-grid">
        <GoodsCard v-for="item in hotItems" :key="item.id || item.itemId" :goods="item" />
      </div>
    </section>

    <!-- ── 最新招领 ── -->
    <section class="section">
      <div class="section-header">
        <h5 class="section-title">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20" class="section-icon success"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/><line x1="8" y1="11" x2="14" y2="11"/></svg>
          最新招领
        </h5>
        <router-link to="/lostfound" class="section-more">
          查看更多
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
        </router-link>
      </div>

      <div v-if="lostFoundLoading" class="goods-grid">
        <div v-for="n in 4" :key="n" class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-lines">
            <div class="skeleton-line w-80"></div>
            <div class="skeleton-line w-40"></div>
          </div>
        </div>
      </div>

      <div v-else-if="lostFoundItems.length === 0" class="empty-mini">
        <p>暂无招领信息</p>
      </div>

      <div v-else class="lostfound-mini-grid">
        <router-link
          v-for="lf in lostFoundItems"
          :key="lf.id"
          :to="`/lostfound/${lf.id}`"
          class="lostfound-mini-card"
        >
          <img :src="lf.image || lf.images?.[0]" :alt="lf.title" @error="onImgError" />
          <div class="lf-body">
            <div class="lf-header">
              <h6>{{ lf.title }}</h6>
              <span class="lf-badge" :class="lf.status === 'closed' ? 'closed' : 'open'">
                {{ lf.status === 'closed' ? '已归还' : '可认领' }}
              </span>
            </div>
            <small class="lf-location">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              {{ lf.location || '校内' }}
            </small>
          </div>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getItems } from '../api/item'
import { getBanners } from '../api/banner'
import { getLostFounds } from '../api/lostfound'
import BannerCarousel from '../components/BannerCarousel.vue'
import CategoryGrid from '../components/CategoryGrid.vue'
import GoodsCard from '../components/GoodsCard.vue'

// ── 轮播图 ──
const banners = ref([])
onMounted(async () => {
  try {
    const res = await getBanners()
    if (res?.data?.length) banners.value = res.data
  } catch {
    // 默认占位 banner
    banners.value = []
  }
})

// ── 分类 ──
const categories = [
  { id: 1, name: '书籍教材', icon: 'bi bi-book' },
  { id: 2, name: '数码电子', icon: 'bi bi-phone' },
  { id: 3, name: '生活用品', icon: 'bi bi-house-heart' },
  { id: 4, name: '服装鞋帽', icon: 'bi bi-bag' },
  { id: 5, name: '运动健身', icon: 'bi bi-bicycle' },
  { id: 6, name: '美妆护肤', icon: 'bi bi-stars' },
  { id: 7, name: '游戏娱乐', icon: 'bi bi-controller' },
  { id: 8, name: '其他闲置', icon: 'bi bi-grid' },
]

// ── 最新上架 ──
const latestItems = ref([])
const latestLoading = ref(true)
onMounted(async () => {
  try {
    const res = await getItems({ page: 1, pageSize: 8, status: 1, sort: 'newest' })
    latestItems.value = (res?.data?.records || []).map(normalize)
  } catch { latestItems.value = [] }
  finally { latestLoading.value = false }
})

// ── 热门商品 ──
const hotItems = ref([])
const hotLoading = ref(true)
onMounted(async () => {
  try {
    const res = await getItems({ page: 1, pageSize: 8, sort: 'hottest' })
    hotItems.value = (res?.data?.records || []).map(normalize)
  } catch { hotItems.value = [] }
  finally { hotLoading.value = false }
})

// ── 最新招领 ──
const lostFoundItems = ref([])
const lostFoundLoading = ref(true)
onMounted(async () => {
  try {
    const res = await getLostFounds({ page: 1, pageSize: 8 })
    lostFoundItems.value = res?.data?.records || res?.data || []
  } catch { lostFoundItems.value = [] }
  finally { lostFoundLoading.value = false }
})

// 将后端字段映射为 GoodsCard 需要的字段
function normalize(item) {
  return {
    ...item,
    id: item.id || item.itemId,
    image: item.image || item.imageUrl,
    title: item.title || item.name,
    price: item.price,
    condition: item.condition || item.quality,
    views: item.views || item.viewCount,
    location: item.location || item.address,
  }
}

function onImgError(e) {
  e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 200"><rect fill="%23f5f6f8" width="300" height="200"/><text x="150" y="100" text-anchor="middle" fill="%23adb5bd" font-size="14">暂无图片</text></svg>'
}
</script>

<style scoped>
.home {
  padding-bottom: var(--space-xxl);
}

.section {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 var(--space-lg);
  margin-bottom: var(--space-xl);
}
.section.bg-light {
  background: var(--color-surface);
  padding-top: var(--space-lg);
  padding-bottom: var(--space-lg);
  max-width: none;
}
.section.bg-light .section-header,
.section.bg-light .goods-grid,
.section.bg-light .empty-mini {
  max-width: var(--content-max-width);
  margin-left: auto;
  margin-right: auto;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-md);
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.0625rem;
  font-weight: 700;
  color: var(--color-ink);
  margin: 0;
}
.section-icon { color: var(--color-primary); }
.section-icon.fire { color: var(--color-danger); }
.section-icon.success { color: var(--color-success); }

.section-more {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.8125rem;
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  padding: 6px 12px;
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm);
  transition: background var(--duration-fast), color var(--duration-fast);
}
.section-more:hover { background: var(--color-primary); color: #fff; }

/* ── Goods Grid ── */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

/* ── Skeleton ── */
.skeleton-card {
  border: 1px solid var(--color-divider);
  border-radius: var(--radius-md);
  overflow: hidden;
}
.skeleton-img {
  aspect-ratio: 4/3;
  background: var(--color-surface);
  animation: shimmer 1.5s infinite;
}
.skeleton-lines { padding: 12px; }
.skeleton-line {
  height: 12px; background: var(--color-surface);
  border-radius: var(--radius-sm); margin-bottom: 8px;
  animation: shimmer 1.5s infinite;
}
.skeleton-line.w-80 { width: 80%; }
.skeleton-line.w-40 { width: 40%; margin-bottom: 0; }

@keyframes shimmer {
  0% { opacity: 1; } 50% { opacity: 0.5; } 100% { opacity: 1; }
}

/* ── Empty ── */
.empty-mini {
  text-align: center; padding: var(--space-xxl) var(--space-lg);
  color: var(--color-muted); font-size: var(--font-body-size);
}

/* ── LostFound Mini ── */
.lostfound-mini-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--space-md);
}
.lostfound-mini-card {
  display: flex;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  text-decoration: none;
  color: var(--color-ink);
  transition: box-shadow var(--duration-normal);
}
.lostfound-mini-card:hover { box-shadow: var(--shadow-ambient-low); }
.lostfound-mini-card img {
  width: 120px; min-height: 100%; object-fit: cover; flex-shrink: 0;
  background: var(--color-surface);
}
.lf-body {
  padding: 12px; display: flex; flex-direction: column; flex: 1;
}
.lf-header {
  display: flex; justify-content: space-between; align-items: flex-start;
  gap: 8px; margin-bottom: auto;
}
.lf-header h6 {
  font-size: 0.875rem; font-weight: 600; margin: 0;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.lf-badge {
  font-size: 0.6875rem; padding: 2px 8px; border-radius: var(--radius-sm);
  white-space: nowrap; flex-shrink: 0;
}
.lf-badge.open { background: var(--color-success-light); color: var(--color-success); }
.lf-badge.closed { background: var(--color-surface); color: var(--color-muted); }
.lf-location {
  display: flex; align-items: center; gap: 4px;
  color: var(--color-muted); font-size: 0.75rem; margin-top: 6px;
}

/* ── Responsive ── */
@media (max-width: 640px) {
  .goods-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .lostfound-mini-grid { grid-template-columns: 1fr; }
  .lostfound-mini-card img { width: 100px; }
  .section { margin-bottom: var(--space-md); }
}
</style>
