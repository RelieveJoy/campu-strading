<template>
  <div class="lostfound-page">
    <div class="container">
      <!-- 头部 -->
      <div class="page-header">
        <div>
          <h4 class="page-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="22" height="22" class="title-icon"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/><line x1="8" y1="11" x2="14" y2="11"/></svg>
            失物招领
          </h4>
          <p class="page-desc">浏览已审核的招领信息，若是失主可直接联系拾取者核对认领。</p>
        </div>
        <button class="btn-publish" @click="showPublish = true">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          发布招领
        </button>
      </div>

      <!-- 搜索筛选 -->
      <div class="filter-card">
        <form class="filter-form" @submit.prevent="fetchItems">
          <div class="filter-row">
            <div class="filter-field">
              <label class="filter-label">关键词</label>
              <input v-model="keyword" type="text" class="filter-input" placeholder="搜索物品名称、地点、特征描述" />
            </div>
            <div class="filter-field">
              <label class="filter-label">类别</label>
              <select v-model="categoryFilter" class="filter-select">
                <option value="">全部</option>
                <option value="lost">寻物</option>
                <option value="found">招领</option>
              </select>
            </div>
            <button type="submit" class="filter-btn">筛选</button>
          </div>
        </form>
      </div>

      <!-- 结果统计 -->
      <p class="result-info">共 {{ total }} 条招领信息</p>

      <!-- Loading -->
      <div v-if="loading" class="lf-grid">
        <div v-for="n in 4" :key="n" class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-lines">
            <div class="skeleton-line w-80"></div>
            <div class="skeleton-line w-40"></div>
          </div>
        </div>
      </div>

      <!-- Empty -->
      <div v-else-if="items.length === 0" class="empty-state">
        <p>暂无招领信息</p>
      </div>

      <!-- Grid -->
      <div v-else class="lf-grid">
        <router-link
          v-for="item in items"
          :key="item.id"
          :to="`/lostfound/${item.id}`"
          class="lf-card"
        >
          <img :src="item.image" :alt="item.title" @error="onImgError" />
          <div class="lf-card-body">
            <div class="lf-card-header">
              <h5>{{ item.title }}</h5>
              <span class="lf-cat" :class="item.category">{{ item.category === 'lost' ? '寻物' : '招领' }}</span>
            </div>
            <p class="lf-loc" v-if="item.location">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="14" height="14"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
              {{ item.location }}
            </p>
            <p class="lf-date" v-if="item.createTime">{{ item.createTime }}</p>
          </div>
        </router-link>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > pageSize && !loading">
        <el-pagination
          :current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="onPage"
          background
        />
      </div>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog v-model="showPublish" title="发布失物招领" width="500px" :close-on-click-modal="false">
      <el-form :model="pubForm" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="pubForm.title" placeholder="物品名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="类别" required>
          <el-radio-group v-model="pubForm.category">
            <el-radio value="lost">寻物</el-radio>
            <el-radio value="found">招领</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="pubForm.location" placeholder="在哪见到的" maxlength="100" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="pubForm.description" type="textarea" :rows="3" placeholder="物品特征等" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="pubForm.contact" placeholder="手机号或微信" maxlength="50" />
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-wrap">
            <input type="file" ref="fileInput" accept="image/*" @change="onFileChange" style="display:none" />
            <el-button @click="$refs.fileInput.click()" :loading="uploading">
              {{ uploading ? '上传中...' : '选择图片' }}
            </el-button>
            <img v-if="pubForm.imageUrl" :src="pubForm.imageUrl" class="upload-preview" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPublish = false">取消</el-button>
        <el-button type="primary" @click="handlePublish" :loading="publishing">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getLostFounds, publishLostFound } from '../api/lostfound'
import { uploadFile } from '../api/item'

const keyword = ref('')
const categoryFilter = ref('')       // '' | 'lost' | 'found'
const page = ref(1)
const pageSize = 8

const items = ref([])
const total = ref(0)
const loading = ref(true)

async function fetchItems() {
  loading.value = true
  try {
    const params = { page: page.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (categoryFilter.value) params.category = categoryFilter.value
    const res = await getLostFounds(params)
    items.value = (res?.data?.records || res?.data || []).map(normalize)
    total.value = res?.data?.total || items.value.length
  } catch { items.value = []; total.value = 0 }
  finally { loading.value = false }
}

function normalize(item) {
  return {
    ...item,
    id: item.lostFoundId,
    image: item.imageUrl,
  }
}

function onPage(p) { page.value = p; fetchItems(); window.scrollTo({ top: 0, behavior: 'smooth' }) }

function onImgError(e) {
  e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 200"><rect fill="%23f5f6f8" width="300" height="200"/><text x="150" y="100" text-anchor="middle" fill="%23adb5bd" font-size="14">暂无图片</text></svg>'
}

const showPublish = ref(false)
const publishing = ref(false)
const uploading = ref(false)
const pubForm = reactive({ title: '', category: 'found', location: '', description: '', contact: '', imageUrl: '' })

async function onFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  try {
    const res = await uploadFile(file)
    pubForm.imageUrl = res.data || ''
    ElMessage.success('上传成功')
  } catch { /* handled by interceptor */ }
  finally { uploading.value = false }
}

async function handlePublish() {
  if (!pubForm.title || !pubForm.category) {
    ElMessage.warning('请填写标题和类别')
    return
  }
  publishing.value = true
  try {
    await publishLostFound({ ...pubForm })
    ElMessage.success('发布成功')
    showPublish.value = false
    pubForm.title = ''; pubForm.description = ''; pubForm.contact = ''; pubForm.imageUrl = ''
    fetchItems()
  } catch { /* interceptor handles */ }
  finally { publishing.value = false }
}

