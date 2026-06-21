<template>
  <div class="stats-page">
    <div class="page-header">
      <h1>数据统计</h1>
      <p class="page-subtitle">平台商品与交易数据概览</p>
    </div>

    <div class="stats-grid">
      <!-- Category Pie -->
      <div class="chart-card">
        <h3 class="chart-title">各分类商品数量</h3>
        <div ref="categoryChart" class="chart-body"></div>
        <div v-if="!hasCategoryData" class="chart-empty">暂无数据</div>
      </div>

      <!-- Price Range Bar -->
      <div class="chart-card">
        <h3 class="chart-title">价格区间分布</h3>
        <div ref="priceChart" class="chart-body"></div>
        <div v-if="!hasPriceData" class="chart-empty">暂无数据</div>
      </div>

      <!-- Trade Trend Line -->
      <div class="chart-card chart-wide">
        <h3 class="chart-title">近30天交易趋势</h3>
        <div ref="tradeChart" class="chart-body chart-tall"></div>
        <div v-if="!hasTradeData" class="chart-empty">暂无交易数据</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getCategoryStats, getPriceRangeStats, getTradeTrend } from '../api/stats'

const categoryChart = ref(null)
const priceChart = ref(null)
const tradeChart = ref(null)
const hasCategoryData = ref(true)
const hasPriceData = ref(true)
const hasTradeData = ref(true)

// ── Brand-aligned ECharts colors (深蓝 #204198 体系) ──
const BLUE = '#204198'
const BLUE_LIGHT = '#AEBAD9'
const BLUE_LIGHTER = '#d6dcf0'
const BLUE_DARK = '#142960'
const SUCCESS = '#198754'
const WARNING = '#ffc107'
const BLUE_ACCENT = '#5b8def'
const BRAND_COLORS = [
  BLUE, BLUE_LIGHT, SUCCESS, WARNING,
  '#6f42c1', // purple
  '#fd7e14', // orange
]
const TEXT_COLOR = '#212529'
const MUTED_COLOR = '#6c757d'
const BORDER_COLOR = '#dee2e6'

const chartTheme = {
  textStyle: { fontFamily: "'PingFang SC','Microsoft YaHei',system-ui,sans-serif" },
}

async function initPie() {
  const res = await getCategoryStats()
  const data = res.data || []
  if (!data.length) { hasCategoryData.value = false; return }

  const chart = echarts.init(categoryChart.value)
  chart.setOption({
    ...chartTheme,
    tooltip: { trigger: 'item', formatter: '{b}: {c} 件 ({d}%)' },
    color: BRAND_COLORS,
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['50%', '50%'],
      data: data.map((i) => ({ name: i.name || '未分类', value: i.value })),
      emphasis: {
        itemStyle: { shadowBlur: 8, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.1)' },
      },
      label: { color: TEXT_COLOR, fontSize: 12 },
    }],
  })
}

async function initBar() {
  const res = await getPriceRangeStats()
  const data = res.data || []
  if (!data.length) { hasPriceData.value = false; return }

  const chart = echarts.init(priceChart.value)
  chart.setOption({
    ...chartTheme,
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '8%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map((i) => i.name),
      axisLine: { lineStyle: { color: BORDER_COLOR } },
      axisTick: { show: false },
      axisLabel: { color: MUTED_COLOR, fontSize: 11 },
    },
    yAxis: {
      type: 'value',
      name: '件数',
      nameTextStyle: { color: MUTED_COLOR, fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e9ecef' } },
      axisLabel: { color: MUTED_COLOR, fontSize: 11 },
    },
    series: [{
      type: 'bar',
      data: data.map((i) => i.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: BLUE },
          { offset: 1, color: BLUE_LIGHT },
        ]),
        borderRadius: [6, 6, 0, 0],
      },
      barWidth: '50%',
      emphasis: { itemStyle: { color: BLUE } },
    }],
  })
}

async function initLine() {
  const res = await getTradeTrend()
  const data = res.data || []
  if (!data.length) { hasTradeData.value = false; return }

  const chart = echarts.init(tradeChart.value)
  chart.setOption({
    ...chartTheme,
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '10%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map((i) => i.name),
      axisLine: { lineStyle: { color: BORDER_COLOR } },
      axisTick: { show: false },
      axisLabel: { color: MUTED_COLOR, fontSize: 11, rotate: 45 },
      boundaryGap: false,
    },
    yAxis: {
      type: 'value',
      name: '订单数',
      nameTextStyle: { color: MUTED_COLOR, fontSize: 11 },
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e9ecef' } },
      axisLabel: { color: MUTED_COLOR, fontSize: 11 },
    },
    series: [{
      type: 'line',
      data: data.map((i) => i.value),
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: BLUE, width: 2.5 },
      itemStyle: { color: BLUE },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(32,65,152,0.18)' },
          { offset: 1, color: 'rgba(32,65,152,0.02)' },
        ]),
      },
    }],
  })
}

onMounted(async () => {
  await nextTick()
  initPie()
  initBar()
  initLine()
})
</script>

<style scoped>
.stats-page { max-width: 1100px; margin: 0 auto; padding: var(--space-lg); }

.page-header { margin-bottom: var(--space-lg); }
.page-header h1 { font-size: var(--font-display-size); font-weight: var(--font-display-weight); color: var(--color-ink); margin-bottom: var(--space-xs); }
.page-subtitle { font-size: var(--font-body-size); color: var(--color-muted); }

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-lg);
}
.chart-wide { grid-column: 1 / -1; }

.chart-card {
  background: var(--color-bg);
  border: 1px solid var(--color-divider);
  border-radius: var(--radius-md);
  padding: var(--space-lg);
  position: relative;
}
.chart-title {
  font-size: var(--font-title-size);
  font-weight: var(--font-title-weight);
  color: var(--color-ink);
  margin-bottom: var(--space-md);
}
.chart-body { width: 100%; height: 320px; }
.chart-tall { height: 360px; }

.chart-empty {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  color: var(--color-muted);
  font-size: var(--font-body-size);
  pointer-events: none;
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr; }
  .chart-wide { grid-column: auto; }
}
</style>
