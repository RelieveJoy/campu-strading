<template>
  <div class="stats-container">
    <div class="header">
      <h2>数据统计</h2>
    </div>
    <div class="charts">
      <div class="chart-card">
        <h3>各分类商品数量</h3>
        <div ref="categoryChart" style="width:100%;height:350px"></div>
      </div>
      <div class="chart-card">
        <h3>价格区间分布</h3>
        <div ref="priceChart" style="width:100%;height:350px"></div>
      </div>
      <div class="chart-card chart-wide">
        <h3>近30天交易趋势</h3>
        <div ref="tradeChart" style="width:100%;height:350px"></div>
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

const initPie = async () => {
  const res = await getCategoryStats()
  const chart = echarts.init(categoryChart.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: res.data.map(i => ({ name: i.name || '未分类', value: i.value })),
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  })
}

const initBar = async () => {
  const res = await getPriceRangeStats()
  const chart = echarts.init(priceChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.map(i => i.name) },
    yAxis: { type: 'value', name: '商品数量' },
    series: [{ type: 'bar', data: res.data.map(i => i.value), itemStyle: { color: '#409eff' } }]
  })
}

const initLine = async () => {
  const res = await getTradeTrend()
  const chart = echarts.init(tradeChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.map(i => i.name), axisLabel: { rotate: 45 } },
    yAxis: { type: 'value', name: '订单数' },
    series: [{ type: 'line', data: res.data.map(i => i.value), smooth: true, itemStyle: { color: '#67c23a' } }]
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
.stats-container { max-width:1200px; margin:0 auto; padding:20px; }
.header { display:flex; align-items:center; gap:16px; margin-bottom:24px; }
.header h2 { margin:0; }
.charts { display:grid; grid-template-columns:1fr 1fr; gap:24px; }
.chart-wide { grid-column:1 / -1; }
.chart-card { background:#fff; padding:20px; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.06); }
.chart-card h3 { margin-bottom:16px; font-size:16px; color:#333; }
</style>
