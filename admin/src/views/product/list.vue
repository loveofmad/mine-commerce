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

      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索商品名称" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.categoryId" placeholder="商品分类" clearable style="width: 160px; margin-left: 12px" @change="loadData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-select v-model="query.sortField" placeholder="排序方式" style="width: 160px; margin-left: 12px" @change="loadData">
          <el-option label="按添加时间正序" value="create_asc" />
          <el-option label="按添加时间倒序" value="create_desc" />
          <el-option label="按排序值正序" value="sort_asc" />
          <el-option label="按销量倒序" value="sales_desc" />
          <el-option label="按价格升序" value="price_asc" />
          <el-option label="按价格降序" value="price_desc" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="success" plain :disabled="selectedRows.length === 0" @click="batchUpdateStatus(1)">批量上架</el-button>
        <el-button type="warning" plain :disabled="selectedRows.length === 0" @click="batchUpdateStatus(0)">批量下架</el-button>
        <el-button type="danger" plain :disabled="selectedRows.length === 0" @click="batchDelete">批量删除</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%" @selection-change="handleSelectionChange" ref="tableRef">
        <el-table-column type="selection" width="50" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div class="product-info-cell">
              <el-image 
                :src="row.mainImage" 
                style="width: 56px; height: 56px; border-radius: 8px; flex-shrink: 0" 
                fit="cover"
                v-if="row.mainImage"
              >
                <template #error><div class="image-error">无图</div></template>
              </el-image>
              <div v-else class="image-placeholder">无图</div>
              <div class="product-text">
                <div class="product-name">{{ row.title }}</div>
                <div class="product-subtitle">{{ row.subtitle || '暂无副标题' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="规格" min-width="180">
          <template #default="{ row }">
            <div v-if="row.skuList && row.skuList.length > 0" class="sku-tags">
              <el-tag v-for="sku in row.skuList.slice(0, 3)" :key="sku.id" size="small" type="info" class="sku-tag">
                {{ sku.title }} ¥{{ sku.price }}
              </el-tag>
              <el-tag v-if="row.skuList.length > 3" size="small" type="info" class="sku-tag">
                +{{ row.skuList.length - 3 }}个
              </el-tag>
            </div>
            <span v-else class="text-muted">无规格</span>
          </template>
        </el-table-column>

        <el-table-column label="价格" min-width="100" align="center">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column label="库存" min-width="80" align="center">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>

        <el-table-column label="销量" min-width="80" align="center">
          <template #default="{ row }">
            <span class="text-muted">{{ row.sales || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="排序" width="120" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.sort" :min="0" :max="9999" size="small" controls-position="right" style="width: 90px" @change="(val) => handleSortChange(row, val)" />
          </template>
        </el-table-column>

        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog 
      v-model="dialogVisible" 
      :title="editingId ? '编辑商品' : '新增商品'" 
      width="900px"
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

        <!-- SKU规格管理 -->
        <el-divider content-position="left">商品规格 (SKU)</el-divider>
        <div class="sku-manager">
          <div class="sku-header">
            <el-row :gutter="12">
              <el-col :span="5"><span class="sku-label">规格名称</span></el-col>
              <el-col :span="4"><span class="sku-label">价格</span></el-col>
              <el-col :span="4"><span class="sku-label">库存</span></el-col>
              <el-col :span="6"><span class="sku-label">规格属性</span></el-col>
              <el-col :span="4"><span class="sku-label">图片</span></el-col>
              <el-col :span="1"></el-col>
            </el-row>
          </div>
          <div v-for="(sku, index) in skuList" :key="sku.id || index" class="sku-item">
            <el-row :gutter="12" align="middle">
              <el-col :span="5">
                <el-input v-model="sku.title" placeholder="规格名称" />
              </el-col>
              <el-col :span="4">
                <el-input-number v-model="sku.price" :min="0" :precision="2" controls-position="right" style="width:100%" />
              </el-col>
              <el-col :span="4">
                <el-input-number v-model="sku.stock" :min="0" controls-position="right" style="width:100%" />
              </el-col>
              <el-col :span="6">
                <el-input v-model="sku.specs" placeholder="颜色:红色;尺寸:大" />
              </el-col>
              <el-col :span="4">
                <el-upload
                  action="/api/upload"
                  :on-success="(res) => handleSkuUploadSuccess(res, index)"
                  :show-file-list="false"
                  accept="image/*"
                >
                  <el-button size="small" :type="sku.image ? 'success' : 'default'">
                    {{ sku.image ? '已上传' : '上传图片' }}
                  </el-button>
                </el-upload>
              </el-col>
              <el-col :span="1">
                <el-button type="danger" :icon="Delete" circle size="small" @click="removeSku(index)" />
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" plain @click="addSku" style="margin-top: 12px">
            <el-icon><Plus /></el-icon>添加规格
          </el-button>
        </div>

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
        <el-form-item label="商品图片">
          <div class="multi-image-area">
            <div class="image-list" v-if="form.images.length > 0">
              <div class="image-item" v-for="(img, idx) in form.images" :key="idx">
                <el-image :src="img" style="width: 80px; height: 80px; border-radius: 6px" fit="cover" />
                <el-button class="image-remove" type="danger" :icon="Delete" circle size="small" @click="removeImage(idx)" />
              </div>
            </div>
            <el-upload
              action="/api/upload"
              :on-success="handleMultiUploadSuccess"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <el-button size="small" type="primary" plain>+ 添加图片</el-button>
            </el-upload>
            <div class="image-tip">第一张为主图，后续图片在商品详情页轮播展示</div>
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
import { Plus, Search, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const tableRef = ref(null)
const imageTab = ref('url')
const selectedRows = ref([])
const query = reactive({ keyword: '', categoryId: '', sortField: 'create_desc', pageNum: 1, pageSize: 10 })

const form = reactive({ title: '', subtitle: '', spec: '', categoryId: null, price: 0, stock: 0, mainImage: '', images: [], detail: '', status: 1, sort: 0 })
const skuList = ref([])
const rules = {
  title: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    delete params.sortField
    const [field, order] = query.sortField.split('_')
    params.sortField = field
    params.sortOrder = order
    const res = await request.get('/admin/product/spu/list', { params })
    const records = res.data?.records || []
    total.value = res.data?.total || 0
    const recordsWithSku = await Promise.all(records.map(async (spu) => {
      try {
        const skuRes = await request.get(`/admin/product/sku/${spu.id}`)
        return { ...spu, skuList: skuRes.data || [] }
      } catch {
        return { ...spu, skuList: [] }
      }
    }))
    tableData.value = recordsWithSku
  } catch { tableData.value = [] } finally { loading.value = false }
}

async function loadCategories() {
  try { const res = await request.get('/api/category/list'); categories.value = res.data || [] } catch {}
}

function resetQuery() {
  query.keyword = ''
  query.categoryId = ''
  query.sortField = 'create_desc'
  query.pageNum = 1
  loadData()
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { title: '', subtitle: '', spec: '', categoryId: null, price: 0, stock: 0, mainImage: '', images: [], detail: '', status: 1, sort: 0 })
  skuList.value = []
  imageTab.value = 'url'
}

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleEdit(row) {
  editingId.value = row.id
  let images = []
  if (row.images) {
    try { images = JSON.parse(row.images) } catch { images = [] }
  }
  Object.assign(form, {
    title: row.title,
    subtitle: row.subtitle || '',
    spec: row.spec || '',
    categoryId: row.categoryId,
    price: row.price,
    stock: row.stock,
    mainImage: row.mainImage || '',
    images,
    detail: row.detail || '',
    status: row.status,
    sort: row.sort || 0
  })
  loadSkuList(row.id)
  dialogVisible.value = true
}

function addSku() {
  skuList.value.push({
    _tempId: '_' + Math.random().toString(36).substr(2, 9),
    title: '', price: 0, stock: 0, specs: '', image: '', status: 1
  })
}

function removeSku(index) {
  skuList.value.splice(index, 1)
}

function handleSkuUploadSuccess(response, index) {
  if (response.code === 200) {
    skuList.value[index].image = response.data
    ElMessage.success('图片上传成功')
  }
}

async function loadSkuList(spuId) {
  try {
    const res = await request.get(`/admin/product/sku/${spuId}`)
    skuList.value = res.data || []
  } catch {
    skuList.value = []
  }
}

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) { ElMessage.error('只能上传图片文件!'); return false }
  if (!isLt2M) { ElMessage.error('图片大小不能超过 2MB!'); return false }
  return true
}

function handleUploadSuccess(response) {
  if (response.code === 200) {
    form.mainImage = response.data
    ElMessage.success('上传成功')
  }
}

function handleUploadError() {
  ElMessage.error('上传失败，请重试')
}

function handleMultiUploadSuccess(response) {
  if (response.code === 200) {
    form.images.push(response.data)
    ElMessage.success('图片添加成功')
  }
}

function removeImage(index) {
  form.images.splice(index, 1)
}

async function handleSortChange(row, val) {
  try {
    await request.put('/admin/product/spu', { id: row.id, sort: val })
  } catch {}
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    let spuId = editingId.value
    const spuData = { ...form, images: JSON.stringify(form.images) }
    if (spuId) {
      await request.put('/admin/product/spu', { ...spuData, id: spuId })
    } else {
      const res = await request.post('/admin/product/spu', spuData)
      spuId = res.data
    }
    for (const sku of skuList.value) {
      const skuData = { ...sku, spuId, _tempId: undefined }
      if (sku.id) {
        await request.put('/admin/product/sku', skuData)
      } else {
        await request.post('/admin/product/sku', skuData)
      }
    }
    ElMessage.success(spuId === editingId.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/product/spu/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

function handleSelectionChange(rows) {
  selectedRows.value = rows
}

async function batchUpdateStatus(status) {
  const ids = selectedRows.value.map(r => r.id)
  const label = status === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确定批量${label} ${ids.length} 个商品吗？`, '提示', { type: 'info' })
  try {
    for (const id of ids) {
      await request.put(`/admin/product/spu/${id}/status`, null, { params: { status } })
    }
    ElMessage.success(`批量${label}成功`)
    selectedRows.value = []
    loadData()
  } catch {}
}

async function batchDelete() {
  const ids = selectedRows.value.map(r => r.id)
  await ElMessageBox.confirm(`确定删除 ${ids.length} 个商品吗？此操作不可恢复`, '警告', { type: 'warning' })
  try {
    for (const id of ids) {
      await request.delete(`/admin/product/spu/${id}`)
    }
    ElMessage.success('批量删除成功')
    selectedRows.value = []
    loadData()
  } catch {}
}

onMounted(() => { loadData(); loadCategories() })
</script>

<style scoped>
.product-list { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 20px; font-weight: 700; color: var(--text-primary); }
.filter-bar { display: flex; align-items: center; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid var(--border); }
.product-info-cell { display: flex; align-items: center; gap: 12px; }
.image-placeholder, .image-error { width: 56px; height: 56px; background: var(--bg-page); border-radius: 8px; display: flex; justify-content: center; align-items: center; color: var(--text-muted); font-size: 12px; }
.product-text { flex: 1; min-width: 0; }
.product-name { font-size: 14px; font-weight: 500; color: var(--text-primary); }
.product-subtitle { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
.price-text { color: var(--accent); font-weight: 600; }
.text-danger { color: var(--danger); font-weight: 500; }
.text-muted { color: var(--text-muted); }
.sku-tags { display: flex; flex-wrap: wrap; gap: 4px; }
.sku-tag { margin: 0; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 20px; border-top: 1px solid var(--border); }
.product-form :deep(.el-form-item__label) { font-weight: 500; }
.image-upload-area { width: 100%; }
.image-tabs { margin-bottom: 12px; }
.image-preview { margin-top: 12px; padding: 8px; background: var(--bg-page); border-radius: 8px; display: inline-block; }
.multi-image-area { width: 100%; }
.image-list { display: flex; flex-wrap: wrap; gap: 12px; margin-bottom: 12px; }
.image-item { position: relative; }
.image-remove { position: absolute; top: -8px; right: -8px; }
.image-tip { font-size: 12px; color: var(--text-muted); margin-top: 8px; }
.sku-manager { width: 100%; }
.sku-header { margin-bottom: 8px; padding: 0 12px; }
.sku-label { font-size: 12px; color: var(--text-muted); font-weight: 500; }
.sku-item { padding: 12px; margin-bottom: 8px; background: var(--bg-page); border-radius: 8px; }
</style>
