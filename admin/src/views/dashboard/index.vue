<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: item.color }">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header><span>最近订单</span></template>
          <el-table :data="recentOrders" stripe>
            <el-table-column prop="id" label="订单号" width="100" />
            <el-table-column prop="username" label="用户" />
            <el-table-column prop="totalAmount" label="金额">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="orderStatusType(row.status)">{{ orderStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>快捷操作</span></template>
          <div class="shortcuts">
            <el-button type="primary" @click="$router.push('/product')">商品管理</el-button>
            <el-button type="success" @click="$router.push('/order')">订单管理</el-button>
            <el-button type="warning" @click="$router.push('/coupon')">优惠券管理</el-button>
            <el-button type="info" @click="$router.push('/user')">用户管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const stats = ref([
  { title: '今日订单', value: 0, icon: 'ShoppingCart', color: '#409eff' },
  { title: '今日销售额', value: '¥0.00', icon: 'Money', color: '#67c23a' },
  { title: '商品总数', value: 0, icon: 'Goods', color: '#e6a23c' },
  { title: '注册用户', value: 0, icon: 'User', color: '#f56c6c' }
])

const recentOrders = ref([])

function orderStatusType(status) {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success' }
  return map[status] || 'info'
}

function orderStatusLabel(status) {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成' }
  return map[status] || '未知'
}
</script>

<style scoped>
.stat-row { margin-bottom: 20px; }
.stat-card { cursor: pointer; }
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-title { font-size: 14px; color: #909399; }
.stat-value { font-size: 24px; font-weight: 600; margin-top: 8px; }
.stat-icon { font-size: 48px; opacity: 0.3; }
.shortcuts { display: flex; flex-direction: column; gap: 12px; }
.shortcuts .el-button { width: 100%; }
</style>
