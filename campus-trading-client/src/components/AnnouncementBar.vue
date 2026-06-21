<template>
  <div class="announcement-bar" v-if="announcements.length">
    <div class="announcement-inner">
      <i class="bi bi-megaphone me-2"></i>
      <div class="announcement-text" ref="textRef">
        <span v-for="(a, i) in announcements" :key="i" class="announcement-item" :class="{ active: i === current }">
          <strong>{{ a.title }}</strong>：{{ a.content }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  announcements: { type: Array, default: () => [] }
})

const current = ref(0)
let timer = null

onMounted(() => {
  if (props.announcements.length > 1) {
    timer = setInterval(() => {
      current.value = (current.value + 1) % props.announcements.length
    }, 4000)
  }
})

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.announcement-bar {
  background: var(--color-warning-light);
  border-bottom: 1px solid #ffe5a3;
  overflow: hidden;
}
.announcement-inner {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 6px 16px;
  display: flex;
  align-items: center;
  color: #664d03;
  font-size: 0.8125rem;
}
.announcement-text {
  position: relative;
  flex: 1;
  min-height: 1.4em;
}
.announcement-item {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.5s ease;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.announcement-item.active {
  opacity: 1;
}
</style>
