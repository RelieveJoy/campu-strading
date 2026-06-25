<template>
  <div class="user-page">
    <!-- Loading -->
    <div v-if="loading" class="loading-state">加载中...</div>

    <!-- Not Found -->
    <div v-else-if="!profile" class="empty-state">
      <h2>用户不存在</h2>
      <button class="btn-primary" @click="$router.push('/')">返回首页</button>
    </div>

    <template v-else>
      <div class="user-layout">
        <!-- 左侧菜单 -->
        <aside class="user-sidebar">
          <ul class="side-menu">
            <!-- 用户主页 -->
            <li class="menu-item" :class="{ active: currentView === 'home' }">
              <a class="menu-link" @click="goView('home')">
                <span class="menu-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                </span>
                <span>我的主页</span>
              </a>
            </li>

            <!-- 我的交易 -->
            <li class="menu-item" :class="{ open: menuOpen.trade, active: currentView === 'items' || currentView === 'sold' || currentView === 'bought' }">
              <a class="menu-link" @click="menuOpen.trade = !menuOpen.trade">
                <span class="menu-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><rect x="1" y="3" width="15" height="13" rx="2"/><polygon points="23 7 16 12 16 7 23 7 23 20"/></svg>
                </span>
                <span>我的交易</span>
                <svg class="menu-arrow" :class="{ rotated: menuOpen.trade }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><polyline points="9 18 15 12 9 6"/></svg>
              </a>
              <ul class="sub-menu" v-show="menuOpen.trade">
                <li class="sub-item" :class="{ active: currentView === 'items' }" @click="goView('items')">
                  <span>我发布的</span>
                </li>
                <li class="sub-item" :class="{ active: currentView === 'sold' }" @click="goView('sold')">
                  <span>我卖出的</span>
                </li>
                <li class="sub-item" :class="{ active: currentView === 'bought' }" @click="goView('bought')">
                  <span>我买到的</span>
                </li>
              </ul>
            </li>

            <!-- 我的收藏 -->
            <li class="menu-item" :class="{ active: currentView === 'favorites' }">
              <a class="menu-link" @click="goView('favorites')">
                <span class="menu-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
                </span>
                <span>我的收藏</span>
              </a>
            </li>

            <!-- 系统设置 -->
            <li class="menu-item" :class="{ open: menuOpen.settings, active: currentView === 'profile' || currentView === 'security' }">
              <a class="menu-link" @click="menuOpen.settings = !menuOpen.settings">
                <span class="menu-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
                </span>
                <span>系统设置</span>
                <svg class="menu-arrow" :class="{ rotated: menuOpen.settings }" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><polyline points="9 18 15 12 9 6"/></svg>
              </a>
              <ul class="sub-menu" v-show="menuOpen.settings">
                <li class="sub-item" :class="{ active: currentView === 'profile' }" @click="goView('profile')">
                  <span>个人资料</span>
                </li>
                <li class="sub-item" :class="{ active: currentView === 'security' }" @click="goView('security')">
                  <span>账号与安全</span>
                </li>
              </ul>
            </li>

          </ul>
        </aside>

        <!-- ───────── 右侧：我的主页 ───────── -->
        <template v-if="currentView === 'home'">
          <div class="user-main">
            <div class="profile-card">
              <div class="avatar-wrap">
                <div class="avatar">{{ (profile.username || '?')[0] }}</div>
              </div>
              <div class="profile-info">
                <h2 class="profile-name">{{ profile.username }}</h2>
                <div class="profile-meta">
                  <span>{{ profile.totalItems || 0 }} 在售</span>
                  <span class="meta-sep">|</span>
                  <span>{{ profile.totalSold || 0 }} 已售</span>
                  <span class="meta-sep">|</span>
                  <span>
                    <RatingStars v-if="profile.overallRating > 0" :average-rating="profile.overallRating" :review-count="profile.ratedItemCount" mode="readonly" />
                    <span v-else>暂无评分</span>
                  </span>
                </div>
              </div>
            </div>

            <div class="tab-bar">
              <button class="tab-item" :class="{ active: homeTab === 'items' }" @click="homeTab = 'items'">
                宝贝<span class="tab-underline" v-if="homeTab === 'items'"></span>
              </button>
              <button class="tab-item" :class="{ active: homeTab === 'reviews' }" @click="homeTab = 'reviews'">
                信用及评价<span class="tab-underline" v-if="homeTab === 'reviews'"></span>
              </button>
            </div>

            <!-- 主页-宝贝 -->
            <template v-if="homeTab === 'items'">
              <div v-if="itemsLoading" class="loading-state">加载中...</div>
              <div v-else-if="items.length === 0" class="empty-box">
                <svg viewBox="0 0 120 120" fill="none" class="empty-icon">
                  <circle cx="60" cy="60" r="54" fill="var(--color-surface)"/>
                  <rect x="35" y="40" width="50" height="45" rx="4" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
                  <path d="m42 92 12-16 8 10 6-8 12 14" stroke="var(--color-muted)" stroke-width="2" fill="none"/>
                </svg>
                <p>暂无商品</p>
              </div>
              <div v-else>
                <div class="goods-grid">
                  <GoodsCard v-for="item in items" :key="item.id || item.itemId" :goods="item" />
                </div>
                <div class="pagination-wrap" v-if="itemsTotal > pageSize">
                  <el-pagination :current-page="itemsPage" :page-size="pageSize" :total="itemsTotal"
                    layout="prev, pager, next" @current-change="onItemsPageChange" background />
                </div>
              </div>
            </template>

            <!-- 主页-信用及评价 -->
            <template v-if="homeTab === 'reviews'">
              <div v-if="reviewsLoading" class="loading-state">加载中...</div>
              <div v-else-if="reviews.length === 0" class="empty-box">
                <p>暂无评价</p>
              </div>
              <div v-else>
                <div class="review-list">
                  <div v-for="r in reviews" :key="r.reviewId" class="review-card">
                    <div class="review-top">
                      <span class="reviewer-avatar">{{ (r.reviewerName || '?')[0] }}</span>
                      <div class="reviewer-info">
                        <span class="reviewer-name">{{ r.reviewerName }}</span>
                        <span class="review-time">{{ r.createTime }}</span>
                      </div>
                      <span class="review-stars">
                        <i v-for="i in 5" :key="i" class="bi" :class="i <= r.rating ? 'bi-star-fill' : 'bi-star'" :style="{ color: i <= r.rating ? '#ffb800' : '#ddd', fontSize: '0.75rem' }"></i>
                      </span>
                    </div>
                    <p class="review-content" v-if="r.content">{{ r.content }}</p>
                    <span class="review-item-title" v-if="r.itemTitle">商品：{{ r.itemTitle }}</span>
                  </div>
                </div>
                <div class="pagination-wrap" v-if="reviewsTotal > reviewPageSize">
                  <el-pagination :current-page="reviewsPage" :page-size="reviewPageSize" :total="reviewsTotal"
                    layout="prev, pager, next" @current-change="onReviewsPageChange" background />
                </div>
              </div>
            </template>

          </div>
        </template>

        <!-- ───────── 右侧：我发布的 / 我卖出的 ───────── -->
        <template v-if="currentView === 'items' || currentView === 'sold'">
          <div class="user-main">
            <h3 class="section-title">{{ currentView === 'items' ? '我发布的' : '我卖出的' }}</h3>
            <div v-if="itemsLoading" class="loading-state">加载中...</div>
            <div v-else-if="items.length === 0" class="empty-box">
              <p>{{ currentView === 'sold' ? '暂无已售商品' : '暂无商品' }}</p>
            </div>
            <div v-else>
              <div class="goods-grid">
                <GoodsCard v-for="item in items" :key="item.id || item.itemId" :goods="item" />
              </div>
              <div class="pagination-wrap" v-if="itemsTotal > pageSize">
                <el-pagination :current-page="itemsPage" :page-size="pageSize" :total="itemsTotal"
                  layout="prev, pager, next" @current-change="onItemsPageChange" background />
              </div>
            </div>
          </div>
        </template>

        <!-- ───────── 右侧：我买到的 ───────── -->
        <template v-if="currentView === 'bought'">
          <div class="user-main">
            <h3 class="section-title">我买到的</h3>
            <div v-if="boughtLoading" class="loading-state">加载中...</div>
            <div v-else-if="boughtOrders.length === 0" class="empty-box">
              <p>暂无购买记录</p>
            </div>
            <div v-else>
              <div v-for="o in boughtOrders" :key="o.orderId" class="bought-card">
                <div class="bought-img" @click="$router.push('/item/' + o.itemId)">
                  <img v-if="o.itemImageUrl" :src="o.itemImageUrl" :alt="o.itemTitle" />
                  <div v-else class="img-placeholder">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" width="36" height="36"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/></svg>
                  </div>
                </div>
                <div class="bought-body">
                  <div class="bought-top">
                    <span class="bought-title" @click="$router.push('/item/' + o.itemId)">{{ o.itemTitle }}</span>
                    <span class="bought-status" :class="'bs-' + o.status">{{ ['','待确认','已完成','已取消'][o.status] }}</span>
                  </div>
                  <span class="bought-price">¥{{ o.amount }}</span>
                  <span class="bought-meta">卖家：{{ o.sellerName }} · {{ o.createTime }}</span>
                  <div class="bought-actions">
                    <button class="bought-btn" @click.stop="contactSeller(o.sellerId, o.sellerName)">联系卖家</button>
                  </div>
                </div>
              </div>
              <div class="pagination-wrap" v-if="boughtTotal > pageSize">
                <el-pagination :current-page="boughtPage" :page-size="pageSize" :total="boughtTotal"
                  layout="prev, pager, next" @current-change="onBoughtPageChange" background />
              </div>
            </div>
          </div>
        </template>

        <!-- ───────── 右侧：我的收藏 ───────── -->
        <template v-if="currentView === 'favorites'">
          <div class="user-main">
            <h3 class="section-title">我的收藏</h3>
            <div v-if="favLoading" class="loading-state">加载中...</div>
            <div v-else-if="favItems.length === 0" class="empty-box">
              <p>暂无收藏</p>
            </div>
            <div v-else>
              <div class="goods-grid">
                <GoodsCard v-for="item in favItems" :key="item.id || item.itemId" :goods="item" />
              </div>
              <div class="pagination-wrap" v-if="favTotal > pageSize">
                <el-pagination :current-page="favPage" :page-size="pageSize" :total="favTotal"
                  layout="prev, pager, next" @current-change="onFavPageChange" background />
              </div>
            </div>
          </div>
        </template>

        <!-- ───────── 右侧：个人资料 ───────── -->
        <template v-if="currentView === 'profile'">
          <div class="user-main">
            <h3 class="section-title">个人资料</h3>
            <div class="settings-section">
              <div class="settings-title">基本信息</div>
              <div class="settings-row">
                <span class="settings-label">头像</span>
                <span class="settings-value">
                  <span class="settings-avatar-lg">{{ (profile?.username || '?')[0] }}</span>
                </span>
              </div>
              <div class="settings-row">
                <span class="settings-label">昵称</span>
                <span class="settings-value">
                  <template v-if="editingNick">
                    <input v-model="nickForm" class="settings-input" @keyup.enter="saveNick" />
                    <button class="settings-link" @click="saveNick">保存</button>
                    <button class="settings-link cancel" @click="editingNick = false">取消</button>
                  </template>
                  <template v-else>
                    <span>{{ profile?.username }}</span>
                    <button class="settings-link" @click="editingNick = true">编辑</button>
                  </template>
                </span>
              </div>
              <div class="settings-row">
                <span class="settings-label">简介</span>
                <span class="settings-value">
                  <template v-if="editingBio">
                    <input v-model="bioForm" class="settings-input" placeholder="介绍一下自己吧" @keyup.enter="saveBio" />
                    <button class="settings-link" @click="saveBio">保存</button>
                    <button class="settings-link cancel" @click="editingBio = false">取消</button>
                  </template>
                  <template v-else>
                    <span class="settings-hint">{{ profile?.bio || '未设置' }}</span>
                    <button class="settings-link" @click="editingBio = true">编辑</button>
                  </template>
                </span>
              </div>
              <div class="settings-row">
                <span class="settings-label">性别</span>
                <span class="settings-value">
                  <template v-if="editingGender">
                    <select v-model="genderForm" class="settings-select">
                      <option value="">请选择</option>
                      <option value="male">男</option>
                      <option value="female">女</option>
                      <option value="secret">保密</option>
                    </select>
                    <button class="settings-link" @click="saveGender">保存</button>
                    <button class="settings-link cancel" @click="editingGender = false">取消</button>
                  </template>
                  <template v-else>
                    <span class="settings-hint">{{ {male:'男',female:'女',secret:'保密'}[profile?.gender] || '未设置' }}</span>
                    <button class="settings-link" @click="editingGender = true">编辑</button>
                  </template>
                </span>
              </div>
              <div class="settings-row">
                <span class="settings-label">生日</span>
                <span class="settings-value">
                  <span class="settings-hint">{{ profile?.birthday || '未设置' }}</span>
                  <button class="settings-link" @click="editingBirthday = true">编辑</button>
                </span>
              </div>
              <div class="settings-row">
                <span class="settings-label">个性装扮</span>
                <span class="settings-value">
                  <span class="settings-hint">敬请期待</span>
                </span>
              </div>
            </div>
          </div>

          <!-- 生日选择弹窗 -->
          <el-dialog v-model="editingBirthday" title="选择生日" width="360px" :close-on-click-modal="false">
            <el-date-picker v-model="birthdayForm" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width:100%" />
            <template #footer>
              <el-button @click="editingBirthday = false">取消</el-button>
              <el-button type="primary" @click="saveBirthday">确定</el-button>
            </template>
          </el-dialog>
        </template>

        <!-- ───────── 右侧：账号与安全 ───────── -->
        <template v-if="currentView === 'security'">
          <div class="user-main">
            <h3 class="section-title">账号与安全</h3>

            <div class="account-block">
              <div class="account-block-title">基本信息</div>
              <div class="account-row">
                <span class="ac-label">会员名</span>
                <span class="ac-val strong">{{ profile?.username }}</span>
              </div>
              <div class="account-row">
                <span class="ac-label">学号</span>
                <span class="ac-val">{{ studentId || '未绑定' }}</span>
              </div>
              <div class="account-row">
                <span class="ac-label">手机号</span>
                <span class="ac-val">{{ phone || '未绑定' }}</span>
              </div>
            </div>

            <div class="account-block">
              <div class="account-block-title">认证信息</div>
              <div class="account-row">
                <span class="ac-label">学生认证</span>
                <span class="ac-val">校园交易平台学生身份</span>
                <span class="ac-status success">已认证</span>
              </div>
              <div class="account-row">
                <span class="ac-label">手机绑定</span>
                <span class="ac-val">{{ phone ? '已绑定手机号 ' + phone : '绑定手机以保障账户安全' }}</span>
                <span class="ac-status" :class="phone ? 'success' : 'muted'">{{ phone ? '已绑定' : '未绑定' }}</span>
              </div>
            </div>

            <div class="account-block">
              <div class="account-block-title">安全中心</div>
              <div class="account-row">
                <span class="ac-label">安全中心</span>
                <span class="ac-val">学习账户保护、交易安全等相关知识</span>
                <span class="ac-link">查看 &gt;</span>
              </div>
              <div class="account-row">
                <span class="ac-label">修改密码</span>
                <span class="ac-val">定期修改密码保护账户安全</span>
                <span class="ac-link" @click="showPwdDialog = true">修改 &gt;</span>
              </div>
            </div>

            <div class="account-block">
              <div class="account-row">
                <span class="ac-label">退出登录</span>
                <span class="ac-val">退出当前登录的账户</span>
                <span class="ac-link danger" @click="handleLogout">退出 &gt;</span>
              </div>
            </div>
          </div>
        </template>

      </div>
    </template>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="400px" :close-on-click-modal="false">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPwd" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPwd" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="pwdForm.confirmPwd" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="handleChangePwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserProfile, getUserItems, updateUserInfo, editPassword } from '../api/user'
