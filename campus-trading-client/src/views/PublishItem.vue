<template>
  <div class="publish-page">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑商品' : '发布商品' }}</h1>
      <p class="page-subtitle">{{ isEdit ? '修改商品信息后保存' : '填写商品信息，让更多人看到你的闲置好物' }}</p>
    </div>

    <div class="form-card">
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>

        <div class="field">
          <label class="field-label" for="item-title">商品标题 <span class="required">*</span></label>
          <input id="item-title" v-model="form.title" type="text" class="field-input"
            placeholder="例如：九成新高等数学教材上下册" maxlength="50" />
          <span class="field-hint">{{ form.title.length }}/50</span>
          <span class="field-error" v-if="errors.title">{{ errors.title }}</span>
        </div>

        <div class="field">
          <label class="field-label" for="item-desc">商品描述</label>
          <textarea id="item-desc" v-model="form.description" class="field-textarea"
            placeholder="描述商品的成色、使用时长、购买渠道等信息..." rows="4"></textarea>
        </div>
      </div>

      <div class="form-section">
        <h3 class="section-title">价格与分类</h3>

        <div class="field-row">
          <div class="field">
            <label class="field-label" for="item-price">售价 <span class="required">*</span></label>
            <div class="price-input-wrap">
              <span class="price-prefix">¥</span>
              <input id="item-price" v-model.number="form.price" type="number"
                class="field-input has-prefix" placeholder="0.00" min="0" step="0.01" />
            </div>
            <span class="field-error" v-if="errors.price">{{ errors.price }}</span>
          </div>
          <div class="field">
            <label class="field-label" for="item-original">原价</label>
            <div class="price-input-wrap">
              <span class="price-prefix">¥</span>
              <input id="item-original" v-model.number="form.originalPrice" type="number"
                class="field-input has-prefix" placeholder="选填" min="0" step="0.01" />
            </div>
          </div>
        </div>

        <div class="field">
          <label class="field-label">分类 <span class="required">*</span></label>
          <div class="category-chips">
            <button v-for="c in categories" :key="c.value" type="button"
              class="cat-chip" :class="{ active: form.categoryId === c.value }"
              @click="form.categoryId = c.value">
              {{ c.label }}
            </button>
          </div>
          <span class="field-error" v-if="errors.categoryId">{{ errors.categoryId }}</span>
        </div>

        <div class="field">
          <label class="field-label">成色</label>
          <div class="category-chips">
            <button v-for="c in conditionList" :key="c.value" type="button"
              class="cat-chip" :class="{ active: form.itemCondition === c.value }"
              @click="form.itemCondition = c.value">
              {{ c.label }}
            </button>
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3 class="section-title">商品图片</h3>

        <div class="upload-area" v-if="!form.imageUrl">
          <label class="upload-label">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="upload-icon">
              <rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/>
              <path d="m21 15-5-5L5 21"/>
            </svg>
            <span>点击选择图片</span>
            <span class="upload-hint">支持 JPG、PNG，建议尺寸 800×600</span>
            <input type="file" accept="image/*" @change="handleUpload" hidden />
          </label>
        </div>

        <div class="upload-preview" v-else>
          <img :src="form.imageUrl" alt="商品图片预览" />
          <button type="button" class="upload-change" @click="form.imageUrl = ''">更换图片</button>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" class="btn-cancel" @click="$router.back()">取消</button>
        <button type="button" class="btn-submit" @click="submit" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          {{ isEdit ? '保存修改' : '发布商品' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addItem, updateItem, getItemDetail, uploadFile } from '../api/item'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const isEdit = ref(route.name === 'EditItem')

const categories = [
  { label: '书籍教材', value: 1 },
  { label: '数码电子', value: 2 },
  { label: '生活用品', value: 3 },
  { label: '服装鞋帽', value: 4 },
  { label: '运动健身', value: 5 },
  { label: '美妆护肤', value: 6 },
  { label: '游戏娱乐', value: 7 },
  { label: '其他闲置', value: 8 },
]

const conditionList = [
  { label: '全新', value: 1 },
  { label: '九成新', value: 2 },
  { label: '七成新', value: 3 },
  { label: '五成新', value: 4 },
]

const form = reactive({
  title: '', description: '', price: null, originalPrice: null, categoryId: null, itemCondition: null, imageUrl: ''
})

const errors = reactive({ title: '', price: '', categoryId: '' })

function clearErrors() { Object.keys(errors).forEach(k => errors[k] = '') }

function validate() {
  clearErrors()
  let ok = true
  if (!form.title.trim()) { errors.title = '请输入商品标题'; ok = false }
  if (form.price == null || form.price <= 0) { errors.price = '请输入有效售价'; ok = false }
  if (form.categoryId == null) { errors.categoryId = '请选择分类'; ok = false }
  return ok
}

async function handleUpload(e) {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    form.imageUrl = res.data
    ElMessage.success('图片上传成功')
  } catch (e) { ElMessage.error('上传失败') }
}

