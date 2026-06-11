<template>
  <div class="coupon-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券列表</span>
          <el-button type="primary" @click="handleAdd">新增优惠券</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="优惠券名称" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'success' : 'primary'">{{ row.type === 0 ? '满减' : '折扣' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="面值" width="80">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="minAmount" label="最低消费" width="90">
          <template #default="{ row }">¥{{ row.minAmount }}</template>
        </el-table-column>
        <el-table-column prop="total" label="发行量" width="70" />
        <el-table-column prop="used" label="已领取" width="70" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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
const query = reactive({ pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/api/coupon/available')
    tableData.value = Array.isArray(res.data) ? res.data : (res.data?.list || [])
    total.value = tableData.value.length
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该优惠券吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/api/coupon/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

function handleAdd() {
  ElMessage.info('新增优惠券功能开发中')
}

function handleEdit(row) {
  ElMessage.info(`编辑优惠券: ${row.name}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
