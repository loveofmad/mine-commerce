<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
        </div>
      </template>
      <div class="filter-bar">
        <el-select v-model="query.status" placeholder="订单状态" clearable style="width: 140px" @change="loadData">
          <el-option label="待付款" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="订单号" width="100" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="orderStatusType(row.status)">{{ orderStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" @click="handleShip(row)">发货</el-button>
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ status: '', pageNum: 1, pageSize: 10 })

function orderStatusType(status) {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success' }
  return map[status] || 'info'
}

function orderStatusLabel(status) {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成' }
  return map[status] || '未知'
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/order/list', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function handleShip(row) {
  await ElMessageBox.confirm('确认发货？', '提示', { type: 'info' })
  try {
    await request.put(`/admin/order/${row.id}/deliver`)
    ElMessage.success('发货成功')
    loadData()
  } catch {}
}

async function handleCancel(row) {
  await ElMessageBox.confirm('确认取消订单？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/order/${row.id}`)
    ElMessage.success('已取消')
    loadData()
  } catch {}
}

function handleDetail(row) {
  ElMessage.info(`查看订单: ${row.id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
