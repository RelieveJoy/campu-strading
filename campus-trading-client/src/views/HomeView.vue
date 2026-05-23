<template>
  <div class="home-container">
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品..." clearable @keyup.enter="search" style="width:300px" />
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width:160px;margin-left:12px">
        <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
      </el-select>
      <el-input-number v-model="minPrice" placeholder="最低价" :min="0" style="width:130px;margin-left:12px" />
      <el-input-number v-model="maxPrice" placeholder="最高价" :min="0" style="width:130px;margin-left:8px" />
      <el-button @click="search" type="primary" style="margin-left:12px">搜索</el-button>
    </div>

    <div class="item-grid">
      <div v-for="item in items" :key="item.itemId" class="item-card" @click="$router.push(`/item/${item.itemId}`)">
        <el-image :src="item.imageUrl" fit="cover" class="item-image">
          <template #error><div class="image-placeholder">暂无图片</div></template>
        </el-image>
        <div class="item-info">
          <h3>{{ item.title }}</h3>
          <div class="item-price"><span class="price">￥{{ item.price }}</span>
            <span class="original" v-if="item.originalPrice">￥{{ item.originalPrice }}</span>
          </div>
          <div class="item-meta">
            <span>{{ item.sellerName }}</span>
            <span>{{ item.categoryName }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getItems } from '../api/item'

const router = useRouter()
const items = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 12
const keyword = ref('')
const categoryId = ref(null)
const minPrice = ref(null)
const maxPrice = ref(null)

const categories = [
  { label: '教材', value: 1 }, { label: '电子产品', value: 2 },
  { label: '生活用品', value: 3 }, { label: '衣物', value: 4 }
]

const fetchItems = async () => {
  const params = { page: page.value, pageSize }
  if (keyword.value) params.keyword = keyword.value
  if (categoryId.value) params.categoryId = categoryId.value
  if (minPrice.value != null) params.minPrice = minPrice.value
  if (maxPrice.value != null) params.maxPrice = maxPrice.value
  const res = await getItems(params)
  items.value = res.data.records
  total.value = res.data.total
}

const search = () => { page.value = 1; fetchItems() }
const handlePageChange = (p) => { page.value = p; fetchItems() }

onMounted(fetchItems)
</script>

<style scoped>
.home-container { padding:20px; }
.search-bar { display:flex; align-items:center; margin-bottom:24px; flex-wrap:wrap; gap:8px; }
.item-grid { display:grid; grid-template-columns:repeat(4, 1fr); gap:16px; }
.item-card { border:1px solid #eee; border-radius:8px; overflow:hidden; cursor:pointer; transition:box-shadow 0.2s; }
.item-card:hover { box-shadow:0 4px 16px rgba(0,0,0,0.1); }
.item-image { width:100%; height:200px; }
.image-placeholder { width:100%; height:200px; display:flex; align-items:center; justify-content:center; background:#f5f5f5; color:#999; }
.item-info { padding:12px; }
.item-info h3 { font-size:15px; margin-bottom:8px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.item-price { margin-bottom:6px; }
.price { color:#f56c6c; font-size:18px; font-weight:bold; }
.original { color:#999; font-size:13px; text-decoration:line-through; margin-left:8px; }
.item-meta { font-size:12px; color:#999; display:flex; justify-content:space-between; }
.pagination { margin-top:24px; display:flex; justify-content:center; }
</style>
