<template>
  <div class="list-page">
    <div class="page-header">
      <h1>我的发布</h1>
      <span class="item-count" v-if="allItems.length">{{ allItems.length }} 件商品</span>
    </div>

    <!-- ── Empty ── -->
    <div v-if="!loading && allItems.length === 0" class="empty-state">
      <div class="empty-illustration">
        <svg viewBox="0 0 120 120" fill="none">
          <circle cx="60" cy="60" r="54" fill="var(--color-primary-light)"/>
          <path d="M35 45h50M35 60h40M35 75h30" stroke="var(--color-primary)" stroke-width="3" stroke-linecap="round"/>
        </svg>
      </div>
      <h2>还没有发布商品</h2>
      <p>发布你的闲置物品，让它们找到新主人</p>
      <button class="btn-primary" @click="$router.push('/publish')">发布商品</button>
    </div>

    <!-- ── List ── -->
    <div v-else class="item-list">
      <div v-for="item in items" :key="item.itemId" class="list-row">
        <div class="row-image" @click="$router.push(`/item/${item.itemId}`)">
          <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.title" />
          <div v-else class="row-image-placeholder">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
          </div>
        </div>

        <div class="row-body" @click="$router.push(`/item/${item.itemId}`)">
          <div class="row-top">
            <h3 class="row-title">{{ item.title }}</h3>
            <span class="row-status" :class="'status-' + item.status">
              {{ statusMap[item.status] }}
            </span>
          </div>
          <div class="row-price">¥{{ item.price }} <span v-if="item.originalPrice" class="row-original">原价 ¥{{ item.originalPrice }}</span></div>
          <div class="row-meta">{{ item.categoryName }} · {{ item.viewCount || 0 }} 次浏览 · {{ item.createTime }}</div>
        </div>

        <div class="row-actions">
          <button v-if="item.status === 1" class="action-btn" @click="$router.push(`/edit/${item.itemId}`)">编辑</button>
          <button v-if="item.status === 0" class="action-btn accent" @click="handleRelist(item.itemId)">上架</button>
          <button v-if="item.status === 1" class="action-btn danger" @click="handleDelete(item.itemId)">下架</button>
        </div>
      </div>
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
import { getUserItems } from '../api/user'
import { deleteItem, relistItem } from '../api/item'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')
const loading = ref(true)

const allItems = ref([])
const page = ref(1)
const pageSize = 8
const total = computed(() => allItems.value.length)
const items = computed(() => {
  const start = (page.value - 1) * pageSize
  return allItems.value.slice(start, start + pageSize)
})

const statusMap = { 1: '在售', 0: '已下架', 2: '已售出' }

async function fetchItems() {
  if (!loggedIn.value) { requireLogin(); return }
  loading.value = true
  try {
    const u = JSON.parse(localStorage.getItem('user') || '{}')
    const res = await getUserItems(u.id)
    allItems.value = res.data
  } catch (e) { allItems.value = [] }
  finally { loading.value = false }
}

async function handleDelete(id) {
  try { await deleteItem(id); ElMessage.success('已下架'); fetchItems() } catch (e) { /* ignore */ }
}

async function handleRelist(id) {
  try { await relistItem(id); ElMessage.success('已上架'); fetchItems() } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchItems() })
onMounted(fetchItems)
</script>

<style scoped>
.list-page { max-width: 900px; margin: 0 auto; }

.page-header {
  display: flex; align-items: baseline; gap: var(--space-md);
  margin-bottom: var(--space-lg);
}
.page-header h1 {
  font-size: var(--font-display-size); font-weight: var(--font-display-weight); color: var(--color-ink);
}
.item-count { color: var(--color-muted); font-size: var(--font-body-size); }

/* ── Empty ── */
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

/* ── List ── */
.list-row {
  display: flex; gap: var(--space-md); align-items: center;
  padding: var(--space-md); border: 1px solid var(--color-divider);
  border-radius: var(--radius-md); margin-bottom: var(--space-md);
  background: var(--color-bg); transition: box-shadow var(--duration-fast);
}
.list-row:hover { box-shadow: var(--shadow-ambient-low); }

.row-image {
  width: 100px; height: 75px; border-radius: var(--radius-sm); overflow: hidden;
  cursor: pointer; flex-shrink: 0; background: var(--color-surface);
}
.row-image img { width: 100%; height: 100%; object-fit: cover; }
.row-image-placeholder {
  width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;
  color: var(--color-muted);
}
.row-image-placeholder svg { width: 32px; height: 32px; }

.row-body { flex: 1; cursor: pointer; min-width: 0; }
.row-top { display: flex; align-items: center; gap: var(--space-sm); margin-bottom: 4px; }
.row-title { font-size: var(--font-body-size); font-weight: 600; color: var(--color-ink); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.row-status { font-size: 0.6875rem; font-weight: 500; padding: 2px 10px; border-radius: var(--radius-full); flex-shrink: 0; }
.status-1 { background: var(--color-success-light); color: var(--color-success); }
.status-0 { background: var(--color-surface); color: var(--color-muted); }
.status-2 { background: var(--color-warning-light); color: var(--color-warning); }

.row-price { font-size: var(--font-body-size); font-weight: 600; color: var(--color-primary); margin-bottom: 2px; }
.row-original { font-size: var(--font-label-size); color: var(--color-muted); text-decoration: line-through; font-weight: 400; margin-left: var(--space-sm); }
.row-meta { font-size: var(--font-label-size); color: var(--color-muted); }

.row-actions { display: flex; gap: var(--space-sm); flex-shrink: 0; }
.action-btn {
  padding: 6px 16px; border: 1px solid var(--color-border); border-radius: var(--radius-sm);
  background: var(--color-bg); color: var(--color-ink); font-size: var(--font-label-size);
  font-family: var(--font-family); cursor: pointer; transition: all var(--duration-fast);
}
.action-btn:hover { border-color: var(--color-primary); color: var(--color-primary); }
.action-btn.accent { border-color: var(--color-accent); color: var(--color-accent); }
.action-btn.accent:hover { background: var(--color-accent); color: white; }
.action-btn.danger { border-color: var(--color-danger); color: var(--color-danger); }
.action-btn.danger:hover { background: var(--color-danger); color: white; }

.pagination-wrap { margin-top: var(--space-lg); display: flex; justify-content: center; }

@media (max-width: 640px) {
  .row-image { width: 72px; height: 54px; }
  .row-actions { flex-direction: column; }
}
</style>
