<template>
  <div class="detail-page">
    <!-- ── Loading ── -->
    <div v-if="loading" class="detail-loading">
      <div class="skeleton-detail">
        <div class="skel-image"></div>
        <div class="skel-info">
          <div class="skel-line w-60 lg"></div>
          <div class="skel-line w-30 xl"></div>
          <div class="skel-line w-40"></div>
          <div class="skel-line w-80"></div>
        </div>
      </div>
    </div>

    <!-- ── Not Found ── -->
    <div v-else-if="!detail" class="empty-state">
      <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
        <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
        <rect x="35" y="40" width="50" height="45" rx="4" stroke="var(--color-muted)" stroke-width="2.5" fill="none"/>
        <circle cx="52" cy="55" r="6" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
        <path d="m42 92 12-16 8 10 6-8 12 14" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
      </svg>
      <h2>商品不存在</h2>
      <p>该商品可能已下架或删除</p>
      <button class="btn-primary" @click="$router.push('/')">返回首页</button>
    </div>

    <!-- ── Content ── -->
    <template v-else>
      <!-- 面包屑 -->
      <nav class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span class="sep">›</span>
        <router-link to="/goods">全部商品</router-link>
        <span class="sep">›</span>
        <router-link v-if="detail.categoryId" :to="`/goods?category=${detail.categoryId}`">
          {{ detail.categoryName || detail.categoryId }}
        </router-link>
        <span v-if="detail.categoryId" class="sep">›</span>
        <span class="current">{{ detail.title }}</span>
      </nav>

      <!-- 上排：图片 + 基本信息 -->
      <div class="detail-top">
        <!-- 左：图片 -->
        <div class="gallery-col">
          <div class="gallery-main">
            <img
              v-if="images.length"
              :src="images[currentImg]"
              :alt="detail.title"
              @error="onImgError"
              @click="previewOpen = true"
            />
            <div v-else class="img-placeholder">暂无图片</div>
            <!-- 状态标签 -->
            <span v-if="detail.status === 2" class="status-badge sold">已售出</span>
            <span v-else-if="detail.status === 0" class="status-badge offline">已下架</span>
          </div>
          <!-- 缩略图 -->
          <div class="thumb-list" v-if="images.length > 1">
            <img
              v-for="(img, i) in images"
              :key="i"
              :src="img"
              class="thumb"
              :class="{ active: i === currentImg }"
              @click="currentImg = i"
              @error="onThumbError"
            />
          </div>
        </div>

        <!-- 右：基本信息 -->
        <div class="info-col">
          <h1 class="detail-title">{{ detail.title }}</h1>

          <div class="tag-row">
            <span class="condition-badge" :class="detail.condition">
              {{ conditionMap[detail.condition] || detail.condition || '未知' }}
            </span>
            <span class="meta-item"><i class="bi bi-eye"></i> {{ detail.viewCount || 0 }} 次浏览</span>
            <span class="meta-item"><i class="bi bi-heart"></i> {{ detail.favCount || 0 }} 人收藏</span>
          </div>

          <!-- 价格卡片 -->
          <div class="price-card">
            <span class="price-current">¥{{ detail.price }}</span>
            <span v-if="detail.originalPrice" class="price-original">原价 ¥{{ detail.originalPrice }}</span>
          </div>

          <!-- 属性表 -->
          <table class="info-table">
            <tr>
              <th>分类</th>
              <td>
                <router-link v-if="detail.categoryId" :to="`/goods?category=${detail.categoryId}`">
                  {{ detail.categoryName || '未分类' }}
                </router-link>
                <span v-else>未分类</span>
              </td>
            </tr>
            <tr>
              <th>成色</th>
              <td>{{ conditionMap[detail.condition] || detail.condition || '未知' }}</td>
            </tr>
            <tr v-if="detail.location || detail.address">
              <th>地点</th>
              <td><i class="bi bi-geo-alt text-danger me-1"></i>{{ detail.location || detail.address }}</td>
            </tr>
            <tr>
              <th>发布时间</th>
              <td>{{ detail.createTime }}</td>
            </tr>
          </table>

          <!-- 操作按钮 -->
          <div class="action-area">
            <template v-if="loggedIn && !isOwner">
              <button class="btn-fav" :class="{ favorited: isFaved }" @click="toggleFavorite" :disabled="favLoading">
                <i :class="isFaved ? 'bi bi-heart-fill' : 'bi bi-heart'"></i>
                {{ isFaved ? '已收藏' : '收藏' }}
              </button>
              <button class="btn-contact" @click="openContact">
                <i class="bi bi-chat-dots me-1"></i>联系卖家
              </button>
              <button
                class="btn-buy"
                :disabled="detail.status !== 1 || buyLoading"
                @click="handleBuy"
              >
                <span v-if="buyLoading" class="spinner"></span>
                {{ detail.status === 1 ? '立即购买' : detail.status === 2 ? '已售出' : '已下架' }}
              </button>
            </template>
            <template v-else-if="!loggedIn">
              <button class="btn-login-buy" @click="requireLogin">
                <i class="bi bi-box-arrow-in-right me-1"></i>登录后购买
              </button>
            </template>
            <template v-else-if="isOwner">
              <div class="owner-bar">
                <span>这是我发布的商品</span>
                <button class="btn-edit" @click="$router.push(`/edit/${detail.itemId}`)">
                  <i class="bi bi-pencil me-1"></i>编辑商品
                </button>
              </div>
            </template>
          </div>
        </div>
      </div>

      <!-- 下排：描述 + 卖家 -->
      <div class="detail-bottom">
        <!-- 左：商品描述 + 相关推荐 -->
        <div class="bottom-left">
          <!-- 描述 -->
          <div class="section-card">
            <div class="card-header">
              <h6><i class="bi bi-file-text me-2"></i>商品描述</h6>
            </div>
            <div class="card-body">
              <p v-if="detail.description" class="desc-text">{{ detail.description }}</p>
              <p v-else class="muted-text">卖家暂无描述</p>
            </div>
          </div>

          <!-- 相关商品 -->
          <div class="related-section" v-if="relatedItems.length">
            <h6 class="related-title"><i class="bi bi-grid me-2"></i>相关商品</h6>
            <div class="related-grid">
              <GoodsCard v-for="item in relatedItems" :key="item.id || item.itemId" :goods="item" />
            </div>
          </div>
        </div>

        <!-- 右：卖家卡片 -->
        <div class="seller-col">
          <div class="seller-card">
            <div class="seller-avatar-lg">{{ (detail.sellerName || '?')[0] }}</div>
            <h6 class="seller-name-lg">{{ detail.sellerName }}</h6>
            <p class="seller-dept" v-if="detail.sellerDept">{{ detail.sellerDept }}</p>

            <div class="seller-stats">
              <div class="stat-item">
                <div class="stat-num">{{ detail.sellerSoldCount || 0 }}</div>
                <div class="stat-label">已售</div>
              </div>
              <div class="stat-item">
                <div class="stat-num text-success">{{ detail.sellerItemCount || 0 }}</div>
                <div class="stat-label">在售</div>
              </div>
            </div>

            <button v-if="detail.sellerId" class="btn-seller-home" @click="$router.push(`/user/${detail.sellerId}`)">
              <i class="bi bi-person me-1"></i>查看主页
            </button>
          </div>
        </div>
      </div>

      <!-- 联系卖家弹窗 -->
      <ChatModal
        ref="chatModalRef"
        :item-id="detail.itemId"
        :receiver-id="chatReceiverId || detail.sellerId"
        :seller-name="isOwner ? '买家' : detail.sellerName"
        :item-title="detail.title"
      />

      <!-- 图片预览弹窗 -->
      <teleport to="body">
        <div v-if="previewOpen" class="preview-overlay" @click="previewOpen = false">
          <button class="preview-close" @click="previewOpen = false">&times;</button>
          <button v-if="images.length > 1" class="preview-nav prev" @click.stop="prevImg">&lsaquo;</button>
          <img :src="images[currentImg]" class="preview-img" @click.stop />
          <button v-if="images.length > 1" class="preview-nav next" @click.stop="nextImg">&rsaquo;</button>
        </div>
      </teleport>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getItemDetail, getItems } from '../api/item'
