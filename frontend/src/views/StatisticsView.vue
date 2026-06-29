<template>
  <div class="page statistics-page">
    <div class="page-heading">
      <h1 class="page-title">数据统计</h1>
      <el-button :icon="Refresh" :loading="loading" @click="load">刷新</el-button>
    </div>

    <div class="metric-grid">
      <div class="metric">
        <div class="metric-label">客户总数</div>
        <div class="metric-value">{{ overview.customerCount }}</div>
      </div>
      <div class="metric">
        <div class="metric-label">今日预约</div>
        <div class="metric-value">{{ overview.todayAppointmentCount }}</div>
      </div>
      <div class="metric">
        <div class="metric-label">今日订单</div>
        <div class="metric-value">{{ overview.todayOrderCount }}</div>
      </div>
      <div class="metric">
        <div class="metric-label">今日成交额</div>
        <div class="metric-value">¥{{ overview.todayAmount || 0 }}</div>
      </div>
    </div>

    <div class="analytics-grid">
      <div class="panel chart-panel chart-panel-wide">
        <div class="panel-title">
          <div>
            <strong>近 7 天订单金额</strong>
            <span>合计 ¥{{ totalAmount }}</span>
          </div>
          <div class="panel-stat">峰值 ¥{{ peakAmount }}</div>
        </div>
        <div ref="amountChartRef" class="chart-box chart-box-tall"></div>
      </div>

      <div class="panel chart-panel">
        <div class="panel-title">
          <div>
            <strong>今日转化</strong>
            <span>预约到订单</span>
          </div>
          <div class="panel-stat">{{ conversionRate }}%</div>
        </div>
        <div ref="todayChartRef" class="chart-box chart-box-compact"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api/modules'

type EChartsInstance = ReturnType<typeof echarts.init>

const amountChartRef = ref<HTMLDivElement>()
const todayChartRef = ref<HTMLDivElement>()
const loading = ref(false)
const dateList = ref<string[]>([])
const amountList = ref<number[]>([])
let amountChart: EChartsInstance | undefined
let todayChart: EChartsInstance | undefined

const overview = reactive({
  customerCount: 0,
  todayAppointmentCount: 0,
  todayOrderCount: 0,
  todayAmount: 0
})

const totalAmount = computed(() => amountList.value.reduce((sum, item) => sum + Number(item || 0), 0).toFixed(2))
const peakAmount = computed(() => Math.max(0, ...amountList.value.map((item) => Number(item || 0))).toFixed(2))
const conversionRate = computed(() => {
  if (!overview.todayAppointmentCount) return 0
  return Math.round((overview.todayOrderCount / overview.todayAppointmentCount) * 100)
})

const chartTextColor = '#334155'
const axisTextColor = '#64748b'

const renderAmountChart = () => {
  if (!amountChartRef.value) return
  amountChart ||= echarts.init(amountChartRef.value)
  amountChart.setOption({
    color: ['#0f766e', '#d97706'],
    grid: { left: 54, right: 28, top: 34, bottom: 42 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#111827',
      borderWidth: 0,
      textStyle: { color: '#fff' },
      valueFormatter: (value: unknown) => `¥${Number(value || 0).toFixed(2)}`
    },
    xAxis: {
      type: 'category',
      data: dateList.value,
      axisLine: { lineStyle: { color: '#d9dee8' } },
      axisTick: { show: false },
      axisLabel: { color: axisTextColor }
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: axisTextColor, formatter: '¥{value}' },
      splitLine: { lineStyle: { color: '#eef2f7' } }
    },
    series: [
      {
        name: '订单金额',
        type: 'bar',
        barWidth: 24,
        itemStyle: {
          borderRadius: [7, 7, 2, 2],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#14b8a6' },
            { offset: 1, color: '#0f766e' }
          ])
        },
        data: amountList.value
      },
      {
        name: '金额走势',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3 },
        itemStyle: { color: '#d97706' },
        areaStyle: { color: 'rgba(217, 119, 6, 0.08)' },
        data: amountList.value
      }
    ]
  })
}

const renderTodayChart = () => {
  if (!todayChartRef.value) return
  todayChart ||= echarts.init(todayChartRef.value)
  const converted = overview.todayOrderCount
  const waiting = Math.max(overview.todayAppointmentCount - overview.todayOrderCount, 0)
  const hasData = converted + waiting > 0
  todayChart.setOption({
    color: hasData ? ['#0f766e', '#c2410c'] : ['#e5e7eb'],
    title: {
      show: !hasData,
      text: '暂无数据',
      left: 'center',
      top: '42%',
      textStyle: {
        color: '#667085',
        fontSize: 14,
        fontWeight: 600
      }
    },
    tooltip: {
      show: hasData,
      trigger: 'item',
      backgroundColor: '#111827',
      borderWidth: 0,
      textStyle: { color: '#fff' }
    },
    legend: {
      show: hasData,
      bottom: 0,
      icon: 'circle',
      textStyle: { color: chartTextColor }
    },
    series: [
      {
        name: '今日转化',
        type: 'pie',
        radius: ['56%', '76%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: {
          show: hasData,
          color: chartTextColor,
          formatter: '{b}\n{c}'
        },
        data: hasData
          ? [
              { value: converted, name: '成交订单' },
              { value: waiting, name: '待转化预约' }
            ]
          : [{ value: 1, name: '暂无数据' }]
      }
    ]
  })
}

const renderCharts = async () => {
  await nextTick()
  renderAmountChart()
  renderTodayChart()
}

const load = async () => {
  loading.value = true
  try {
    const [overviewData, amountData] = await Promise.all([
      statisticsApi.overview(),
      statisticsApi.orderAmount()
    ])
    Object.assign(overview, overviewData)
    dateList.value = amountData.dateList || []
    amountList.value = (amountData.amountList || []).map((item) => Number(item || 0))
    renderCharts()
  } finally {
    loading.value = false
  }
}

const resizeCharts = () => {
  amountChart?.resize()
  todayChart?.resize()
}

onMounted(() => {
  load()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  amountChart?.dispose()
  todayChart?.dispose()
})
</script>