onMounted(fetchItems)
</script>

<style scoped>
.lostfound-page { padding: var(--space-lg) 0; }
.container { max-width: var(--content-max-width); margin: 0 auto; padding: 0 16px; }

/* ── Header ── */
.page-header {
  display: flex; flex-wrap: wrap; gap: 16px;
  justify-content: space-between; align-items: flex-start;
  margin-bottom: var(--space-lg);
}
.page-title {
  display: flex; align-items: center; gap: 8px;
  font-size: 1.25rem; font-weight: 700; color: var(--color-ink); margin: 0 0 4px 0;
}
.title-icon { color: var(--color-primary); }
.page-desc { color: var(--color-muted); font-size: 0.8125rem; margin: 0; }
.btn-publish {
  display: flex; align-items: center; gap: 6px;
  padding: 8px 18px; background: var(--color-primary); color: #fff;
  border-radius: var(--radius-md); text-decoration: none;
  font-size: 0.875rem; font-weight: 500; white-space: nowrap;
  transition: background var(--duration-fast);
}
.btn-publish:hover { background: var(--color-primary-hover); }

/* ── Filter ── */
.filter-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); padding: 16px; margin-bottom: var(--space-md);
}
.filter-row { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; }
.filter-field { flex: 1; min-width: 160px; }
.filter-label { display: block; font-size: 0.8125rem; font-weight: 500; color: var(--color-ink); margin-bottom: 4px; }
.filter-input, .filter-select {
  width: 100%; padding: 8px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-sm); font-size: 0.8125rem; font-family: var(--font-family);
  color: var(--color-ink); background: var(--color-bg); outline: none;
  transition: border-color var(--duration-fast);
}
.filter-input:focus, .filter-select:focus { border-color: var(--color-primary); box-shadow: var(--focus-ring); }
.filter-btn {
  padding: 8px 20px; background: var(--color-bg); color: var(--color-primary);
  border: 1px solid var(--color-primary); border-radius: var(--radius-sm);
  font-size: 0.8125rem; cursor: pointer; font-family: var(--font-family);
  transition: all var(--duration-fast);
}
.filter-btn:hover { background: var(--color-primary); color: #fff; }

.result-info { font-size: 0.8125rem; color: var(--color-muted); margin-bottom: var(--space-md); }

/* ── Grid ── */
.lf-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: var(--space-lg); }
.lf-card {
  background: var(--color-bg); border: 1px solid var(--color-border);
  border-radius: var(--radius-md); overflow: hidden;
  text-decoration: none; color: var(--color-ink);
  transition: box-shadow var(--duration-normal);
}
.lf-card:hover { box-shadow: var(--shadow-ambient-low); }
.lf-card img {
  width: 100%; height: 200px; object-fit: cover;
  background: var(--color-surface);
}
.lf-card-body { padding: 14px; }
.lf-card-header {
  display: flex; justify-content: space-between; align-items: flex-start;
  gap: 8px; margin-bottom: 8px;
}
.lf-card-header h5 { font-size: 0.9375rem; font-weight: 600; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; }
.lf-cat { font-size: 0.6875rem; padding: 2px 8px; border-radius: var(--radius-sm); white-space: nowrap; flex-shrink: 0; }
.lf-cat.lost { background: var(--color-warning-light); color: var(--color-warning); }
.lf-cat.found { background: var(--color-primary-light, #e8edf8); color: var(--color-primary); }
.lf-status { font-size: 0.6875rem; padding: 2px 8px; border-radius: var(--radius-sm); white-space: nowrap; flex-shrink: 0; }
.lf-status.active { background: var(--color-success-light); color: var(--color-success); }
.lf-status.closed { background: var(--color-surface); color: var(--color-muted); }
.lf-loc {
  display: flex; align-items: center; gap: 4px;
  color: var(--color-muted); font-size: 0.8125rem; margin: 0 0 4px 0;
}
.lf-date { color: var(--color-muted); font-size: 0.75rem; margin: 0; }

/* ── Skeleton ── */
.skeleton-card { border: 1px solid var(--color-divider); border-radius: var(--radius-md); overflow: hidden; }
.skeleton-img { height: 200px; background: var(--color-surface); animation: shimmer 1.5s infinite; }
.skeleton-lines { padding: 14px; }
.skeleton-line { height: 12px; background: var(--color-surface); border-radius: var(--radius-sm); margin-bottom: 8px; animation: shimmer 1.5s infinite; }
.skeleton-line.w-80 { width: 80%; }
.skeleton-line.w-40 { width: 40%; }
@keyframes shimmer { 0% { opacity: 1; } 50% { opacity: 0.5; } 100% { opacity: 1; } }

/* ── Empty ── */
.empty-state { text-align: center; padding: var(--space-xxl) var(--space-lg); color: var(--color-muted); }

.pagination-wrap { margin-top: var(--space-xl); display: flex; justify-content: center; }

.upload-wrap { display: flex; align-items: center; gap: 12px; }
.upload-preview { width: 80px; height: 60px; object-fit: cover; border-radius: var(--radius-sm); border: 1px solid var(--color-divider); }

@media (max-width: 640px) {
  .lf-grid { grid-template-columns: 1fr; }
  .filter-row { flex-direction: column; }
}
</style>
