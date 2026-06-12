<template>
  <div class="product-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">商品管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增商品
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.categoryId" placeholder="商品分类" clearable style="width: 160px; margin-left: 12px" @change="loadData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" row-class-name="table-row">
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info-cell">
              <el-image 
                :src="row.mainImage" 
                style="width: 56px; height: 56px; border-radius: 8px; flex-shrink: 0" 
                fit="cover"
                v-if="row.mainImage"
              >
                <template #error>
                  <div class="image-error">无图</div>
                </template>
              </el-image>
              <div v-else class="image-placeholder">无图</div>
              <div class="product-text">
                <div class="product-name">{{ row.title }}</div>
                <div class="product-subtitle">{{ row.subtitle || '暂无副标题' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="价格" min-width="120" align="center">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="库存" min-width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>

        <el-table-column label="销量" min-width="100" align="center">
          <template #default="{ row }">
            <span class="text-gray">{{ row.sales || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" min-width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="editingId ? '编辑商品' : '新增商品'" 
      width="650px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="product-form">
        <el-form-item label="商品名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="请输入副标题（选填）" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品价格" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规格属性">
          <el-input v-model="form.spec" placeholder="如：重量5斤、颜色红色等（选填）" />
        </el-form-item>
        <el-form-item label="商品主图">
          <div class="image-upload-area">
            <el-tabs v-model="imageTab" class="image-tabs">
              <el-tab-pane label="图片URL" name="url">
                <el-input v-model="form.mainImage" placeholder="请输入图片URL地址" />
              </el-tab-pane>
              <el-tab-pane label="本地上传" name="upload">
                <el-upload 
                  action="/api/upload" 
                  :on-success="handleUploadSuccess" 
                  :on-error="handleUploadError"
                  :before-upload="beforeUpload"
                  :show-file-list="false" 
                  accept="image/*"
                >
                  <el-button size="small" type="primary">选择图片</el-button>
                </el-upload>
              </el-tab-pane>
            </el-tabs>
            <div class="image-preview" v-if="form.mainImage">
              <el-image :src="form.mainImage" style="width: 120px; height: 120px; border-radius: 8px" fit="cover" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="商品详情">
          <el-input v-model="form.detail" type="textarea" :rows="4" placeholder="请输入商品详情（选填）" />
        </el-form-item>
        <el-form-item label="商品状态">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue'
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

const form = reactive({ title: '', subtitle: '', spec: '', categoryId: null, price: 0, stock: 0, mainImage: '', detail: '', status: 1 })
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

function resetQuery() {
  query.keyword = ''
  query.categoryId = ''
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { title: '', subtitle: '', spec: '', categoryId: null, price: 0, stock: 0, mainImage: '', detail: '', status: 1 })
  imageTab.value = 'url'
}

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleEdit(row) {
  editingId.value = row.id
  Object.assign(form, {
    title: row.title,
    subtitle: row.subtitle || '',
    spec: row.spec || '',
    categoryId: row.categoryId,
    price: row.price,
    stock: row.stock,
    mainImage: row.mainImage || '',
    detail: row.detail || '',
    status: row.status
  })
  dialogVisible.value = true
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

function handleUploadSuccess(response) {
  console.log('上传响应:', response)
  if (response.code === 200) {
    form.mainImage = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleUploadError(error) {
  console.error('上传错误:', error)
  ElMessage.error('上传失败，请重试')
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (editingId.value) {
      await request.put('/admin/product/spu', { ...form, id: editingId.value })
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/product/spu', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/product/spu/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => { loadData(); loadCategories() })
</script>

<style scoped>
.product-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.product-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.image-placeholder, .image-error {
  width: 64px;
  height: 64px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #c0c4cc;
  font-size: 12px;
}

.product-text {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-subtitle {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
}

.text-danger {
  color: #f56c6c;
  font-weight: 500;
}

.text-gray {
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.product-form :deep(.el-form-item__label) {
  font-weight: 500;
}

.image-upload-area {
  width: 100%;
}

.image-tabs {
  margin-bottom: 12px;
}

.image-preview {
  margin-top: 12px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 8px;
  display: inline-block;
}

:deep(.el-table) {
  width: 100% !important;
  border-radius: 8px;
}

:deep(.el-table__body-wrapper) {
  width: 100%;
}

:deep(.el-table__header-wrapper) {
  width: 100%;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
