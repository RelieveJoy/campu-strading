<template>
  <div class="banner-carousel" v-if="banners.length">
    <div class="carousel-track" ref="trackRef">
      <div
        v-for="(b, i) in banners"
        :key="i"
        class="carousel-slide"
        :class="{ active: i === current }"
        :style="{ backgroundImage: `url(${b.image})` }"
        @click="b.link && $router?.push(b.link)"
      >
        <img :src="b.image" :alt="b.title || ''" />
        <div class="carousel-caption" v-if="b.title">
          <h5>{{ b.title }}</h5>
        </div>
      </div>
    </div>
    <!-- Indicators -->
    <div class="carousel-indicators" v-if="banners.length > 1">
      <button
        v-for="(b, i) in banners"
        :key="i"
        :class="{ active: i === current }"
        @click="goTo(i)"
      ></button>
    </div>
    <!-- Controls -->
    <button v-if="banners.length > 1" class="carousel-control prev" @click="prev">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="32" height="32"><polyline points="15 18 9 12 15 6"/></svg>
    </button>
    <button v-if="banners.length > 1" class="carousel-control next" @click="next">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="32" height="32"><polyline points="9 18 15 12 9 6"/></svg>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  banners: { type: Array, default: () => [] }
})

const current = ref(0)
let timer = null

function goTo(i) { current.value = i; resetTimer() }
function prev() { current.value = (current.value - 1 + props.banners.length) % props.banners.length; resetTimer() }
function next() { current.value = (current.value + 1) % props.banners.length; resetTimer() }
function resetTimer() {
  if (timer) clearInterval(timer)
  timer = setInterval(() => next(), 4000)
}

onMounted(() => { if (props.banners.length > 1) timer = setInterval(() => next(), 4000) })
onUnmounted(() => { if (timer) clearInterval(timer) })
</script>

<style scoped>
.banner-carousel {
  position: relative;
  max-height: 400px;
  overflow: hidden;
  background: var(--color-surface);
}
.carousel-track { position: relative; width: 100%; height: 400px; }
.carousel-slide {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 0.5s ease;
  cursor: pointer;
}
.carousel-slide.active { opacity: 1; }
.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.carousel-caption {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  color: #fff;
  text-shadow: 0 1px 6px rgba(0,0,0,0.4);
}
.carousel-caption h5 { font-size: 1.25rem; font-weight: 600; }
.carousel-indicators {
  position: absolute;
  bottom: 1rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}
.carousel-indicators button {
  width: 10px; height: 10px;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.7);
  background: transparent;
  cursor: pointer;
  padding: 0;
  transition: background var(--duration-fast);
}
.carousel-indicators button.active { background: #fff; border-color: #fff; }
.carousel-control {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.75);
  border: none;
  border-radius: 50%;
  width: 40px; height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #333;
  opacity: 0;
  transition: opacity var(--duration-fast);
}
.carousel-control.prev { left: 1rem; }
.carousel-control.next { right: 1rem; }
.banner-carousel:hover .carousel-control { opacity: 1; }

@media (max-width: 768px) {
  .carousel-track { height: 220px; }
  .carousel-control { width: 32px; height: 32px; opacity: 1; }
}
</style>
