<template>
  <div class="category-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">分类管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column label="图标" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.icon && !row.icon.startsWith('http')" style="font-size: 28px;">{{ row.icon }}</span>
            <el-image v-else-if="row.icon" :src="row.icon" style="width: 40px; height: 40px; border-radius: 8px" fit="cover">
              <template #error><div class="image-error">无</div></template>
            </el-image>
            <div v-else class="image-placeholder">无</div>
          </template>
        </el-table-column>

        <el-table-column label="分类名称" min-width="200">
          <template #default="{ row }">
            <span :style="{ paddingLeft: (row.level - 1) * 20 + 'px' }">{{ row.name }}</span>
          </template>
        </el-table-column>

        <el-table-column label="层级" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">第{{ row.level }}级</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="排序" width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.sort }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="success" plain @click="handleAddChild(row)">添加子分类</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="父分类">
          <el-input v-model="parentName" disabled placeholder="顶级分类" />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类图标">
          <div style="width: 100%">
            <el-input v-model="form.icon" placeholder="请输入 emoji 图标，如 🍎" />
            <div class="icon-preview" v-if="form.icon">
              <span style="font-size: 36px;">{{ form.icon }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const parentId = ref(0)
const parentName = ref('顶级分类')

const form = reactive({ name: '', icon: '', parentId: 0, level: 1, sort: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const dialogTitle = computed(() => {
  if (editingId.value) return '编辑分类'
  return parentId.value > 0 ? '添加子分类' : '新增分类'
})

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/api/category/list')
    const list = res.data || []
    tableData.value = buildTree(list, 0)
  } catch { tableData.value = [] } finally { loading.value = false }
}

function buildTree(list, parentId) {
  return list.filter(item => item.parentId === parentId).map(item => ({
    ...item,
    children: buildTree(list, item.id)
  }))
}

function resetForm() {
  editingId.value = null
  parentId.value = 0
  parentName.value = '顶级分类'
  Object.assign(form, { name: '', icon: '', parentId: 0, level: 1, sort: 0, status: 1 })
}

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleAddChild(row) {
  resetForm()
  parentId.value = row.id
  parentName.value = row.name
  form.parentId = row.id
  form.level = row.level + 1
  dialogVisible.value = true
}

function handleEdit(row) {
  editingId.value = row.id
  parentId.value = row.parentId || 0
  parentName.value = row.parentId > 0 ? '上级分类' : '顶级分类'
  Object.assign(form, { name: row.name, icon: row.icon || '', parentId: row.parentId, level: row.level, sort: row.sort || 0, status: row.status })
  dialogVisible.value = true
}

function handleIconUpload(res) {
  if (res.code === 200) { form.icon = res.data; ElMessage.success('上传成功') }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (editingId.value) {
      await request.put(`/admin/category/${editingId.value}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/category', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false; loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/category/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; }
.image-placeholder, .image-error { width: 40px; height: 40px; background: #f5f7fa; border-radius: 8px; display: flex; justify-content: center; align-items: center; color: #c0c4cc; font-size: 10px; }
.icon-preview { margin-top: 12px; padding: 8px; background: #f5f7fa; border-radius: 8px; display: inline-flex; align-items: center; justify-content: center; }
</style>
