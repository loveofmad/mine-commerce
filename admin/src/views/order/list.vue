<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header"><span>订单管理</span></div>
      </template>
      <div class="filter-bar">
        <el-select v-model="query.status" placeholder="订单状态" clearable style="width:130px" @change="loadData">
          <el-option label="待付款" :value="0" />
          <el-option label="已付款" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索订单号" clearable style="width:200px;margin-left:12px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" style="margin-left:12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="totalAmount" label="订单金额" width="90">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付" width="80">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="orderStatusType(row.status)">{{ orderStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 0" link type="danger" @click="handleCancel(row)">取消</el-button>
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @size-change="loadData" @current-change="loadData" />
    </el-card>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="650px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="orderStatusType(currentOrder.status)">{{ orderStatusLabel(currentOrder.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ currentOrder.freightAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentOrder.payType === 0 ? '微信' : '支付宝' }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="收货电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '未支付' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ currentOrder.deliverTime || '未发货' }}</el-descriptions-item>
        <el-descriptions-item label="收货时间">{{ currentOrder.receiveTime || '未收货' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const query = reactive({ status: '', keyword: '', pageNum: 1, pageSize: 10 })

function orderStatusType(s) { return { 0:'warning', 1:'primary', 2:'info', 3:'success', 4:'danger' }[s] || 'info' }
function orderStatusLabel(s) { return { 0:'待付款', 1:'已付款', 2:'已发货', 3:'已完成', 4:'已取消' }[s] || '未知' }

async function loadData() {
  loading.value = true
  try { const res = await request.get('/admin/order/list', { params: query }); tableData.value = res.data?.records || []; total.value = res.data?.total || 0 }
  catch { tableData.value = [] } finally { loading.value = false }
}

async function handleShip(row) {
  await ElMessageBox.confirm('确认发货？', '提示', { type: 'info' })
  try { await request.put(`/admin/order/${row.id}/deliver`); ElMessage.success('发货成功'); loadData() } catch {}
}

async function handleCancel(row) {
  await ElMessageBox.confirm('确认取消订单？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/order/${row.id}`); ElMessage.success('已取消'); loadData() } catch {}
}

async function handleDetail(row) {
  try { const res = await request.get(`/admin/order/${row.id}`); currentOrder.value = res.data; detailVisible.value = true } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
