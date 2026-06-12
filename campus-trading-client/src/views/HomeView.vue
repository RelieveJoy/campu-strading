<template>
  <div class="home">
    <!-- ── Search Section ── -->
    <section class="search-section">
      <div class="search-inner">
        <div class="search-main">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input
            v-model="keyword"
            type="text"
            class="search-input"
            placeholder="搜索你想要的二手好物..."
            @keyup.enter="search"
          />
        </div>
        <div class="search-filters">
          <div class="filter-group">
            <span class="filter-label">价格</span>
            <input v-model.number="minPrice" type="number" class="price-input" placeholder="最低" min="0" />
            <span class="price-sep">—</span>
            <input v-model.number="maxPrice" type="number" class="price-input" placeholder="最高" min="0" />
          </div>
          <button class="btn-search" @click="search">搜索</button>
        </div>
      </div>
    </section>

    <!-- ── Category Chips ── -->
    <div class="category-strip">
      <button
        class="chip"
        :class="{ active: categoryId === null }"
        @click="selectCategory(null)"
      >全部</button>
      <button
        v-for="c in categories"
        :key="c.value"
        class="chip"
        :class="{ active: categoryId === c.value }"
        @click="selectCategory(c.value)"
      >{{ c.label }}</button>
    </div>

    <!-- ── Loading State ── -->
    <div v-if="loading" class="item-grid">
      <div v-for="n in 8" :key="n" class="skeleton-card">
        <div class="skeleton-image"></div>
        <div class="skeleton-body">
          <div class="skeleton-line w-80"></div>
          <div class="skeleton-line w-40"></div>
          <div class="skeleton-line w-60 short"></div>
        </div>
      </div>
    </div>

    <!-- ── Empty State ── -->
    <div v-else-if="!loading && items.length === 0" class="empty-state">
      <div class="empty-illustration">
        <svg viewBox="0 0 120 120" fill="none">
          <circle cx="60" cy="60" r="54" fill="var(--color-primary-light)" />
          <path d="M40 50h40M40 65h30M40 80h20" stroke="var(--color-primary)" stroke-width="3" stroke-linecap="round"/>
          <circle cx="85" cy="45" r="12" fill="var(--color-bg)" stroke="var(--color-primary)" stroke-width="2.5"/>
          <path d="M82 45l2-2 2 2 4-4" stroke="var(--color-primary)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <h2 class="empty-title">还没有商品</h2>
      <p class="empty-desc">成为第一个发布闲置物品的人吧</p>
      <button class="btn-primary" @click="$router.push('/publish')">发布商品</button>
    </div>

    <!-- ── Product Grid ── -->
    <div v-else class="item-grid">
      <article
        v-for="item in items"
        :key="item.itemId"
        class="item-card"
        @click="$router.push(`/item/${item.itemId}`)"
      >
        <div class="card-image-wrap">
          <el-image
            v-if="item.imageUrl"
            :src="item.imageUrl"
            fit="cover"
            class="card-image"
            lazy
          >
            <template #error>
              <div class="card-image-fallback">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="fallback-icon">
                  <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/>
                  <path d="m21 15-5-5L5 21"/>
                </svg>
              </div>
            </template>
          </el-image>
          <div v-else class="card-image-fallback">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="fallback-icon">
              <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/>
              <path d="m21 15-5-5L5 21"/>
            </svg>
          </div>
          <span class="card-category" v-if="item.categoryName">{{ item.categoryName }}</span>
        </div>

        <div class="card-body">
          <h3 class="card-title">{{ item.title }}</h3>
          <div class="card-price-row">
            <span class="card-price">¥{{ item.price }}</span>
            <span class="card-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
          </div>
          <div class="card-meta">
            <span class="card-seller">{{ item.sellerName }}</span>
            <span class="card-views" v-if="item.viewCount">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="views-icon">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              {{ item.viewCount }}
            </span>
          </div>
        </div>
      </article>
    </div>

    <!-- ── Pagination ── -->
    <div class="pagination-wrap" v-if="total > pageSize && !loading">
      <el-pagination
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getItems } from '../api/item'

