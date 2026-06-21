<template>
  <div class="goods-card" @click="goDetail">
    <div class="goods-img-wrap">
      <img
        :src="goods.image || goods.images?.[0] || '/no-image.svg'"
        :alt="goods.title"
        @error="onImgError"
      />
      <span v-if="goods.condition" class="condition-badge" :class="conditionClass">
        {{ conditionText }}
      </span>
    </div>
    <div class="goods-body">
      <p class="goods-title" :title="goods.title">{{ goods.title }}</p>
      <div class="goods-meta">
        <span class="goods-price">¥{{ goods.price }}</span>
        <span class="goods-condition" :class="conditionClass">
          {{ conditionText }}
        </span>
      </div>
      <div class="goods-footer">
        <small class="goods-location">
          <i class="bi bi-geo-alt me-1"></i>{{ goods.location || '校内' }}
        </small>
        <small class="goods-views">
          <i class="bi bi-eye me-1"></i>{{ goods.views || 0 }}
        </small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  goods: { type: Object, required: true }
})

function goDetail() {
  const id = props.goods.id || props.goods.itemId
  if (id) router.push(`/item/${id}`)
}

const conditionMap = { 'new': '全新', 'like_new': '九成新', 'good': '七成新', 'fair': '五成新' }
const conditionText = computed(() => conditionMap[props.goods.condition] || props.goods.condition || '')
const conditionClass = computed(() => props.goods.condition || '')

function onImgError(e) {
  e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 200"><rect fill="%23f5f6f8" width="300" height="200"/><text x="150" y="100" text-anchor="middle" fill="%23adb5bd" font-size="14">暂无图片</text></svg>'
}
</script>

<style scoped>
.goods-card {
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow var(--duration-normal), transform var(--duration-normal);
  height: 100%;
  display: flex;
  flex-direction: column;
}
.goods-card:hover {
  box-shadow: var(--shadow-ambient-low);
  transform: translateY(-2px);
}
.goods-img-wrap {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: var(--color-surface);
}
.goods-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}
.goods-card:hover .goods-img-wrap img {
  transform: scale(1.05);
}
.condition-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 0.6875rem;
  font-weight: 500;
  background: rgba(255,255,255,0.9);
  color: var(--color-muted);
}
.condition-badge.new { color: var(--color-success); }
.condition-badge.like_new { color: #055160; }
.condition-badge.good { color: var(--color-primary); }
.goods-body {
  padding: 10px 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goods-title {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--color-ink);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.goods-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}
.goods-price {
  font-size: 1rem;
  font-weight: 700;
  color: var(--color-price);
}
.goods-condition {
  font-size: 0.6875rem;
  padding: 1px 6px;
  border-radius: var(--radius-sm);
  background: var(--color-surface); color: var(--color-muted);
}
.goods-condition.new { background: var(--color-success-light); color: var(--color-success); }
.goods-condition.like_new { background: #cff4fc; color: #055160; }
.goods-condition.good { background: var(--color-accent-light); color: var(--color-primary); }
.goods-condition.fair { background: var(--color-surface); color: var(--color-muted); }
.goods-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}
.goods-footer small {
  color: var(--color-muted);
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 75%;
}
</style>
