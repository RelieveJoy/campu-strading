<template>
  <div class="orders-container">
    <div class="orders-header">
      <el-button @click="$router.push('/')">← 返回首页</el-button>
      <h2>我的订单</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="() => { page = 1; fetchOrders() }">
      <el-tab-pane label="我买的" name="buy" />
      <el-tab-pane label="我卖的" name="sell" />
    </el-tabs>

    <div class="order-list">
      <div v-for="o in orders" :key="o.orderId" class="order-card">
        <div class="order-top">
          <el-image :src="o.itemImageUrl" fit="cover" style="width:80px;height:80px;border-radius:4px">
            <template #error><div class="img-slot">无图</div></template>
          </el-image>
          <div class="order-info">
            <h4>{{ o.itemTitle }}</h4>
            <p>￥{{ o.amount }}</p>
            <p class="order-meta">
              {{ activeTab === 'buy' ? `卖家：${o.sellerName}` : `买家：${o.buyerName}` }}
              &nbsp;|&nbsp; {{ o.createTime }}
            </p>
          </div>
          <div class="order-status">
            <el-tag :type="statusType(o.status)">{{ statusText(o.status) }}</el-tag>
          </div>
        </div>
        <div class="order-actions" v-if="o.status === 1">
          <el-button v-if="activeTab === 'sell'" type="success" size="small" @click="handleConfirm(o.orderId)">确认完成</el-button>
          <el-button type="danger" size="small" @click="handleCancel(o.orderId)">取消订单</el-button>
        </div>
      </div>
      <el-empty v-if="orders.length === 0" description="暂无订单" />
    </div>
    <div class="pagination" v-if="total > pageSize">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="(p) => { page = p; fetchOrders() }" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, confirmOrder, cancelOrder } from '../api/order'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')

const activeTab = ref('buy')
const orders = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 5
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const fetchOrders = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  const params = { page: page.value, pageSize }
  if (activeTab.value === 'buy') params.buyerId = user.value.id
  else params.sellerId = user.value.id
  try {
    const res = await getOrders(params)
    orders.value = res.data.records
    total.value = res.data.total
  } catch (e) { /* ignore */ }
}

watch(loggedIn, (val) => { if (val) fetchOrders() })

const statusText = (s) => ({ 1: '待确认', 2: '已完成', 3: '已取消' }[s] || '未知')
const statusType = (s) => ({ 1: 'warning', 2: 'success', 3: 'info' }[s] || '')

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认完成此订单？', '提示', { type: 'info' })
    await confirmOrder(id)
    ElMessage.success('订单已完成')
    fetchOrders()
  } catch (e) { /* cancel or error */ }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消此订单？商品将恢复在售。', '提示', { type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) { /* cancel or error */ }
}

onMounted(fetchOrders)
</script>

<style scoped>
.orders-container { max-width:800px; margin:0 auto; padding:20px; }
.orders-header { display:flex; align-items:center; gap:16px; margin-bottom:16px; }
.orders-header h2 { margin:0; }
.order-card { border:1px solid #eee; border-radius:8px; padding:16px; margin-bottom:12px; }
.order-top { display:flex; gap:16px; align-items:center; }
.order-info { flex:1; }
.order-info h4 { margin-bottom:4px; }
.order-info p { margin:2px 0; color:#666; font-size:14px; }
.order-meta { font-size:12px; color:#999; }
.img-slot { width:80px;height:80px;display:flex;align-items:center;justify-content:center;background:#f5f5f5;color:#999;font-size:12px; }
.order-actions { margin-top:12px; display:flex; gap:8px; justify-content:flex-end; }
.pagination { margin-top:16px; display:flex; justify-content:center; }
</style>
