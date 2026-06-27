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
import { ref, watch, onUnmounted } from 'vue'

const props = defineProps({
  announcements: { type: Array, default: () => [] }
})

const current = ref(0)
let timer = null

function startTimer() {
  if (timer) clearInterval(timer)
  if (props.announcements.length > 1) {
    current.value = 0
    timer = setInterval(() => {
      current.value = (current.value + 1) % props.announcements.length
    }, 4000)
  }
}

watch(() => props.announcements, startTimer, { immediate: true })

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
  padding: 8px 16px;
  display: flex;
  align-items: center;
  color: #664d03;
  font-size: 0.8125rem;
}
.announcement-text {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.announcement-item {
  display: none;
}
.announcement-item.active {
  display: inline;
}
</style>
