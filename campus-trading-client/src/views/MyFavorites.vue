<template>
  <div class="fav-page">
    <div class="page-header">
      <h1>我的收藏</h1>
      <span class="fav-count" v-if="allFavorites.length">{{ allFavorites.length }} 件商品</span>
    </div>

    <!-- ── Empty ── -->
    <div v-if="!loading && allFavorites.length === 0" class="empty-state">
      <div class="empty-illustration">
        <svg viewBox="0 0 120 120" fill="none">
          <circle cx="60" cy="60" r="54" fill="var(--color-primary-light)"/>
          <polygon points="60 25 67.5 47.5 91.5 47.5 72 62 79.5 85 60 70 40.5 85 48 62 28.5 47.5 52.5 47.5" stroke="var(--color-primary)" stroke-width="2.5" fill="none"/>
        </svg>
      </div>
      <h2>还没有收藏商品</h2>
      <p>浏览商品时点击收藏，方便以后查看</p>
      <button class="btn-primary" @click="$router.push('/')">去逛逛</button>
    </div>

    <!-- ── Grid ── -->
    <div v-else class="fav-grid">
      <article v-for="f in favorites" :key="f.favoriteId" class="fav-card" @click="$router.push(`/item/${f.itemId}`)">
        <div class="card-img-wrap">
          <img v-if="f.itemImageUrl" :src="f.itemImageUrl" :alt="f.itemTitle" />
          <div v-else class="card-img-fb">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="40" height="40"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
          </div>
          <button class="unfav-btn" @click.stop="handleUnfav(f.favoriteId)" title="取消收藏">
            <svg viewBox="0 0 24 24" :fill="'currentColor'" stroke="none" width="16" height="16">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
            </svg>
          </button>
        </div>
        <div class="card-body">
          <h3 class="card-title">{{ f.itemTitle }}</h3>
          <div class="card-price">¥{{ f.itemPrice }}</div>
          <div class="card-time">收藏于 {{ f.createTime }}</div>
        </div>
      </article>
    </div>

    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="(p) => { page = p }" background />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavorites, deleteFavorite } from '../api/favorite'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')
const loading = ref(true)

const allFavorites = ref([])
const page = ref(1)
const pageSize = 12
const total = computed(() => allFavorites.value.length)
const favorites = computed(() => {
  const start = (page.value - 1) * pageSize
  return allFavorites.value.slice(start, start + pageSize)
})

async function fetchFavorites() {
  if (!loggedIn.value) { requireLogin(); return }
  loading.value = true
  try {
    const res = await getFavorites()
    allFavorites.value = res.data
  } catch (e) { allFavorites.value = [] }
  finally { loading.value = false }
}

async function handleUnfav(id) {
  try { await deleteFavorite(id); ElMessage.success('已取消收藏'); fetchFavorites() } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchFavorites() })
onMounted(fetchFavorites)
</script>

<style scoped>
.fav-page { max-width: 1100px; margin: 0 auto; }

.page-header { display: flex; align-items: baseline; gap: var(--space-md); margin-bottom: var(--space-lg); }
.page-header h1 { font-size: var(--font-display-size); font-weight: var(--font-display-weight); color: var(--color-ink); }
.fav-count { color: var(--color-muted); font-size: var(--font-body-size); }

/* Empty */
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); }
.empty-illustration { margin-bottom: var(--space-lg); }
.empty-illustration svg { width: 120px; height: 120px; }
.empty-state h2 { font-size: var(--font-headline-size); font-weight: var(--font-headline-weight); margin-bottom: var(--space-sm); }
.empty-state p { color: var(--color-muted); margin-bottom: var(--space-lg); }
.btn-primary {
  padding: 10px 28px; background: var(--color-primary); color: var(--color-primary-text);
  border: none; border-radius: var(--radius-md); font-size: var(--font-body-size); font-weight: 500;
  font-family: var(--font-family); cursor: pointer; transition: background var(--duration-fast);
}
.btn-primary:hover { background: var(--color-primary-hover); }

/* Grid */
.fav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: var(--space-lg);
}

.fav-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); overflow: hidden; cursor: pointer;
  transition: box-shadow var(--duration-normal) var(--ease-standard),
              transform var(--duration-normal) var(--ease-standard);
}
.fav-card:hover { box-shadow: var(--shadow-ambient-low); transform: translateY(-2px); }

.card-img-wrap {
  position: relative; aspect-ratio: 4 / 3; overflow: hidden; background: var(--color-surface);
}
.card-img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.card-img-fb {
  width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;
  color: var(--color-muted);
}
.unfav-btn {
  position: absolute; top: var(--space-sm); right: var(--space-sm);
  padding: 6px; background: rgba(255,255,255,0.85); backdrop-filter: blur(4px);
  border: none; border-radius: var(--radius-sm); color: var(--color-primary);
  cursor: pointer; display: flex; opacity: 0;
  transition: opacity var(--duration-fast);
}
.fav-card:hover .unfav-btn { opacity: 1; }

.card-body { padding: var(--space-md); }
.card-title {
  font-size: var(--font-body-size); font-weight: 600; color: var(--color-ink);
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: var(--space-sm);
}
.card-price { font-size: 1.125rem; font-weight: 700; color: var(--color-primary); margin-bottom: 4px; }
.card-time { font-size: var(--font-label-size); color: var(--color-muted); }

.pagination-wrap { margin-top: var(--space-lg); display: flex; justify-content: center; }
</style>
