<template>
  <div class="my-items-container">
    <div class="header">
      <h2>我的发布</h2>
    </div>

    <div class="item-list">
      <div v-for="item in items" :key="item.itemId" class="item-row">
        <el-image :src="item.imageUrl" fit="cover" style="width:100px;height:100px;border-radius:4px">
          <template #error><div class="img-slot">无图</div></template>
        </el-image>
        <div class="item-body">
          <h4>{{ item.title }}</h4>
          <p>￥{{ item.price }} <span class="original" v-if="item.originalPrice">原价 ￥{{ item.originalPrice }}</span></p>
          <p class="meta">{{ item.categoryName }} | 浏览量 {{ item.viewCount }} | {{ item.createTime }}</p>
          <el-tag :type="statusType(item.status)" size="small">{{ statusText(item.status) }}</el-tag>
        </div>
        <div class="item-actions">
          <el-button size="small" @click="$router.push(`/edit/${item.itemId}`)" v-if="item.status === 1">编辑</el-button>
          <el-button size="small" type="warning" @click="handleRelist(item.itemId)" v-if="item.status === 0">上架</el-button>
          <el-button size="small" type="danger" @click="handleDelete(item.itemId)" v-if="item.status === 1">下架</el-button>
        </div>
      </div>
      <el-empty v-if="items.length === 0" description="还没有发布商品">
        <el-button type="primary" @click="$router.push('/publish')">去发布</el-button>
      </el-empty>
    </div>
    <div class="pagination" v-if="total > pageSize">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="(p) => { page = p }" />
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

const allItems = ref([])
const page = ref(1)
const pageSize = 5
const total = computed(() => allItems.value.length)
const items = computed(() => {
  const start = (page.value - 1) * pageSize
  return allItems.value.slice(start, start + pageSize)
})

const statusText = (s) => ({ 1: '在售', 0: '已下架', 2: '已售出' }[s] || '')
const statusType = (s) => ({ 1: 'success', 0: 'info', 2: 'warning' }[s] || '')

const fetchItems = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  const u = JSON.parse(localStorage.getItem('user') || '{}')
  try {
    const res = await getUserItems(u.id)
    allItems.value = res.data
  } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchItems() })

const handleDelete = async (id) => {
  try { await deleteItem(id); ElMessage.success('已下架'); fetchItems() } catch (e) { /* ignore */ }
}

const handleRelist = async (id) => {
  try { await relistItem(id); ElMessage.success('已上架'); fetchItems() } catch (e) { /* ignore */ }
}

onMounted(fetchItems)
</script>

<style scoped>
.my-items-container { max-width:900px; margin:0 auto; padding:20px; }
.header { display:flex; align-items:center; gap:16px; margin-bottom:24px; }
.header h2 { margin:0; }
.item-row { display:flex; gap:16px; align-items:center; padding:16px; border:1px solid #eee; border-radius:8px; margin-bottom:12px; }
.item-body { flex:1; }
.item-body h4 { margin-bottom:4px; }
.item-body p { color:#666; font-size:14px; margin:2px 0; }
.original { color:#999; text-decoration:line-through; margin-left:8px; }
.meta { font-size:12px; color:#999; }
.item-actions { display:flex; gap:8px; }
.img-slot { width:100px;height:100px;display:flex;align-items:center;justify-content:center;background:#f5f5f5;color:#999;font-size:12px; }
.pagination { margin-top:16px; display:flex; justify-content:center; }
</style>
