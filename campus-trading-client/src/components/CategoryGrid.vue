<template>
  <div class="category-grid">
    <div class="category-row">
      <router-link
        v-for="cat in categories"
        :key="cat.id"
        :to="`/goods?category=${cat.id}`"
        class="category-item"
      >
        <div class="category-icon">
          <i :class="cat.icon"></i>
        </div>
        <small>{{ cat.name }}</small>
      </router-link>
    </div>
  </div>
</template>

<script setup>
defineProps({
  categories: {
    type: Array,
    default: () => [
      { id: 1,  name: '书籍教材', icon: 'bi bi-book' },
      { id: 2,  name: '数码电子', icon: 'bi bi-phone' },
      { id: 3,  name: '生活用品', icon: 'bi bi-house-heart' },
      { id: 4,  name: '服装鞋帽', icon: 'bi bi-bag' },
      { id: 5,  name: '运动健身', icon: 'bi bi-bicycle' },
      { id: 6,  name: '美妆护肤', icon: 'bi bi-stars' },
      { id: 7,  name: '游戏娱乐', icon: 'bi bi-controller' },
      { id: 8,  name: '其他闲置', icon: 'bi bi-grid' },
    ]
  }
})
</script>

<style scoped>
.category-grid {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 16px;
}
.category-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  padding: 20px 0;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: var(--color-ink);
  padding: 12px 16px;
  border-radius: var(--radius-md);
  transition: background var(--duration-fast);
  min-width: 72px;
}
.category-item:hover {
  background: var(--color-accent-light);
  color: var(--color-primary);
}
.category-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: var(--color-accent-light);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4px;
  transition: background var(--duration-fast);
}
.category-icon :deep(i) {
  font-size: 1.35rem;
  color: var(--color-primary);
}
.category-item:hover .category-icon {
  background: var(--color-primary-light);
}
.category-item small {
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--color-ink);
}
</style>