import { getUserReviewList } from '../api/review'
import { getOrders } from '../api/order'
import { getFavorites } from '../api/favorite'
import GoodsCard from '../components/GoodsCard.vue'
import RatingStars from '../components/RatingStars.vue'

const userData = JSON.parse(localStorage.getItem('user') || '{}')
const myId = userData.id || 0
const studentId = userData.studentId || ''
const phone = userData.phone || ''

const currentView = ref('home')   // home | items | sold | bought | favorites
const homeTab = ref('items')      // items | reviews
const menuOpen = reactive({ trade: true, settings: false })

const profile = ref(null)
const loading = ref(true)
const items = ref([])
const itemsLoading = ref(true)
const itemsTotal = ref(0)
const itemsPage = ref(1)
const pageSize = 8

const reviews = ref([])
const reviewsLoading = ref(true)
const reviewsTotal = ref(0)
const reviewsPage = ref(1)
const reviewPageSize = 10

const boughtOrders = ref([])
const boughtLoading = ref(true)
const boughtTotal = ref(0)
const boughtPage = ref(1)

const favItems = ref([])
const favLoading = ref(true)
const favTotal = ref(0)
const favPage = ref(1)

const showPwdDialog = ref(false)
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })

// 个人资料编辑
const editingNick = ref(false)
const nickForm = ref('')
const editingBio = ref(false)
const bioForm = ref('')
const editingGender = ref(false)
const genderForm = ref('')
const editingBirthday = ref(false)
const birthdayForm = ref('')

