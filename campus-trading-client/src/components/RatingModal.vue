<template>
  <teleport to="body">
    <transition name="modal-fade">
      <div class="rating-overlay" v-if="visible" @click.self="skip">
        <div class="rating-card">
          <h5 class="rating-title">评价商品</h5>
          <p class="rating-subtitle">{{ itemTitle }}</p>

          <!-- 星级选择 -->
          <div class="star-row">
            <span
              v-for="i in 5"
              :key="i"
              class="star-btn"
              :class="{ active: rating >= i }"
              @click="setRating(i)"
              @mouseenter="hoverStar = i"
              @mouseleave="hoverStar = 0"
            >
              <i :class="starIcon(i)"></i>
            </span>
            <span class="star-label">{{ starLabel }}</span>
          </div>

          <!-- 评语 -->
          <textarea
            v-model="content"
            class="rating-textarea"
            :placeholder="rating > 0 ? '写下你的评价（可选）...' : '选择跳过将不评分'"
            rows="3"
            maxlength="500"
          ></textarea>
          <span class="char-count">{{ content.length }}/500</span>

          <!-- 按钮 -->
          <div class="rating-actions">
            <button class="btn-skip" @click="skip">跳过不评</button>
            <button
              class="btn-submit"
              :disabled="submitting"
              @click="submit"
            >
              <span v-if="submitting" class="spinner"></span>
              提交评价
            </button>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { submitReview, updateReview } from '../api/review'

const props = defineProps({
  orderId: { type: Number, required: true },
  itemId: { type: Number, required: true },
  itemTitle: { type: String, default: '' },
  existingReview: { type: Object, default: null }, // 已有评价（修改模式）
})

const emit = defineEmits(['done'])
const visible = ref(false)
const rating = ref(0)
const content = ref('')
const hoverStar = ref(0)
const submitting = ref(false)

const starLabel = (() => {
  if (rating.value === 0) return '点击星星评分'
  const labels = ['', '非常差', '较差', '一般', '满意', '非常好']
  return labels[rating.value]
})()

function starIcon(i) {
  const active = hoverStar.value > 0 ? i <= hoverStar.value : i <= rating.value
  return active ? 'bi bi-star-fill' : 'bi bi-star'
}

function setRating(i) {
  rating.value = i
}

function open(existing) {
  if (existing) {
    rating.value = existing.rating || 0
    content.value = existing.content || ''
  } else {
    rating.value = 0
    content.value = ''
  }
  visible.value = true
}

function skip() {
  visible.value = false
  // rating=0 → 跳过不评
  if (rating.value === 0) {
    submitting.value = true
    submitReview({ orderId: props.orderId, itemId: props.itemId, rating: 0, content: '' })
      .then(() => emit('done'))
      .catch(() => {})
      .finally(() => { submitting.value = false; visible.value = false })
  } else {
    visible.value = false
  }
}

async function submit() {
  if (submitting.value) return
  submitting.value = true
  try {
    if (props.existingReview) {
      await updateReview(props.existingReview.reviewId, {
        orderId: props.orderId, itemId: props.itemId,
        rating: rating.value, content: content.value,
      })
    } else {
      await submitReview({
        orderId: props.orderId, itemId: props.itemId,
        rating: rating.value, content: content.value,
      })
    }
    ElMessage.success('评价已提交')
    emit('done')
    visible.value = false
  } catch { /* error handled by interceptor */ }
  finally { submitting.value = false }
}

defineExpose({ open })
</script>

<style scoped>
.rating-overlay {
  position: fixed; inset: 0; z-index: var(--z-modal);
  background: rgba(0,0,0,0.35); display: flex;
  align-items: center; justify-content: center;
  padding: var(--space-lg);
}
.rating-card {
  width: 100%; max-width: 420px; background: var(--color-bg);
  border-radius: var(--radius-lg); padding: 24px;
  box-shadow: var(--shadow-ambient-high);
}
.rating-title { font-size: 1.125rem; font-weight: 700; color: var(--color-ink); margin: 0 0 4px 0; }
.rating-subtitle { font-size: 0.875rem; color: var(--color-muted); margin: 0 0 18px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.star-row { display: flex; align-items: center; gap: 4px; margin-bottom: 16px; }
.star-btn { background: none; border: none; cursor: pointer; padding: 2px; font-size: 1.8rem; color: #ddd; transition: color var(--duration-fast), transform var(--duration-fast); }
.star-btn:hover { transform: scale(1.15); }
.star-btn.active, .star-btn:hover { color: #ffb800; }
.star-label { margin-left: 10px; font-size: 0.8125rem; color: var(--color-muted); min-width: 80px; }

.rating-textarea {
  width: 100%; padding: 10px 12px; border: 1px solid var(--color-border);
  border-radius: var(--radius-sm); font-size: 0.875rem; font-family: var(--font-family);
  resize: none; outline: none; background: var(--color-bg);
  transition: border-color var(--duration-fast);
}
.rating-textarea:focus { border-color: var(--color-primary); }
.char-count { font-size: 0.6875rem; color: var(--color-muted); display: block; text-align: right; margin-top: 2px; }

.rating-actions { display: flex; gap: 10px; margin-top: 16px; }
.btn-skip {
  flex: 1; padding: 10px 0; border: 1px solid var(--color-border);
  border-radius: var(--radius-md); background: var(--color-bg);
  color: var(--color-muted); font-size: 0.875rem; font-family: var(--font-family);
  cursor: pointer; transition: all var(--duration-fast);
}
.btn-skip:hover { border-color: var(--color-muted); color: var(--color-ink); }
.btn-submit {
  flex: 1.5; padding: 10px 0; border: none; border-radius: var(--radius-md);
  background: var(--color-primary); color: #fff; font-size: 0.875rem; font-weight: 600;
  font-family: var(--font-family); cursor: pointer;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  transition: background var(--duration-fast);
}
.btn-submit:hover:not(:disabled) { background: var(--color-primary-hover); }
.btn-submit:disabled { opacity: 0.6; cursor: not-allowed; }

.spinner {
  width: 14px; height: 14px; border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff; border-radius: 50%; animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
.modal-fade-enter-active { transition: opacity var(--duration-normal); }
.modal-fade-enter-active .rating-card { transition: transform var(--duration-normal) var(--ease-out); }
.modal-fade-leave-active { transition: opacity var(--duration-fast); }
.modal-fade-enter-from { opacity: 0; }
.modal-fade-enter-from .rating-card { transform: translateY(12px) scale(0.97); }
.modal-fade-leave-to { opacity: 0; }
</style>
