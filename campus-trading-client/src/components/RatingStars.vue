<template>
  <span class="rating-stars" :class="{ interactive: mode === 'interactive' }">
    <i
      v-for="i in 5"
      :key="i"
      class="star"
      :class="starClass(i)"
      @click="onClick(i)"
      @mouseenter="hovered = i"
      @mouseleave="hovered = 0"
    ></i>
    <span v-if="showCount && reviewCount > 0" class="rating-count">
      ({{ reviewCount }})
    </span>
    <span v-else-if="showCount && reviewCount === 0" class="rating-none">
      暂无评分
    </span>
  </span>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: 0 },  // 当前评分 0~5
  averageRating: { type: Number, default: 0 }, // 只读模式的均分
  reviewCount: { type: Number, default: 0 },
  showCount: { type: Boolean, default: true },
  mode: { type: String, default: 'readonly' }, // 'interactive' | 'readonly'
})

const emit = defineEmits(['update:modelValue'])
const hovered = ref(0)

function effectiveRating() {
  if (props.mode === 'interactive') {
    if (hovered.value > 0) return hovered.value
    return props.modelValue
  }
  return Math.round(props.averageRating)
}

function starClass(i) {
  const r = effectiveRating()
  if (i <= r) {
    if (r >= 4) return 'bi bi-star-fill active high'
    if (r >= 3) return 'bi bi-star-fill active'
    return 'bi bi-star-fill active low'
  }
  if (props.mode === 'interactive' && hovered.value > 0 && i <= hovered.value) {
    return 'bi bi-star-fill hover'
  }
  return 'bi bi-star'
}

function onClick(i) {
  if (props.mode !== 'interactive') return
  // 点同一颗星取消评分（设为0）
  emit('update:modelValue', props.modelValue === i ? 0 : i)
}
</script>

<style scoped>
.rating-stars { display: inline-flex; align-items: center; gap: 2px; }
.star { font-size: 1rem; color: #ddd; transition: color var(--duration-fast); }
.interactive .star { cursor: pointer; font-size: 1.35rem; }
.star.active { color: #ffb800; }
.star.active.high { color: #f59e0b; }
.star.active.low { color: #d97706; }
.star.hover, .interactive .star:hover { color: #fcd34d; }
.rating-count { font-size: 0.8125rem; color: var(--color-muted); margin-left: 6px; }
.rating-none { font-size: 0.75rem; color: var(--color-muted); margin-left: 4px; }
</style>