const items = ref([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const pageSize = 12
const keyword = ref('')
const categoryId = ref(null)
const minPrice = ref(null)
const maxPrice = ref(null)

const categories = [
  { label: '教材', value: 1 },
  { label: '电子产品', value: 2 },
  { label: '生活用品', value: 3 },
  { label: '衣物鞋包', value: 4 },
  { label: '运动户外', value: 5 },
  { label: '其他', value: 6 },
]

const fetchItems = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (categoryId.value != null) params.categoryId = categoryId.value
    if (minPrice.value != null) params.minPrice = minPrice.value
    if (maxPrice.value != null) params.maxPrice = maxPrice.value
    const res = await getItems(params)
    items.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    items.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const search = () => { page.value = 1; fetchItems() }
const selectCategory = (id) => { categoryId.value = id; search() }
const handlePageChange = (p) => { page.value = p; fetchItems(); window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(fetchItems)
</script>

<style scoped>
/* ── Search Section ── */
.search-section {
  margin-bottom: var(--space-lg);
}

.search-inner {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  border: 1px solid var(--color-divider);
}

.search-main {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 0 var(--space-md);
  transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
  margin-bottom: var(--space-md);
}

.search-main:focus-within {
  border-color: var(--color-primary);
  box-shadow: var(--focus-ring);
}

.search-icon {
  width: 20px;
  height: 20px;
  color: var(--color-muted);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 12px 0;
  font-size: var(--font-body-size);
  color: var(--color-ink);
  font-family: var(--font-family);
}

.search-input::placeholder {
  color: oklch(0.65 0.005 28);
}

.search-filters {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.filter-label {
  font-size: var(--font-label-size);
  font-weight: var(--font-label-weight);
  color: var(--color-muted);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.price-input {
  width: 90px;
  padding: 6px 10px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-body-size);
  color: var(--color-ink);
  background: var(--color-bg);
  font-family: var(--font-family);
  outline: none;
  transition: border-color var(--duration-fast);
}

.price-input:focus {
  border-color: var(--color-primary);
}

.price-input::placeholder {
  color: var(--color-muted);
}

.price-sep {
  color: var(--color-muted);
  font-size: var(--font-label-size);
}

/* Chrome, Safari, Edge, Opera */
.price-input::-webkit-outer-spin-button,
.price-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
.price-input[type=number] {
  -moz-appearance: textfield;
}

/* ── Search Button ── */
.btn-search {
  padding: 8px 28px;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
  margin-left: auto;
}

.btn-search:hover {
  background: var(--color-primary-hover);
}

/* ── Category Chips ── */
.category-strip {
  display: flex;
  gap: var(--space-sm);
  margin-bottom: var(--space-lg);
  overflow-x: auto;
  padding-bottom: var(--space-xs);
  -webkit-overflow-scrolling: touch;
}

.category-strip::-webkit-scrollbar {
  height: 0;
}

.chip {
  padding: 6px 18px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  background: var(--color-bg);
  color: var(--color-ink);
  font-size: var(--font-body-size);
  font-family: var(--font-family);
  cursor: pointer;
  white-space: nowrap;
  flex-shrink: 0;
  transition: all var(--duration-fast) var(--ease-standard);
}

.chip:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.chip.active {
  background: var(--color-primary);
  color: var(--color-primary-text);
  border-color: var(--color-primary);
}

/* ── Product Grid ── */
.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: var(--space-lg);
}

/* ── Card ── */
.item-card {
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow var(--duration-normal) var(--ease-standard),
              transform var(--duration-normal) var(--ease-standard);
}

.item-card:hover {
  box-shadow: var(--shadow-ambient-low);
  transform: translateY(-2px);
}

.item-card:focus-visible {
  box-shadow: var(--focus-ring);
}

.card-image-wrap {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  background: var(--color-surface);
}

.card-image {
  width: 100%;
  height: 100%;
}

.card-image-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-surface);
  color: var(--color-muted);
}

.fallback-icon {
  width: 48px;
  height: 48px;
}

.card-category {
  position: absolute;
  top: var(--space-sm);
  left: var(--space-sm);
  padding: 3px 10px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(4px);
  border-radius: var(--radius-full);
  font-size: 0.6875rem;
  font-weight: 500;
  color: var(--color-accent);
}

.card-body {
  padding: var(--space-md);
}

.card-title {
  font-size: var(--font-body-size);
  font-weight: 600;
  color: var(--color-ink);
  margin-bottom: var(--space-sm);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.card-price-row {
  display: flex;
  align-items: baseline;
  gap: var(--space-sm);
  margin-bottom: var(--space-sm);
}

.card-price {
  font-size: 1.1875rem;
  font-weight: 700;
  color: var(--color-primary);
}

.card-original {
  font-size: var(--font-label-size);
  color: var(--color-muted);
  text-decoration: line-through;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: var(--font-label-size);
  color: var(--color-muted);
}

.card-seller {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-views {
  display: flex;
  align-items: center;
  gap: 3px;
  flex-shrink: 0;
}

.views-icon {
  width: 14px;
  height: 14px;
}

/* ── Skeleton ── */
.skeleton-card {
  border: 1px solid var(--color-divider);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.skeleton-image {
  aspect-ratio: 4 / 3;
  background: var(--color-surface);
  animation: shimmer 1.5s infinite;
}

.skeleton-body {
  padding: var(--space-md);
}

.skeleton-line {
  height: 14px;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  margin-bottom: var(--space-sm);
  animation: shimmer 1.5s infinite;
}

.skeleton-line.w-80 { width: 80%; }
.skeleton-line.w-60 { width: 60%; }
.skeleton-line.w-40 { width: 40%; }
.skeleton-line.short { margin-bottom: 0; }

@keyframes shimmer {
  0%   { opacity: 1; }
  50%  { opacity: 0.5; }
  100% { opacity: 1; }
}

/* ── Empty State ── */
.empty-state {
  text-align: center;
  padding: var(--space-xxl) var(--space-lg);
}

.empty-illustration {
  margin-bottom: var(--space-lg);
}

.empty-illustration svg {
  width: 120px;
  height: 120px;
}

.empty-title {
  font-size: var(--font-headline-size);
  font-weight: var(--font-headline-weight);
  color: var(--color-ink);
  margin-bottom: var(--space-sm);
}

.empty-desc {
  color: var(--color-muted);
  margin-bottom: var(--space-lg);
  font-size: var(--font-body-size);
}

/* ── Primary Button (used in empty state) ── */
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
  transition: background var(--duration-fast) var(--ease-standard);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

/* ── Pagination ── */
.pagination-wrap {
  margin-top: var(--space-xl);
  display: flex;
  justify-content: center;
}

/* ── Responsive ── */
@media (max-width: 640px) {
  .search-inner {
    padding: var(--space-md);
  }

  .search-filters {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-group {
    justify-content: space-between;
  }

  .price-input {
    flex: 1;
    width: auto;
  }

  .btn-search {
    margin-left: 0;
    text-align: center;
  }

  .item-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: var(--space-md);
  }

  .card-body {
    padding: var(--space-sm);
  }

  .card-price {
    font-size: 1rem;
  }
}
</style>