async function saveNick() {
  if (nickForm.value && nickForm.value !== profile.value?.username) {
    try {
      await updateUserInfo(myId, { username: nickForm.value })
      profile.value.username = nickForm.value
      ElMessage.success('昵称已更新')
    } catch {}
  }
  editingNick.value = false
}
async function saveBio() {
  try {
    await updateUserInfo(myId, { bio: bioForm.value || '' })
    profile.value.bio = bioForm.value
    ElMessage.success('简介已更新')
  } catch {}
  editingBio.value = false
}
async function saveGender() {
  try {
    await updateUserInfo(myId, { gender: genderForm.value || '' })
    profile.value.gender = genderForm.value
    ElMessage.success('性别已更新')
  } catch {}
  editingGender.value = false
}

watch(editingBio, (val) => { if (val) bioForm.value = profile.value?.bio || '' })
watch(editingGender, (val) => { if (val) genderForm.value = profile.value?.gender || '' })
watch(editingBirthday, (val) => { if (val) birthdayForm.value = profile.value?.birthday || '' })

async function saveBirthday() {
  try {
    await updateUserInfo(myId, { birthday: birthdayForm.value || '' })
    profile.value.birthday = birthdayForm.value
    ElMessage.success('生日已更新')
  } catch {}
  editingBirthday.value = false
}