import { createOrder } from '../api/order'
import { addFavorite, checkFavorite, deleteFavorite, getFavorites } from '../api/favorite'
import GoodsCard from '../components/GoodsCard.vue'
import ChatModal from '../components/ChatModal.vue'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')
const route = useRoute()
const router = useRouter()

const detail = ref(null)
const loading = ref(true)
const isFaved = ref(false)
const favLoading = ref(false)
const buyLoading = ref(false)
const isOwner = ref(false)
const currentImg = ref(0)
const previewOpen = ref(false)
const chatModalRef = ref(null)
const chatReceiverId = ref(null)  // 从对话列表来时指定的接收者

function openContact() {
  if (!loggedIn.value) { requireLogin(); return }
  chatModalRef.value?.open()
}
const relatedItems = ref([])

const conditionMap = { new: '全新', like_new: '九成新', good: '七成新', fair: '五成新' }

const images = computed(() => {
  if (!detail.value) return []
  const list = []
  if (detail.value.imageUrl) list.push(detail.value.imageUrl)
  if (detail.value.images) list.push(...detail.value.images)
  return [...new Set(list)]
})

function prevImg() { currentImg.value = (currentImg.value - 1 + images.value.length) % images.value.length }
function nextImg() { currentImg.value = (currentImg.value + 1) % images.value.length }

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getItemDetail(route.params.id)
    detail.value = res.data
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user.id === detail.value.sellerId) isOwner.value = true
    // 加载相关商品
    if (detail.value.categoryId) {
      try {
        const rRes = await getItems({ categoryId: detail.value.categoryId, page: 1, pageSize: 5 })
        relatedItems.value = (rRes?.data?.records || [])
          .filter((i) => (i.itemId || i.id) !== detail.value.itemId)
          .slice(0, 4)
          .map((i) => ({ ...i, id: i.id || i.itemId, image: i.image || i.imageUrl }))
      } catch { relatedItems.value = [] }
    }
  } catch (e) {
    detail.value = null
  } finally {
    loading.value = false
  }
}

