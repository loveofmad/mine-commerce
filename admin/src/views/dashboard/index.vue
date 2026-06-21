<template>
  <div class="dashboard" v-loading="pageLoading">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="(item, index) in stats" :key="item.title">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: item.bgColor }">
            <el-icon :size="24" :style="{ color: item.color }"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-label">{{ item.title }}</div>
            <div class="stat-value">{{ item.value }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7天销售趋势</span>
            </div>
          </template>
          <div ref="salesTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">订单状态分布</span>
            </div>
          </template>
          <div ref="orderStatusChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">分类销售排行</span>
            </div>
          </template>
          <div ref="categorySalesChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7天订单量</span>
            </div>
          </template>
          <div ref="orderTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">最近订单</span>
              <el-button type="primary" link @click="$router.push('/order')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" min-width="160" />
            <el-table-column prop="receiverName" label="收货人" width="100" />
            <el-table-column label="金额" width="100" align="right">
              <template #default="{ row }">
                <span class="price-text">¥{{ row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="orderStatusType(row.status)" size="small">{{ orderStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">快捷操作</span>
            </div>
          </template>
          <div class="shortcuts">
            <div class="shortcut-item" v-for="item in shortcuts" :key="item.title" @click="$router.push(item.path)">
              <div class="shortcut-icon" :style="{ background: item.bgColor }">
                <el-icon :size="20" :style="{ color: item.color }"><component :is="item.icon" /></el-icon>
              </div>
              <div class="shortcut-info">
                <div class="shortcut-title">{{ item.title }}</div>
                <div class="shortcut-desc">{{ item.desc }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const stats = ref([
  { title: '今日订单', value: '0', icon: 'ShoppingCart', color: '#4A90A4', bgColor: 'rgba(74,144,164,0.1)' },
  { title: '今日销售额', value: '¥0.00', icon: 'Money', color: '#52A88C', bgColor: 'rgba(82,168,140,0.1)' },
  { title: '商品总数', value: '0', icon: 'Goods', color: '#6BB0C4', bgColor: 'rgba(107,176,196,0.1)' },
  { title: '注册用户', value: '0', icon: 'User', color: '#E07A5F', bgColor: 'rgba(224,122,95,0.1)' }
])

const shortcuts = ref([
  { title: '商品管理', desc: '管理商品信息', icon: 'Goods', color: '#4A90A4', bgColor: 'rgba(74,144,164,0.1)', path: '/product' },
  { title: '订单管理', desc: '查看订单状态', icon: 'List', color: '#52A88C', bgColor: 'rgba(82,168,140,0.1)', path: '/order' },
  { title: '用户管理', desc: '管理用户信息', icon: 'User', color: '#6BB0C4', bgColor: 'rgba(107,176,196,0.1)', path: '/user' },
  { title: '优惠券管理', desc: '创建优惠活动', icon: 'Ticket', color: '#E07A5F', bgColor: 'rgba(224,122,95,0.1)', path: '/coupon' }
])

const recentOrders = ref([])
const pageLoading = ref(true)

const salesTrendChart = ref(null)
const orderStatusChart = ref(null)
const categorySalesChart = ref(null)
const orderTrendChart = ref(null)

let charts = []

function orderStatusType(status) {
  const map = { 0: 'warning', 1: 'primary', 2: 'info', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

function orderStatusLabel(status) {
  const map = { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

function initSalesTrend(data) {
  const chart = echarts.init(salesTrendChart.value)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>销售额: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.dates, axisLine: { lineStyle: { color: '#ddd' } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#666', formatter: '¥{value}' } },
    series: [{
      type: 'line',
      data: data.sales,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color: '#4A90A4', width: 3 },
      itemStyle: { color: '#4A90A4' },
      areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(74,144,164,0.3)' }, { offset: 1, color: 'rgba(74,144,164,0.02)' }]) }
    }]
  })
}

function initOrderStatus(data) {
  const chart = echarts.init(orderStatusChart.value)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center', textStyle: { color: '#666' } },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: data
    }]
  })
}

function initCategorySales(data) {
  const chart = echarts.init(categorySalesChart.value)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: '{b}: ¥{c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#666', formatter: '¥{value}' } },
    yAxis: { type: 'category', data: data.categories.reverse(), axisLine: { lineStyle: { color: '#ddd' } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'bar',
      data: data.sales.reverse(),
      barWidth: 20,
      itemStyle: { borderRadius: [0, 4, 4, 0], color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#4A90A4' }, { offset: 1, color: '#6BB0C4' }]) }
    }]
  })
}

function initOrderTrend(data) {
  const chart = echarts.init(orderTrendChart.value)
  charts.push(chart)
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}<br/>订单量: {c}' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.dates, axisLine: { lineStyle: { color: '#ddd' } }, axisLabel: { color: '#666' } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } }, axisLabel: { color: '#666' } },
    series: [{
      type: 'bar',
      data: data.orderCounts,
      barWidth: 24,
      itemStyle: { borderRadius: [4, 4, 0, 0], color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: '#52A88C' }, { offset: 1, color: 'rgba(82,168,140,0.3)' }]) }
    }]
  })
}

function handleResize() {
  charts.forEach(c => c.resize())
}

onMounted(async () => {
  try {
    const res = await request.get('/admin/dashboard/stats')
    const data = res.data || {}
    stats.value[0].value = String(data.todayOrders || 0)
    stats.value[1].value = '¥' + (data.todaySales || 0)
    stats.value[2].value = String(data.productCount || 0)
    stats.value[3].value = String(data.userCount || 0)
  } catch {}

  try {
    const res = await request.get('/admin/order/list', { params: { pageNum: 1, pageSize: 5 } })
    recentOrders.value = res.data?.records || []
  } catch {}

  await nextTick()

  try {
    const [salesRes, statusRes, categoryRes, orderRes] = await Promise.all([
      request.get('/admin/dashboard/salesTrend'),
      request.get('/admin/dashboard/orderStatus'),
      request.get('/admin/dashboard/categorySales'),
      request.get('/admin/dashboard/salesTrend')
    ])
    if (salesRes.data) initSalesTrend(salesRes.data)
    if (statusRes.data) initOrderStatus(statusRes.data)
    if (categoryRes.data) initCategorySales(categoryRes.data)
    if (orderRes.data) initOrderTrend(orderRes.data)
  } catch {}
  
  pageLoading.value = false
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
  charts = []
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-row {
  margin-bottom: 20px;
}

.chart-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid var(--border);
  transition: all 0.2s;
  cursor: pointer;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.content-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.chart-container {
  width: 100%;
  height: 300px;
}

.price-text {
  color: var(--accent);
  font-weight: 600;
}

.shortcuts {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.shortcut-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.shortcut-item:hover {
  background: var(--bg-page);
}

.shortcut-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.shortcut-info {
  flex: 1;
}

.shortcut-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.shortcut-desc {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