function handleLogout() {
  ElMessageBox.confirm('确定退出登录？', '提示', { confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning' })
    .then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出')
      window.location.href = '/#/login'
    }).catch(() => {})
}

async function handleChangePwd() {
  if (!pwdForm.oldPwd || !pwdForm.newPwd || !pwdForm.confirmPwd) {
    ElMessage.warning('请填写完整')
    return
  }
  if (pwdForm.newPwd !== pwdForm.confirmPwd) {
    ElMessage.warning('两次新密码不一致')
    return
  }
  try {
    await editPassword({ oldPwd: pwdForm.oldPwd, newPwd: pwdForm.newPwd })
    ElMessage.success('密码修改成功')
    showPwdDialog.value = false
    pwdForm.oldPwd = ''; pwdForm.newPwd = ''; pwdForm.confirmPwd = ''
  } catch {}
}

async function fetchProfile() {
  loading.value = true
  try {
    const res = await getUserProfile(myId)
    profile.value = res.data || null
  } catch { profile.value = null }
  finally { loading.value = false }
}

async function fetchItems(status, page = 1) {
  itemsLoading.value = true
  try {
    const params = { page, pageSize }
    if (status != null) params.status = status
    const res = await getUserItems(myId, params)
    const data = res.data
    items.value = (data?.records || data || []).map(normalizeItem)
    itemsTotal.value = data?.total || 0
    itemsPage.value = page
  } catch { items.value = []; itemsTotal.value = 0 }
  finally { itemsLoading.value = false }
}

