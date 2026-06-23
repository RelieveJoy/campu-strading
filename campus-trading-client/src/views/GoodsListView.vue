<template>
  <div class="goods-list-page">
    <div class="container">
      <div class="row-layout">
        <!-- 左侧筛选 -->
        <aside class="filter-col">
          <GoodsFilter
            :activeCategory="activeCategory"
            :activeCondition="activeCondition"
            @update:category="onCategory"
            @update:condition="onCondition"
            @filter-price="onPrice"
          />
        </aside>

        <!-- 右侧列表 -->
        <div class="content-col">
          <!-- 状态栏 -->
          <div class="list-header">
            <span class="result-count">
              共 <strong>{{ total }}</strong> 件商品
            </span>
            <div class="header-right">
              <label class="sold-toggle">
                <input type="checkbox" v-model="showSold" @change="onToggleSold" />
                <span>显示已售出</span>
              </label>
              <span class="sort-label">排序：</span>
              <div class="sort-btns">
                <button
                  v-for="s in sorts"
                  :key="s.value"
                  class="sort-btn"
                  :class="{ active: activeSort === s.value }"
                  @click="onSort(s.value)"
                >{{ s.label }}</button>
              </div>
            </div>
          </div>

          <!-- Loading -->
          <div v-if="loading" class="goods-grid">
            <div v-for="n in 8" :key="n" class="skeleton-card">
              <div class="skeleton-img"></div>
              <div class="skeleton-lines">
                <div class="skeleton-line w-80"></div>
                <div class="skeleton-line w-40"></div>
              </div>
            </div>
          </div>

          <!-- Empty -->
          <div v-else-if="items.length === 0" class="empty-state">
            <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
              <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
              <rect x="35" y="40" width="50" height="45" rx="4" stroke="var(--color-muted)" stroke-width="2.5" fill="none"/>
              <circle cx="52" cy="55" r="6" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
              <path d="m42 92 12-16 8 10 6-8 12 14" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
            </svg>
            <p>没有找到相关商品</p>
          </div>

          <!-- Grid -->
          <div v-else class="goods-grid">
            <GoodsCard v-for="item in items" :key="item.id || item.itemId" :goods="item" />
          </div>

          <!-- 分页 -->
          <div class="pagination-wrap" v-if="total > pageSize && !loading">
            <el-pagination
              :current-page="page"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next"
              @current-change="onPage"
              background
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getItems } from '../api/item'
import GoodsCard from '../components/GoodsCard.vue'
import GoodsFilter from '../components/GoodsFilter.vue'

const route = useRoute()
const router = useRouter()

// ── 筛选状态 ──
const activeCategory = ref(null)
const activeCondition = ref(null)
const activeSort = ref('newest')
const minPrice = ref(null)
const maxPrice = ref(null)
const page = ref(1)
const pageSize = 8
const showSold = ref(false)

const sorts = [
  { value: 'newest', label: '最新' },
  { value: 'hottest', label: '最热' },
  { value: 'price_asc', label: '价格↑' },
  { value: 'price_desc', label: '价格↓' },
]

// ── 数据 ──
const items = ref([])
const total = ref(0)
const loading = ref(true)

async function fetchItems() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize, sort: activeSort.value }
    if (route.query.q) params.keyword = route.query.q
    if (activeCategory.value) params.categoryId = activeCategory.value
    if (activeCondition.value) params.itemCondition = activeCondition.value
    if (minPrice.value != null) params.minPrice = minPrice.value
    if (maxPrice.value != null) params.maxPrice = maxPrice.value
    if (!showSold.value) params.status = 1  // 默认只显示在售
    const res = await getItems(params)
    items.value = (res?.data?.records || []).map(normalize)
    total.value = res?.data?.total || 0
  } catch { items.value = []; total.value = 0 }
  finally { loading.value = false }
}

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

// ── 事件 ──
function onCategory(cat) { activeCategory.value = cat; page.value = 1; router.replace({ query: { ...route.query, category: cat } }); fetchItems() }
function onCondition(cond) { activeCondition.value = cond; page.value = 1; fetchItems() }
function onPrice({ min, max }) { minPrice.value = min || null; maxPrice.value = max || null; page.value = 1; fetchItems() }
function onSort(sort) { activeSort.value = sort; page.value = 1; fetchItems() }
function onToggleSold() { page.value = 1; fetchItems() }
function onPage(p) { page.value = p; fetchItems(); window.scrollTo({ top: 0, behavior: 'smooth' }) }

onMounted(() => {
  if (route.query.category) activeCategory.value = route.query.category
  fetchItems()
})

// 搜索关键字变化时自动重新查询
watch(() => route.query.q, () => {
  page.value = 1
  fetchItems()
})
</script>

<style scoped>
.goods-list-page {
  padding: var(--space-lg) 0;
}
.container {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 16px;
}
.row-layout {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: var(--space-lg);
  align-items: start;
}

/* ── Header ── */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: var(--space-md);
}
.result-count { font-size: 0.8125rem; color: var(--color-muted); }
.result-count strong { color: var(--color-primary); }
.sort-group { display: flex; align-items: center; gap: 8px; }
.sort-label { font-size: 0.8125rem; color: var(--color-muted); }
.sort-btns { display: flex; gap: 2px; }
.sort-btn {
  padding: 5px 12px; border: 1px solid var(--color-border);
  background: var(--color-bg); color: var(--color-muted);
  font-size: 0.75rem; cursor: pointer; font-family: var(--font-family);
  transition: all var(--duration-fast);
}
.sort-btn:first-child { border-radius: var(--radius-sm) 0 0 var(--radius-sm); }
.sort-btn:last-child { border-radius: 0 var(--radius-sm) var(--radius-sm) 0; }
.sort-btn:not(:first-child) { border-left: none; }
.sort-btn:hover { color: var(--color-primary); border-color: var(--color-primary); }
.sort-btn.active { background: var(--color-primary); color: #fff; border-color: var(--color-primary); }

/* ── Grid ── */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: var(--space-md);
}

/* ── Skeleton ── */
.skeleton-card { border: 1px solid var(--color-divider); border-radius: var(--radius-md); overflow: hidden; }
.skeleton-img { aspect-ratio: 4/3; background: var(--color-surface); animation: shimmer 1.5s infinite; }
.skeleton-lines { padding: 12px; }
.skeleton-line { height: 12px; background: var(--color-surface); border-radius: var(--radius-sm); margin-bottom: 8px; animation: shimmer 1.5s infinite; }
.skeleton-line.w-80 { width: 80%; }
.skeleton-line.w-40 { width: 40%; margin-bottom: 0; }
@keyframes shimmer { 0% { opacity: 1; } 50% { opacity: 0.5; } 100% { opacity: 1; } }

/* ── Empty ── */
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }
.empty-icon { width: 120px; height: 120px; margin-bottom: var(--space-md); }

/* ── Pagination ── */
.pagination-wrap { margin-top: var(--space-xl); display: flex; justify-content: center; }

/* ── Responsive ── */
@media (max-width: 768px) {
  .row-layout { grid-template-columns: 1fr; }
  .filter-col { order: 2; }
  .content-col { order: 1; }
  .goods-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