async function checkFav() {
  try { const res = await checkFavorite(route.params.id); isFaved.value = res.data } catch { /* ignore */ }
}

async function toggleFavorite() {
  if (!loggedIn.value) { requireLogin(); return }
  favLoading.value = true
  try {
    if (isFaved.value) {
      const res = await getFavorites()
      const fav = res.data.find((f) => f.itemId === detail.value.itemId)
      if (fav) await deleteFavorite(fav.favoriteId)
    } else {
      await addFavorite({ itemId: detail.value.itemId, userId: 0 })
    }
    isFaved.value = !isFaved.value
    ElMessage.success(isFaved.value ? '收藏成功' : '已取消收藏')
  } catch { /* handled */ }
  finally { favLoading.value = false }
}

async function handleBuy() {
  if (!loggedIn.value) { requireLogin(); return }
  if (detail.value.status !== 1) return
  buyLoading.value = true
  try {
    await createOrder({ itemId: detail.value.itemId })
    ElMessage.success('下单成功！请在我的订单中查看')
    await fetchDetail()
  } catch { /* handled */ }
  finally { buyLoading.value = false }
}

function onImgError(e) {
  e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300"><rect fill="%23f5f6f8" width="400" height="300"/><text x="200" y="155" text-anchor="middle" fill="%23adb5bd" font-size="16">暂无图片</text></svg>'
}
function onThumbError(e) {
  e.target.style.display = 'none'
}