async function submit() {
  if (!validate()) return
  loading.value = true
  try {
    if (isEdit.value) {
      await updateItem(route.params.id, form)
      ElMessage.success('修改成功')
    } else {
      await addItem(form)
      ElMessage.success('发布成功')
    }
    router.push('/my-items')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

async function initPage() {
  if (!loggedIn.value) { requireLogin(); return }
  if (isEdit.value) {
    try {
      const res = await getItemDetail(route.params.id)
      Object.assign(form, {
        title: res.data.title || '',
        description: res.data.description || '',
        price: res.data.price,
        originalPrice: res.data.originalPrice,
        categoryId: res.data.categoryId,
        itemCondition: res.data.itemCondition || null,
        imageUrl: res.data.imageUrl || '',
      })
    } catch (e) { ElMessage.error('商品不存在'); router.push('/') }
  }
}

watch(loggedIn, (val) => { if (val) initPage() })
onMounted(initPage)
</script>

<style scoped>
.publish-page { max-width: 800px; margin: 0 auto; padding: var(--space-lg);
  max-width: 680px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--space-lg);
}

.page-header h1 {
  font-size: var(--font-display-size);
  font-weight: var(--font-display-weight);
  color: var(--color-ink);
  margin-bottom: var(--space-xs);
}

.page-subtitle {
  font-size: var(--font-body-size);
  color: var(--color-muted);
}

/* ── Form Card ── */
.form-card {
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
}

.form-section {
  margin-bottom: var(--space-xl);
  padding-bottom: var(--space-xl);
  border-bottom: 1px solid var(--color-divider);
}

.form-section:last-of-type {
  margin-bottom: var(--space-lg);
  padding-bottom: 0;
  border-bottom: none;
}

.section-title {
  font-size: var(--font-title-size);
  font-weight: var(--font-title-weight);
  color: var(--color-ink);
  margin-bottom: var(--space-md);
}

/* ── Fields ── */
.field {
  margin-bottom: var(--space-md);
}

.field:last-child { margin-bottom: 0; }

.field-label {
  display: block;
  font-size: var(--font-label-size);
  font-weight: 500;
  color: var(--color-ink);
  margin-bottom: 4px;
  letter-spacing: 0.02em;
}

.required { color: var(--color-danger); }

.field-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-body-size);
  color: var(--color-ink);
  background: var(--color-bg);
  font-family: var(--font-family);
  outline: none;
  transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}

.field-input::placeholder { color: oklch(0.65 0.005 28); }
.field-input:focus { border-color: var(--color-primary); box-shadow: var(--focus-ring); }

.field-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: var(--font-body-size);
  color: var(--color-ink);
  background: var(--color-bg);
  font-family: var(--font-family);
  outline: none;
  resize: vertical;
  transition: border-color var(--duration-fast), box-shadow var(--duration-fast);
}

.field-textarea::placeholder { color: oklch(0.65 0.005 28); }
.field-textarea:focus { border-color: var(--color-primary); box-shadow: var(--focus-ring); }

.field-hint {
  font-size: 0.6875rem;
  color: var(--color-muted);
}

.field-error {
  font-size: 0.75rem;
  color: var(--color-danger);
  display: block;
  margin-top: 2px;
}

/* ── Field row ── */
.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
}

.price-input-wrap {
  position: relative;
}

.price-prefix {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-muted);
  font-size: var(--font-body-size);
  font-weight: 500;
}

.field-input.has-prefix {
  padding-left: 30px;
}

/* Chrome number input hide spinners */
.field-input[type=number]::-webkit-outer-spin-button,
.field-input[type=number]::-webkit-inner-spin-button { -webkit-appearance: none; margin: 0; }
.field-input[type=number] { -moz-appearance: textfield; }

/* ── Category Chips ── */
.category-chips {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-sm);
}

.cat-chip {
  padding: 8px 20px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  background: var(--color-bg);
  color: var(--color-ink);
  font-size: var(--font-body-size);
  font-family: var(--font-family);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-standard);
}

.cat-chip:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.cat-chip.active {
  background: var(--color-primary);
  color: var(--color-primary-text);
  border-color: var(--color-primary);
}

/* ── Upload ── */
.upload-area {
  border: 2px dashed var(--color-border);
  border-radius: var(--radius-md);
  padding: var(--space-xl);
  text-align: center;
  transition: border-color var(--duration-fast), background var(--duration-fast);
}

.upload-area:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-sm);
  cursor: pointer;
  color: var(--color-muted);
  font-size: var(--font-body-size);
}

.upload-icon {
  width: 40px; height: 40px;
  color: var(--color-primary);
}

.upload-hint {
  font-size: var(--font-label-size);
  color: var(--color-muted);
}

.upload-preview {
  position: relative;
  display: inline-block;
}

.upload-preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.upload-change {
  display: block;
  margin-top: var(--space-sm);
  padding: 6px 16px;
  background: var(--color-bg);
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm);
  font-size: var(--font-label-size);
  font-family: var(--font-family);
  cursor: pointer;
  transition: all var(--duration-fast);
}

.upload-change:hover {
  background: var(--color-primary);
  color: var(--color-primary-text);
}

/* ── Actions ── */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-md);
  padding-top: var(--space-md);
}

.btn-cancel {
  padding: 10px 24px;
  background: var(--color-surface);
  color: var(--color-ink);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 500;
  font-family: var(--font-family);
  cursor: pointer;
  transition: background var(--duration-fast);
}

.btn-cancel:hover { background: var(--color-divider); }

.btn-submit {
  padding: 10px 28px;
  background: var(--color-primary);
  color: var(--color-primary-text);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-body-size);
  font-weight: 600;
  font-family: var(--font-family);
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-standard);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.btn-submit:hover:not(:disabled) { background: var(--color-primary-hover); }
.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }

.spinner {
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 640px) {
  .form-card { padding: var(--space-md); }
  .field-row { grid-template-columns: 1fr; }
}
</style>
