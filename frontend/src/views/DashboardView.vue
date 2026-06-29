<template>
  <div class="page">
    <h1 class="page-title">工作台</h1>
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
    <div class="panel">
      <div class="toolbar">
        <strong>近 7 天订单金额</strong>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <div ref="chartRef" class="chart-box"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi } from '@/api/modules'

const chartRef = ref<HTMLDivElement>()
const overview = reactive({
  customerCount: 0,
  todayAppointmentCount: 0,
  todayOrderCount: 0,
  todayAmount: 0
})

const renderChart = (dateList: string[], amountList: number[]) => {
  if (!chartRef.value) return
  const chart = echarts.init(chartRef.value)
  chart.setOption({
    grid: { left: 48, right: 24, top: 28, bottom: 36 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dateList },
    yAxis: { type: 'value' },
    series: [
      {
        name: '订单金额',
        type: 'line',
        smooth: true,
        areaStyle: {},
        data: amountList
      }
    ]
  })
}

const load = async () => {
  const [overviewData, amountData] = await Promise.all([
    statisticsApi.overview(),
    statisticsApi.orderAmount()
  ])
  Object.assign(overview, overviewData)
  renderChart(amountData.dateList, amountData.amountList)
}

onMounted(load)
</script>
