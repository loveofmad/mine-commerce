<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="handleAdd">新增商品</el-button>
        </div>
      </template>
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width:200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="query.categoryId" placeholder="商品分类" clearable style="width:140px;margin-left:12px" @change="loadData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-button type="primary" style="margin-left:12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="图片" width="70">
          <template #default="{ row }">
            <el-image :src="row.mainImage" style="width:50px;height:50px" fit="cover" v-if="row.mainImage" :preview-src-list="[row.mainImage]" />
            <span v-else style="color:#999">无图</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="商品名称" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="70" />
        <el-table-column prop="sales" label="销量" width="70" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑商品' : '新增商品'" width="650px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="主图">
          <div style="width:100%">
            <el-tabs v-model="imageTab">
              <el-tab-pane label="图片URL" name="url">
                <el-input v-model="form.mainImage" placeholder="请输入图片URL地址" />
              </el-tab-pane>
              <el-tab-pane label="本地上传" name="upload">
                <el-upload action="/api/upload" :on-success="handleUploadSuccess" :show-file-list="false" accept="image/*">
                  <el-button size="small">选择图片</el-button>
                </el-upload>
              </el-tab-pane>
            </el-tabs>
            <el-image v-if="form.mainImage" :src="form.mainImage" style="width:100px;height:100px;margin-top:8px" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="form.detail" type="textarea" :rows="4" placeholder="请输入商品详情" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="上架" inactive-text="下架" />
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
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const imageTab = ref('url')
const query = reactive({ keyword: '', categoryId: '', pageNum: 1, pageSize: 10 })

const form = reactive({ title: '', subtitle: '', categoryId: null, price: 0, stock: 0, mainImage: '', detail: '', status: 1 })
const rules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/product/spu/list', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { tableData.value = [] } finally { loading.value = false }
}

async function loadCategories() {
  try { const res = await request.get('/api/category/list'); categories.value = res.data || [] } catch {}
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { title: '', subtitle: '', categoryId: null, price: 0, stock: 0, mainImage: '', detail: '', status: 1 })
  imageTab.value = 'url'
}

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleEdit(row) {
  editingId.value = row.id
  Object.assign(form, { title: row.title, subtitle: row.subtitle || '', categoryId: row.categoryId, price: row.price, stock: row.stock, mainImage: row.mainImage || '', detail: row.detail || '', status: row.status })
  dialogVisible.value = true
}

function handleUploadSuccess(res) {
  if (res.code === 200) { form.mainImage = res.data; ElMessage.success('上传成功') }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (editingId.value) { await request.put(`/admin/product/spu/${editingId.value}`, form); ElMessage.success('修改成功') }
    else { await request.post('/admin/product/spu', form); ElMessage.success('添加成功') }
    dialogVisible.value = false; loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/product/spu/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => { loadData(); loadCategories() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
