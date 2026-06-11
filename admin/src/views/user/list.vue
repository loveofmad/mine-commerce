<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索用户名/手机号" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
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
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ keyword: '', pageNum: 1, pageSize: 10 })

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/user/list', { params: query })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch {
    tableData.value = []
  } finally {
    loading.value = false
  }
}

function handleDetail(row) {
  ElMessage.info(`查看用户: ${row.username}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
