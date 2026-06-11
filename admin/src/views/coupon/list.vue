<template>
  <div class="coupon-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
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
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @size-change="loadData" @current-change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑优惠券' : '新增优惠券'" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="0">满减</el-radio>
            <el-radio :label="1">折扣</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="面值/折扣" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="最低消费" prop="minAmount">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="发行量" prop="total">
          <el-input-number v-model="form.total" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const query = reactive({ pageNum: 1, pageSize: 10 })
const form = reactive({ name: '', type: 0, amount: 0, minAmount: 0, total: 100, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入面值', trigger: 'blur' }],
  total: [{ required: true, message: '请输入发行量', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try { const res = await request.get('/api/coupon/available'); tableData.value = Array.isArray(res.data) ? res.data : []; total.value = tableData.value.length }
  catch { tableData.value = [] } finally { loading.value = false }
}

function resetForm() { editingId.value = null; Object.assign(form, { name: '', type: 0, amount: 0, minAmount: 0, total: 100, status: 1 }) }

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleEdit(row) {
  editingId.value = row.id
  Object.assign(form, { name: row.name, type: row.type, amount: row.amount, minAmount: row.minAmount, total: row.total, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (editingId.value) { await request.put(`/api/coupon/${editingId.value}`, form); ElMessage.success('修改成功') }
    else { await request.post('/api/coupon', form); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该优惠券吗？', '提示', { type: 'warning' })
  try { await request.delete(`/api/coupon/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