async function fetchReviews(page = 1) {
  reviewsLoading.value = true
  try {
    const res = await getUserReviewList(myId, { page, pageSize: reviewPageSize })
    const data = res.data
    reviews.value = data?.records || data || []
    reviewsTotal.value = data?.total || 0
    reviewsPage.value = page
  } catch { reviews.value = []; reviewsTotal.value = 0 }
  finally { reviewsLoading.value = false }
}

function onReviewsPageChange(p) {
  fetchReviews(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function normalizeItem(i) {
  return {
    ...i,
    id: i.id || i.itemId,
    image: i.image || i.imageUrl || i.itemImageUrl,
    title: i.title || i.name || i.itemTitle,
    price: i.price || i.itemPrice,
    condition: i.condition || i.quality,
    views: i.views || i.viewCount,
    location: i.location || i.address,
    averageRating: i.averageRating || 0,
    reviewCount: i.reviewCount || 0,
  }
}

function goView(view) {
  currentView.value = view
  itemsPage.value = 1
  if (view === 'home' || view === 'items') {
    fetchItems(1)
  } else if (view === 'sold') {
    fetchItems(2)
  } else if (view === 'bought') {
    fetchBoughtOrders()
  } else if (view === 'favorites') {
    fetchFavItems()
  }
}

async function fetchBoughtOrders(page = 1) {
  boughtLoading.value = true
  try {
    const res = await getOrders({ buyerId: myId, page, pageSize })
    const data = res.data
    boughtOrders.value = data?.records || data || []
    boughtTotal.value = data?.total || 0
    boughtPage.value = page
  } catch { boughtOrders.value = []; boughtTotal.value = 0 }
  finally { boughtLoading.value = false }
}

function contactSeller(sellerId, sellerName) {
  sessionStorage.setItem('openChat', JSON.stringify({ receiverId: sellerId, receiverName: sellerName }))
  window.location.href = '/#/'
}

function onBoughtPageChange(p) {
  fetchBoughtOrders(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function fetchFavItems(page = 1) {
  favLoading.value = true
  try {
    const res = await getFavorites()
    const all = (res.data || []).map(normalizeItem)
    favTotal.value = all.length
    favPage.value = page
    const start = (page - 1) * pageSize
    favItems.value = all.slice(start, start + pageSize)
  } catch { favItems.value = []; favTotal.value = 0 }
  finally { favLoading.value = false }
}

function onFavPageChange(p) {
  fetchFavItems(p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function onItemsPageChange(p) {
  let status
  if (currentView.value === 'items' || currentView.value === 'home') status = 1
  else if (currentView.value === 'sold') status = 2
  fetchItems(status, p)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(editingNick, (val) => { if (val) nickForm.value = profile.value?.username || '' })

onMounted(() => {
  fetchProfile()
  fetchItems(1)
  fetchReviews()
})
</script>

<style scoped>
.user-page { background: #f5f5f5; min-height: calc(100vh - 56px); }

/* ── Layout ── */
.user-layout { max-width: 1600px; margin: 0 auto; padding: 20px 16px; display: grid; grid-template-columns: 260px 1fr; gap: 16px; align-items: start; }

/* ── Left Sidebar ── */
.user-sidebar {
  position: sticky; top: 72px;
  background: #fff; border-radius: var(--radius-md); overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.side-menu { list-style: none; padding: 16px 0; margin: 0; }
.menu-item { }
.menu-link {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 20px; color: var(--color-ink); text-decoration: none;
  font-size: 0.9375rem; cursor: pointer;
  transition: background var(--duration-fast);
}
.menu-link:hover { background: var(--color-surface); }
.menu-item.active > .menu-link,
.menu-item.active > .menu-link:hover {
  background: var(--color-accent-light); color: var(--color-primary); font-weight: 600;
}
.menu-icon { display: flex; align-items: center; flex-shrink: 0; color: var(--color-muted); }
.menu-item.active .menu-icon { color: var(--color-primary); }
.menu-arrow {
  margin-left: auto; flex-shrink: 0; transition: transform var(--duration-fast);
  color: var(--color-muted);
}
.menu-arrow.rotated { transform: rotate(90deg); }

/* Sub menu */
.sub-menu { list-style: none; padding: 0; margin: 0; background: var(--color-surface); }
.sub-item {
  padding: 12px 16px 12px 54px; font-size: 0.875rem;
  color: var(--color-muted); cursor: pointer;
  transition: color var(--duration-fast), background var(--duration-fast);
}
.sub-item:hover { color: var(--color-ink); background: var(--color-divider); }
.sub-item.active { color: var(--color-primary); font-weight: 500; }

.menu-divider { height: 1px; background: var(--color-divider); margin: 4px 12px; }

/* ── Right Main ── */
.user-main {
  background: #fff; border-radius: var(--radius-md);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  padding: 0 24px 24px;
}

/* ── Profile Card ── */
.profile-card {
  display: flex; flex-direction: column; align-items: center;
  padding: 24px 0 16px;
}
.avatar-wrap { margin-bottom: 10px; }
.avatar {
  width: 96px; height: 96px; border-radius: 50%;
  background: var(--color-primary); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 2.5rem; font-weight: 700;
}
.profile-info { text-align: center; }
.profile-name { font-size: 1.375rem; font-weight: 700; color: var(--color-ink); margin: 0 0 6px 0; }
.profile-meta {
  display: flex; align-items: center; justify-content: center; gap: 8px;
  font-size: 0.8125rem; color: var(--color-muted);
}
.meta-sep { color: var(--color-border); }

/* ── Tab Bar ── */
.tab-bar {
  display: flex; justify-content: center; gap: 0;
  border-bottom: 1px solid var(--color-divider);
  margin-bottom: 20px;
}
.tab-item {
  position: relative; padding: 12px 28px; border: none;
  background: transparent; color: var(--color-muted);
  font-size: 0.9375rem; font-weight: 500; cursor: pointer;
  font-family: var(--font-family);
  transition: color var(--duration-fast);
}
.tab-item:hover { color: var(--color-ink); }
.tab-item.active { color: var(--color-primary); font-weight: 600; }
.tab-underline {
  position: absolute; bottom: -1px; left: 50%; transform: translateX(-50%);
  width: 32px; height: 3px; border-radius: 2px; background: var(--color-primary);
}

/* ── Grid ── */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-md);
}

/* ── Review Summary ── */
.review-summary {
  display: flex; justify-content: center; padding: 48px 0;
}
.review-score-wrap {
  text-align: center;
}
.review-big-score {
  font-size: 3.5rem; font-weight: 700; color: var(--color-primary); line-height: 1.1;
}
.review-score-label {
  font-size: 0.9375rem; color: var(--color-muted); margin: 8px 0 12px;
}
.review-score-stars {
  display: flex; justify-content: center; margin-bottom: 6px;
}
.no-rating-text {
  font-size: 0.875rem; color: var(--color-muted);
}
.review-count-text {
  font-size: 0.8125rem; color: var(--color-muted);
}

/* ── Reviews (legacy) ── */
.review-list { display: flex; flex-direction: column; gap: 2px; }
.review-card { padding: 14px 0; border-bottom: 1px solid var(--color-divider); }
.review-card:last-child { border-bottom: none; }
.review-top { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.reviewer-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--color-accent-light); color: var(--color-primary);
  display: flex; align-items: center; justify-content: center;
  font-size: 0.8125rem; font-weight: 700; flex-shrink: 0;
}
.reviewer-info { flex: 1; }
.reviewer-name { display: block; font-weight: 600; font-size: 0.875rem; color: var(--color-ink); }
.review-time { font-size: 0.75rem; color: var(--color-muted); }
.review-stars { display: flex; gap: 1px; flex-shrink: 0; }
.review-content { font-size: 0.875rem; color: var(--color-ink); line-height: 1.6; margin: 0; }
.review-item-title { font-size: 0.75rem; color: var(--color-muted); display: block; margin-top: 4px; }

/* ── States ── */
.loading-state { text-align: center; color: var(--color-muted); padding: var(--space-xxl); }
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }
.empty-box { text-align: center; padding: 48px; color: var(--color-muted); }
.empty-icon { width: 80px; height: 80px; margin-bottom: var(--space-md); display: block; margin-left: auto; margin-right: auto; }
.empty-state h2 { font-size: 1.25rem; margin-bottom: var(--space-sm); color: var(--color-ink); }
.btn-primary { padding: 10px 28px; background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-md); font-size: 0.875rem; font-weight: 500; font-family: var(--font-family); cursor: pointer; }

.section-title { font-size: 1.125rem; font-weight: 600; color: var(--color-ink); margin: 0 0 20px 0; padding-top: 24px; }

.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; padding-bottom: 8px; }

/* ── Bought card ── */
.bought-card {
  display: flex; gap: 16px; padding: 16px; border: 1px solid var(--color-divider);
  border-radius: var(--radius-md); margin-bottom: 12px; background: var(--color-bg);
  transition: box-shadow var(--duration-fast);
}
.bought-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.bought-img {
  width: 120px; height: 120px; border-radius: var(--radius-sm);
  overflow: hidden; flex-shrink: 0; background: var(--color-surface); cursor: pointer;
}
.bought-img img { width: 100%; height: 100%; object-fit: cover; }
.bought-img .img-placeholder {
  width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;
  color: var(--color-muted);
}
.bought-body { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: space-between; }
.bought-top { display: flex; align-items: flex-start; justify-content: space-between; gap: 8px; }
.bought-title { font-weight: 600; font-size: 1rem; color: var(--color-ink); cursor: pointer; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.bought-title:hover { color: var(--color-primary); }
.bought-price { font-weight: 700; font-size: 1.125rem; color: var(--color-primary); margin: 4px 0; }
.bought-meta { font-size: 0.8125rem; color: var(--color-muted); }
.bought-actions { display: flex; justify-content: flex-end; margin-top: 8px; }
.bought-btn {
  padding: 6px 18px; border: 1px solid var(--color-primary); border-radius: var(--radius-sm);
  background: var(--color-bg); color: var(--color-primary); font-size: 0.8125rem;
  font-family: var(--font-family); cursor: pointer; transition: all var(--duration-fast);
}
.bought-btn:hover { background: var(--color-primary); color: #fff; }
.bought-status { font-size: 0.75rem; font-weight: 500; padding: 2px 10px; border-radius: var(--radius-full); flex-shrink: 0; white-space: nowrap; }
.bs-1 { background: var(--color-warning-light); color: var(--color-warning); }
.bs-2 { background: var(--color-success-light); color: var(--color-success); }
.bs-3 { background: var(--color-surface); color: var(--color-muted); }

/* ── Account & Settings ── */
.account-block { margin-bottom: 8px; }
.account-block-title { font-size: 0.8125rem; font-weight: 600; color: var(--color-muted); padding: 16px 0 8px; }
.account-row {
  display: flex; align-items: center; padding: 14px 0;
  border-bottom: 1px solid var(--color-divider);
}
.account-row:last-child { border-bottom: none; }
.ac-label { width: 100px; flex-shrink: 0; font-size: 0.9375rem; color: var(--color-ink); }
.ac-val { flex: 1; font-size: 0.8125rem; color: var(--color-muted); margin-right: 12px; }
.ac-val.strong { font-weight: 600; color: var(--color-ink); font-size: 0.9375rem; }
.ac-status { font-size: 0.8125rem; flex-shrink: 0; }
.ac-status.success { color: var(--color-success); }
.ac-status.muted { color: var(--color-muted); }
.ac-link { font-size: 0.8125rem; color: var(--color-primary); cursor: pointer; flex-shrink: 0; white-space: nowrap; }
.ac-link:hover { text-decoration: underline; }
.ac-link.danger { color: var(--color-danger); }

.settings-section { margin-bottom: 24px; }
.settings-title { font-size: 0.875rem; font-weight: 600; color: var(--color-muted); padding: 12px 0 8px; border-bottom: 1px solid var(--color-divider); margin-bottom: 8px; }
.settings-row { display: flex; align-items: center; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid var(--color-divider); }
.settings-row:last-child { border-bottom: none; }
.settings-label { font-size: 0.9375rem; color: var(--color-ink); }
.settings-value { font-size: 0.875rem; color: var(--color-muted); display: flex; align-items: center; }
.settings-avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 1.125rem; font-weight: 700; }
.settings-avatar-lg { width: 72px; height: 72px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 2rem; font-weight: 700; flex-shrink: 0; }
.settings-input { border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 4px 10px; font-size: 0.875rem; font-family: var(--font-family); width: 180px; }
.settings-select { border: 1px solid var(--color-border); border-radius: var(--radius-sm); padding: 4px 8px; font-size: 0.875rem; font-family: var(--font-family); }
.settings-link { background: none; border: none; color: var(--color-primary); font-size: 0.8125rem; font-family: var(--font-family); cursor: pointer; margin-left: 12px; padding: 0; }
.settings-link:hover { text-decoration: underline; }
.settings-link.cancel { color: var(--color-muted); }
.settings-hint { color: var(--color-muted); font-size: 0.8125rem; }

@media (max-width: 768px) {
  .user-layout { grid-template-columns: 1fr; }
  .user-sidebar { position: static; margin-bottom: 16px; }
  .goods-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