onMounted(async () => {
  await fetchDetail()
  checkFav()
  // 从"我的消息"页面跳转过来时自动打开聊天
  const openChatRaw = sessionStorage.getItem('openChat')
  if (openChatRaw) {
    try {
      const openChat = JSON.parse(openChatRaw)
      if (String(openChat.itemId) === String(route.params.id)) {
        sessionStorage.removeItem('openChat')
        if (openChat.receiverId) chatReceiverId.value = openChat.receiverId
        setTimeout(() => chatModalRef.value?.open(), 300)
      }
    } catch {
      // 旧格式兼容：纯数字 itemId
      sessionStorage.removeItem('openChat')
    }
  }
})
</script>

<style scoped>
.detail-page { max-width: 1100px; margin: 0 auto; padding: var(--space-lg); }

/* ── Breadcrumb ── */
.breadcrumb {
  display: flex; align-items: center; gap: 6px; flex-wrap: wrap;
  font-size: 0.8125rem; margin-bottom: var(--space-md);
}
.breadcrumb a { color: var(--color-primary); text-decoration: none; }
.breadcrumb a:hover { text-decoration: underline; }
.breadcrumb .sep { color: var(--color-muted); }
.breadcrumb .current { color: var(--color-muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 200px; }

/* ── Top: image + info ── */
.detail-top { display: grid; grid-template-columns: 5fr 7fr; gap: var(--space-xl); margin-bottom: var(--space-xl); }

/* ── Gallery ── */
.gallery-main {
  position: relative; border-radius: var(--radius-md); overflow: hidden;
  background: var(--color-surface); border: 1px solid var(--color-border);
  height: 380px; display: flex; align-items: center; justify-content: center;
}
.gallery-main img {
  max-width: 100%; max-height: 100%; object-fit: contain; cursor: zoom-in;
}
.img-placeholder {
  color: var(--color-muted); font-size: 0.9375rem;
}
.status-badge {
  position: absolute; top: 12px; right: 12px;
  padding: 4px 14px; border-radius: var(--radius-full);
  font-size: 0.75rem; font-weight: 600;
}
.status-badge.sold { background: rgba(0,0,0,0.7); color: #fff; }
.status-badge.offline { background: rgba(255,255,255,0.9); color: var(--color-muted); }

.thumb-list { display: flex; gap: 8px; margin-top: 8px; flex-wrap: wrap; }
.thumb {
  width: 60px; height: 60px; object-fit: cover; border-radius: var(--radius-sm);
  border: 2px solid var(--color-border); cursor: pointer;
  transition: border-color var(--duration-fast);
}
.thumb:hover, .thumb.active { border-color: var(--color-primary); }

/* ── Info ── */
.detail-title { font-size: 1.375rem; font-weight: 700; color: var(--color-ink); margin: 0 0 10px 0; }

.tag-row {
  display: flex; align-items: center; gap: 12px; flex-wrap: wrap;
  margin-bottom: var(--space-md); font-size: 0.8125rem;
}
.condition-badge {
  padding: 3px 10px; border-radius: var(--radius-sm);
  font-size: 0.75rem; font-weight: 500;
  background: var(--color-surface); color: var(--color-muted);
}
.condition-badge.new { background: var(--color-success-light); color: var(--color-success); }
.condition-badge.like_new { background: #cff4fc; color: #055160; }
.condition-badge.good { background: var(--color-accent-light); color: var(--color-primary); }
.meta-item { color: var(--color-muted); display: flex; align-items: center; gap: 4px; }

.price-card {
  background: #f8d7da; border-radius: var(--radius-md); padding: 14px 16px;
  margin-bottom: var(--space-md); display: flex; align-items: baseline; gap: 12px;
}
.price-current { font-size: 1.75rem; font-weight: 700; color: var(--color-price); }
.price-original { font-size: 0.8125rem; color: var(--color-muted); text-decoration: line-through; }

/* ── Info Table ── */
.info-table { width: 100%; margin-bottom: var(--space-md); }
.info-table th, .info-table td { padding: 6px 0; font-size: 0.875rem; vertical-align: top; }
.info-table th { color: var(--color-muted); font-weight: 400; width: 72px; text-align: left; padding-left: 0; }
.info-table td { color: var(--color-ink); }
.info-table a { color: var(--color-primary); text-decoration: none; }
.info-table a:hover { text-decoration: underline; }

/* ── Actions ── */
.action-area { display: flex; gap: 12px; }
.btn-fav {
  display: flex; align-items: center; gap: 6px; padding: 12px 20px;
  border: 1px solid var(--color-border); border-radius: var(--radius-md);
  background: var(--color-bg); color: var(--color-ink);
  font-size: 0.9375rem; font-family: var(--font-family); cursor: pointer;
  transition: all var(--duration-fast);
}
.btn-fav:hover { border-color: var(--color-danger); color: var(--color-danger); }
.btn-fav.favorited { color: var(--color-danger); border-color: var(--color-danger); background: var(--color-danger-light); }

.btn-contact {
  display: flex; align-items: center; gap: 6px; padding: 12px 16px;
  border: 1px solid var(--color-primary); border-radius: var(--radius-md);
  background: var(--color-bg); color: var(--color-primary);
  font-size: 0.9375rem; font-family: var(--font-family); cursor: pointer;
  transition: all var(--duration-fast); white-space: nowrap;
}
.btn-contact:hover { background: var(--color-accent-light); }

.btn-buy {
  flex: 1; padding: 12px 0; background: var(--color-primary); color: #fff;
  border: none; border-radius: var(--radius-md); font-size: 1rem; font-weight: 600;
  font-family: var(--font-family); cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 8px;
  transition: background var(--duration-fast);
}
.btn-buy:hover:not(:disabled) { background: var(--color-primary-hover); }
.btn-buy:disabled { background: var(--color-surface); color: var(--color-muted); cursor: not-allowed; }

.btn-login-buy {
  flex: 1; padding: 12px 0; background: var(--color-primary); color: #fff;
  border: none; border-radius: var(--radius-md); font-size: 1rem; font-weight: 600;
  font-family: var(--font-family); cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: background var(--duration-fast);
}
.btn-login-buy:hover { background: var(--color-primary-hover); }

.owner-bar {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px;
  background: var(--color-accent-light); border-radius: var(--radius-md);
  font-size: 0.875rem; color: var(--color-primary); width: 100%;
}
.btn-edit {
  margin-left: auto; padding: 8px 18px; background: var(--color-bg);
  color: var(--color-primary); border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm); font-size: 0.8125rem;
  font-family: var(--font-family); cursor: pointer;
  display: flex; align-items: center; gap: 4px;
  transition: all var(--duration-fast);
}
.btn-edit:hover { background: var(--color-primary); color: #fff; }

.spinner {
  width: 16px; height: 16px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Bottom row ── */
.detail-bottom { display: grid; grid-template-columns: 8fr 4fr; gap: var(--space-xl); }

/* ── Section Card ── */
.section-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); overflow: hidden; margin-bottom: var(--space-lg);
}
.card-header {
  padding: 12px 16px; background: var(--color-bg);
  border-bottom: 1px solid var(--color-divider);
}
.card-header h6 { margin: 0; font-size: 0.9375rem; font-weight: 700; color: var(--color-ink); display: flex; align-items: center; }
.card-header i { color: var(--color-primary); }
.card-body { padding: 16px; }
.desc-text { white-space: pre-line; line-height: 1.7; margin: 0; font-size: 0.875rem; color: var(--color-ink); }
.muted-text { color: var(--color-muted); margin: 0; }

/* ── Related ── */
.related-title {
  font-size: 0.9375rem; font-weight: 700; margin-bottom: var(--space-md);
  display: flex; align-items: center; color: var(--color-ink);
}
.related-title i { color: var(--color-primary); }
.related-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: var(--space-md);
}

/* ── Seller Card ── */
.seller-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); padding: 20px; text-align: center;
  position: sticky; top: 72px;
}
.seller-avatar-lg {
  width: 64px; height: 64px; border-radius: 50%;
  background: var(--color-accent-light); color: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 1.5rem; font-weight: 700; margin: 0 auto 8px;
  border: 2px solid var(--color-border);
}
.seller-name-lg { font-size: 1rem; font-weight: 700; margin: 0 0 4px 0; color: var(--color-ink); }
.seller-dept { font-size: 0.8125rem; color: var(--color-muted); margin: 0 0 12px 0; }

