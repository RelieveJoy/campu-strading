<template>
  <div class="favorites-container">
    <div class="header">
      <h2>我的收藏</h2>
    </div>
    <div class="fav-grid" v-if="favorites.length > 0">
      <div v-for="f in favorites" :key="f.favoriteId" class="fav-card" @click="$router.push(`/item/${f.itemId}`)">
        <el-image :src="f.itemImageUrl" fit="cover" style="width:100%;height:180px">
          <template #error><div class="img-slot">无图</div></template>
        </el-image>
        <div class="fav-info">
          <h4>{{ f.itemTitle }}</h4>
          <p class="price">￥{{ f.itemPrice }}</p>
          <p class="time">收藏于 {{ f.createTime }}</p>
        </div>
        <el-button type="danger" size="small" class="unfav-btn" @click.stop="handleUnfav(f.favoriteId)">取消收藏</el-button>
      </div>
    </div>
    <el-empty v-else description="还没有收藏商品" />
    <div class="pagination" v-if="total > pageSize">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="(p) => { page = p }" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getFavorites, deleteFavorite } from '../api/favorite'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')

const allFavorites = ref([])
const page = ref(1)
const pageSize = 8
const total = computed(() => allFavorites.value.length)
const favorites = computed(() => {
  const start = (page.value - 1) * pageSize
  return allFavorites.value.slice(start, start + pageSize)
})

const fetchFavorites = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  try {
    const res = await getFavorites()
    allFavorites.value = res.data
  } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchFavorites() })

const handleUnfav = async (id) => {
  try { await deleteFavorite(id); ElMessage.success('已取消'); fetchFavorites() } catch (e) { /* ignore */ }
}

onMounted(fetchFavorites)
</script>

<style scoped>
.favorites-container { max-width:1000px; margin:0 auto; padding:20px; }
.header { display:flex; align-items:center; gap:16px; margin-bottom:24px; }
.header h2 { margin:0; }
.fav-grid { display:grid; grid-template-columns:repeat(4, 1fr); gap:16px; }
.fav-card { border:1px solid #eee; border-radius:8px; overflow:hidden; cursor:pointer; position:relative; transition:box-shadow 0.2s; }
.fav-card:hover { box-shadow:0 4px 12px rgba(0,0,0,0.1); }
.fav-info { padding:12px; }
.fav-info h4 { font-size:14px; margin-bottom:4px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.price { color:#f56c6c; font-size:16px; font-weight:bold; }
.time { color:#999; font-size:12px; margin-top:4px; }
.unfav-btn { position:absolute; top:8px; right:8px; opacity:0; transition:opacity 0.2s; }
.fav-card:hover .unfav-btn { opacity:1; }
.img-slot { width:100%;height:180px;display:flex;align-items:center;justify-content:center;background:#f5f5f5;color:#999; }
.pagination { margin-top:16px; display:flex; justify-content:center; }
</style>
