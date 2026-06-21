<template>
  <div class="orders-page">
    <div class="page-header">
      <h1>我的订单</h1>
    </div>

    <div class="tab-switch">
      <button class="tab-btn" :class="{ active: activeTab === 'buy' }" @click="switchTab('buy')">我买的</button>
      <button class="tab-btn" :class="{ active: activeTab === 'sell' }" @click="switchTab('sell')">我卖的</button>
      <div class="tab-indicator" :class="{ right: activeTab === 'sell' }"></div>
    </div>

    <!-- ── Empty ── -->
    <div v-if="!loading && orders.length === 0" class="empty-state">
      <h2>暂无订单</h2>
      <p>{{ activeTab === 'buy' ? '去首页逛逛，发现心仪的好物' : '发布商品后，售出订单会显示在这里' }}</p>
      <button class="btn-primary" @click="$router.push(activeTab === 'buy' ? '/' : '/publish')">
        {{ activeTab === 'buy' ? '去逛逛' : '发布商品' }}
      </button>
    </div>

    <!-- ── Order List ── -->
    <div v-else class="order-list">
      <div v-for="o in orders" :key="o.orderId" class="order-card">
        <div class="order-main">
          <div class="order-image" @click="$router.push(`/item/${o.itemId}`)">
            <img v-if="o.itemImageUrl" :src="o.itemImageUrl" :alt="o.itemTitle" />
            <div v-else class="img-placeholder">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="32" height="32"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
            </div>
          </div>

          <div class="order-info" @click="$router.push(`/item/${o.itemId}`)">
            <h3 class="order-title">{{ o.itemTitle }}</h3>
            <div class="order-price">¥{{ o.amount }}</div>
            <div class="order-meta">
              <span>{{ activeTab === 'buy' ? '卖家' : '买家' }}：{{ activeTab === 'buy' ? o.sellerName : o.buyerName }}</span>
              <span>{{ o.createTime }}</span>
            </div>
          </div>

          <span class="order-status" :class="'ostatus-' + o.status">{{ statusMap[o.status] }}</span>
        </div>

        <div class="order-actions" v-if="o.status === 1">
          <button v-if="activeTab === 'sell'" class="action-btn confirm" @click="handleConfirm(o.orderId)">确认完成</button>
          <button class="action-btn cancel" @click="handleCancel(o.orderId)">取消订单</button>
        </div>
      </div>
    </div>

    <div class="pagination-wrap" v-if="total > pageSize">
      <el-pagination :current-page="page" :page-size="pageSize" :total="total"
        layout="prev, pager, next" @current-change="(p) => { page = p; fetchOrders() }" background />
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
const loading = ref(true)
const page = ref(1)
const pageSize = 8
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const statusMap = { 1: '待确认', 2: '已完成', 3: '已取消' }

function switchTab(tab) { activeTab.value = tab; page.value = 1; fetchOrders() }

async function fetchOrders() {
  if (!loggedIn.value) { requireLogin(); return }
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (activeTab.value === 'buy') params.buyerId = user.value.id
    else params.sellerId = user.value.id
    const res = await getOrders(params)
    orders.value = res.data.records
    total.value = res.data.total
  } catch (e) { orders.value = []; total.value = 0 }
  finally { loading.value = false }
}

