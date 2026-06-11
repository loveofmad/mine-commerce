<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品列表</span>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="query.categoryId" placeholder="商品分类" clearable style="width: 160px; margin-left: 12px" @change="loadData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
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
const categories = ref([])
const query = reactive({ keyword: '', categoryId: '', pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/product/spu/list', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/product/spu/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

function handleAdd() {
  ElMessage.info('新增商品功能开发中')
}

function handleEdit(row) {
  ElMessage.info(`编辑商品: ${row.name}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