.seller-stats {
  display: grid; grid-template-columns: 1fr 1fr;
  border-top: 1px solid var(--color-divider); padding-top: 12px;
  margin-bottom: 12px;
}
.stat-num { font-size: 1.125rem; font-weight: 700; color: var(--color-primary); }
.stat-num.text-success { color: var(--color-success) !important; }
.stat-label { font-size: 0.75rem; color: var(--color-muted); }

.btn-seller-home {
  width: 100%; padding: 8px 0; background: var(--color-bg);
  color: var(--color-primary); border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm); font-size: 0.8125rem;
  font-family: var(--font-family); cursor: pointer;
  transition: all var(--duration-fast);
}
.btn-seller-home:hover { background: var(--color-primary); color: #fff; }

/* ── Skeleton ── */
.skeleton-detail { display: grid; grid-template-columns: 1fr 1fr; gap: var(--space-xl); }
.skel-image { aspect-ratio: 4/3; background: var(--color-surface); border-radius: var(--radius-md); animation: shimmer 1.5s infinite; }
.skel-info { display: flex; flex-direction: column; gap: var(--space-md); }
.skel-line { height: 16px; background: var(--color-surface); border-radius: var(--radius-sm); animation: shimmer 1.5s infinite; }
.skel-line.lg { height: 24px; }
.skel-line.xl { height: 36px; }
.skel-line.w-20 { width: 20%; }
.skel-line.w-30 { width: 30%; }
.skel-line.w-40 { width: 40%; }
.skel-line.w-60 { width: 60%; }
.skel-line.w-80 { width: 80%; }
@keyframes shimmer { 0%,100% { opacity:1; } 50% { opacity:.5; } }

/* ── Empty ── */
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); }
.empty-icon { width: 120px; height: 120px; margin-bottom: var(--space-md); }
.empty-state h2 { font-size: 1.25rem; margin-bottom: var(--space-sm); color: var(--color-ink); }
.empty-state p { color: var(--color-muted); margin-bottom: var(--space-lg); }
.btn-primary { padding: 10px 28px; background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-md); font-size: 0.875rem; font-weight: 500; font-family: var(--font-family); cursor: pointer; }

/* ── Preview Modal ── */
.preview-overlay {
  position: fixed; inset: 0; z-index: var(--z-modal);
  background: rgba(0,0,0,0.9); display: flex; align-items: center; justify-content: center;
}
.preview-close {
  position: absolute; top: 16px; right: 24px; background: none; border: none;
  color: #fff; font-size: 2rem; cursor: pointer; z-index: 1;
}
.preview-nav {
  position: absolute; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: #fff; font-size: 3rem;
  cursor: pointer; padding: 16px; z-index: 1;
}
.preview-nav.prev { left: 8px; }
.preview-nav.next { right: 8px; }
.preview-img { max-width: 90vw; max-height: 85vh; object-fit: contain; }

/* ── Responsive ── */
@media (max-width: 768px) {
  .detail-top { grid-template-columns: 1fr; }
  .detail-bottom { grid-template-columns: 1fr; }
  .gallery-main { height: 260px; }
  .seller-card { position: static; }
  .detail-page { padding: var(--space-md); }
  .related-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