async function handleConfirm(id) {
  try {
    await ElMessageBox.confirm('确认完成此订单？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'info' })
    await confirmOrder(id)
    ElMessage.success('订单已完成')
    fetchOrders()
  } catch (e) { /* cancel */ }
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm('确定取消此订单？商品将恢复在售。', '提示', { confirmButtonText: '确定取消', cancelButtonText: '返回', type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) { /* cancel */ }
}

watch(loggedIn, (val) => { if (val) fetchOrders() })
onMounted(fetchOrders)
</script>

<style scoped>
.orders-page { max-width: 800px; margin: 0 auto; padding: var(--space-lg); }

.page-header { margin-bottom: var(--space-lg); }
.page-header h1 { font-size: var(--font-display-size); font-weight: var(--font-display-weight); color: var(--color-ink); }

/* Tab switch */
.tab-switch {
  display: flex; position: relative;
  background: var(--color-surface); border-radius: var(--radius-md);
  padding: 3px; margin-bottom: var(--space-lg); max-width: 300px;
}
.tab-btn {
  flex: 1; padding: 8px 0; border: none; background: transparent;
  color: var(--color-muted); font-size: var(--font-body-size); font-weight: 500;
  font-family: var(--font-family); cursor: pointer; position: relative; z-index: 1;
  transition: color var(--duration-fast); border-radius: calc(var(--radius-md) - 2px);
}
.tab-btn.active { color: var(--color-ink); }
.tab-indicator {
  position: absolute; top: 3px; left: 3px; width: calc(50% - 3px); height: calc(100% - 6px);
  background: var(--color-bg); border-radius: calc(var(--radius-md) - 2px);
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
  transition: transform var(--duration-normal) var(--ease-standard);
}
.tab-indicator.right { transform: translateX(100%); }

/* Empty */
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); }
.empty-state h2 { font-size: var(--font-headline-size); margin-bottom: var(--space-sm); }
.empty-state p { color: var(--color-muted); margin-bottom: var(--space-lg); }
.btn-primary {
  padding: 10px 28px; background: var(--color-primary); color: var(--color-primary-text);
  border: none; border-radius: var(--radius-md); font-size: var(--font-body-size); font-weight: 500;
  font-family: var(--font-family); cursor: pointer; transition: background var(--duration-fast);
}
.btn-primary:hover { background: var(--color-primary-hover); }

/* Order card */
.order-card {
  border: 1px solid var(--color-divider); border-radius: var(--radius-md);
  padding: var(--space-md); margin-bottom: var(--space-md);
  background: var(--color-bg); transition: box-shadow var(--duration-fast);
}
.order-card:hover { box-shadow: var(--shadow-ambient-low); }

.order-main { display: flex; gap: var(--space-md); align-items: center; }

.order-image {
  width: 80px; height: 60px; border-radius: var(--radius-sm); overflow: hidden;
  cursor: pointer; flex-shrink: 0; background: var(--color-surface);
}
.order-image img { width: 100%; height: 100%; object-fit: cover; }
.img-placeholder {
  width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;
  color: var(--color-muted);
}

.order-info { flex: 1; cursor: pointer; min-width: 0; }
.order-title { font-size: var(--font-body-size); font-weight: 600; color: var(--color-ink); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 4px; }
.order-price { font-size: var(--font-body-size); font-weight: 600; color: var(--color-primary); margin-bottom: 2px; }
.order-meta { font-size: var(--font-label-size); color: var(--color-muted); display: flex; gap: var(--space-md); }

.order-status {
  font-size: var(--font-label-size); font-weight: 500;
  padding: 3px 12px; border-radius: var(--radius-full); flex-shrink: 0;
}
.ostatus-1 { background: var(--color-warning-light); color: var(--color-warning); }
.ostatus-2 { background: var(--color-success-light); color: var(--color-success); }
.ostatus-3 { background: var(--color-surface); color: var(--color-muted); }

.order-actions { margin-top: var(--space-md); display: flex; gap: var(--space-sm); justify-content: flex-end; }

.action-btn {
  padding: 6px 18px; border-radius: var(--radius-sm);
  font-size: var(--font-label-size); font-weight: 500;
  font-family: var(--font-family); cursor: pointer; transition: all var(--duration-fast);
  background: var(--color-bg);
}
.action-btn.confirm {
  border: 1px solid var(--color-success); color: var(--color-success);
}
.action-btn.confirm:hover { background: var(--color-success); color: white; }
.action-btn.cancel {
  border: 1px solid var(--color-danger); color: var(--color-danger);
}
.action-btn.cancel:hover { background: var(--color-danger); color: white; }

.pagination-wrap { margin-top: var(--space-lg); display: flex; justify-content: center; }

@media (max-width: 480px) {
  .order-main { flex-wrap: wrap; }
  .order-meta { flex-direction: column; gap: 2px; }
}
</style>
