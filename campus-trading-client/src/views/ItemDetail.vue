<template>
  <div class="detail-container">
    <div class="detail-header">
      <el-button @click="$router.back()">← 返回</el-button>
    </div>
    <div class="detail-content" v-if="detail">
      <div class="detail-left">
        <el-image :src="detail.imageUrl" fit="contain" style="width:100%;max-height:400px">
          <template #error><div class="img-placeholder">暂无图片</div></template>
        </el-image>
      </div>
      <div class="detail-right">
        <h1>{{ detail.title }}</h1>
        <div class="detail-price">
          <span class="price">￥{{ detail.price }}</span>
          <span class="original" v-if="detail.originalPrice">原价 ￥{{ detail.originalPrice }}</span>
        </div>
        <div class="detail-meta">
          <p>分类：{{ detail.categoryName }}</p>
          <p>卖家：{{ detail.sellerName }}（{{ detail.sellerPhone }}）</p>
          <p>发布时间：{{ detail.createTime }}</p>
          <p>浏览量：{{ detail.viewCount }}</p>
        </div>
        <div class="detail-desc">
          <h3>商品描述</h3>
          <p>{{ detail.description || '暂无描述' }}</p>
        </div>
        <div class="detail-actions">
          <el-button type="warning" @click="toggleFavorite" :disabled="favLoading">
            {{ isFaved ? '取消收藏' : '收藏' }}
          </el-button>
          <el-button type="danger" @click="handleBuy" :disabled="buyLoading" v-if="isOwner !== true">
            {{ detail.status === 1 ? '立即购买' : '已售出' }}
          </el-button>
          <el-tag v-if="detail.status !== 1" type="info">{{ detail.status === 2 ? '已售出' : '已下架' }}</el-tag>
        </div>
      </div>
    </div>
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
const isFaved = ref(false)
const favLoading = ref(false)
const buyLoading = ref(false)
const isOwner = ref(false)

const fetchDetail = async () => {
  const res = await getItemDetail(route.params.id)
  detail.value = res.data
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (user.id === detail.value.sellerId) isOwner.value = true
}

const checkFav = async () => {
  try {
    const res = await checkFavorite(route.params.id)
    isFaved.value = res.data
  } catch (e) { /* ignore */ }
}

const toggleFavorite = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  favLoading.value = true
  try {
    if (isFaved.value) {
      const res = await getFavorites()
      const fav = res.data.find(f => f.itemId === detail.value.itemId)
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

const handleBuy = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  buyLoading.value = true
  try {
    await createOrder({ itemId: detail.value.itemId })
    ElMessage.success('下单成功')
    fetchDetail()
  } catch (e) { /* handled */ }
  finally { buyLoading.value = false }
}

onMounted(() => {
  fetchDetail()
  checkFav()
})
</script>

<style scoped>
.detail-container { max-width:1000px; margin:0 auto; padding:20px; }
.detail-header { margin-bottom:16px; }
.detail-content { display:flex; gap:32px; }
.detail-left { flex:1; }
.img-placeholder { width:100%; height:350px; display:flex; align-items:center; justify-content:center; background:#f5f5f5; color:#999; }
.detail-right { flex:1; }
.detail-right h1 { font-size:22px; margin-bottom:16px; }
.detail-price { margin-bottom:16px; }
.price { color:#f56c6c; font-size:28px; font-weight:bold; }
.original { color:#999; margin-left:12px; text-decoration:line-through; }
.detail-meta p { margin:8px 0; color:#666; font-size:14px; }
.detail-desc { margin:20px 0; padding:16px; background:#f9f9f9; border-radius:8px; }
.detail-desc h3 { margin-bottom:8px; }
.detail-actions { display:flex; gap:12px; margin-top:24px; align-items:center; }
</style>
