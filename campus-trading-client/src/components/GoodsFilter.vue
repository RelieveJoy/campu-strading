<template>
  <div class="goods-filter">
    <h6 class="filter-title">商品分类</h6>
    <div class="filter-group">
      <button
        class="filter-btn"
        :class="{ active: !activeCategory }"
        @click="$emit('update:category', null)"
      >全部分类</button>
      <button
        v-for="cat in categories"
        :key="cat.id"
        class="filter-btn"
        :class="{ active: activeCategory === String(cat.id) }"
        @click="$emit('update:category', String(cat.id))"
      >
        <i :class="cat.icon + ' me-1'"></i>{{ cat.name }}
      </button>
    </div>

    <hr />
    <h6 class="filter-title">成色</h6>
    <div class="filter-group">
      <button class="filter-btn" :class="{ active: !activeCondition }" @click="$emit('update:condition', null)">全部</button>
      <button
        v-for="c in conditions"
        :key="c.value"
        class="filter-btn"
        :class="{ active: activeCondition === c.value }"
        @click="$emit('update:condition', c.value)"
      >{{ c.label }}</button>
    </div>

    <hr />
    <h6 class="filter-title">价格区间</h6>
    <form @submit.prevent="applyPrice" class="price-form">
      <div class="price-inputs">
        <input type="number" v-model="minPrice" placeholder="最低" min="0" class="price-input" />
        <span class="price-sep">-</span>
        <input type="number" v-model="maxPrice" placeholder="最高" min="0" class="price-input" />
      </div>
      <button type="submit" class="price-apply">筛选</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  activeCategory: { type: String, default: null },
  activeCondition: { type: String, default: null },
  categories: {
    type: Array,
    default: () => [
      { id: 1,  name: '数码电子', icon: 'bi bi-phone' },
      { id: 2,  name: '书籍教材', icon: 'bi bi-book' },
      { id: 3,  name: '生活用品', icon: 'bi bi-house-heart' },
      { id: 4,  name: '服装鞋帽', icon: 'bi bi-bag' },
      { id: 5,  name: '运动健身', icon: 'bi bi-bicycle' },
      { id: 6,  name: '美妆护肤', icon: 'bi bi-stars' },
      { id: 7,  name: '游戏娱乐', icon: 'bi bi-controller' },
      { id: 8,  name: '其他闲置', icon: 'bi bi-grid' },
    ]
  },
  conditions: {
    type: Array,
    default: () => [
      { value: 'new', label: '全新' },
      { value: 'like_new', label: '九成新' },
      { value: 'good', label: '七成新' },
      { value: 'fair', label: '五成新' },
    ]
  }
})

const emit = defineEmits(['update:category', 'update:condition', 'filter-price'])

const minPrice = ref('')
const maxPrice = ref('')

function applyPrice() {
  emit('filter-price', { min: minPrice.value, max: maxPrice.value })
}
</script>

<style scoped>
.goods-filter {
  position: sticky;
  top: 72px;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 16px;
}
.filter-title {
  font-weight: 600;
  color: var(--color-ink);
  margin-bottom: 10px;
  font-size: 0.9375rem;
}
.filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.filter-btn {
  display: block;
  width: 100%;
  text-align: left;
  padding: 6px 12px;
  border: none;
  border-radius: var(--radius-sm);
  background: transparent;
  color: var(--color-muted);
  font-size: 0.8125rem;
  cursor: pointer;
  transition: background var(--duration-fast), color var(--duration-fast);
  font-family: var(--font-family);
}
.filter-btn:hover { background: var(--color-surface); color: var(--color-ink); }
.filter-btn.active {
  background: var(--color-primary);
  color: #fff;
}
hr {
  border: none;
  border-top: 1px solid var(--color-divider);
  margin: 14px 0;
}
.price-form {
  display: flex;
  gap: 6px;
  align-items: center;
}
.price-inputs {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}
.price-input {
  width: 0;
  flex: 1;
  padding: 5px 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  font-family: var(--font-family);
}
.price-sep { color: var(--color-muted); font-size: 0.75rem; }
.price-apply {
  padding: 5px 12px;
  border: 1px solid var(--color-primary);
  background: var(--color-bg);
  color: var(--color-primary);
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  cursor: pointer;
  white-space: nowrap;
  font-family: var(--font-family);
  transition: background var(--duration-fast), color var(--duration-fast);
}
.price-apply:hover { background: var(--color-primary); color: #fff; }
</style>
