<template>
  <div class="banner-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">轮播图管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增轮播图
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image :src="row.image" style="width: 160px; height: 60px; border-radius: 8px" fit="cover" v-if="row.image">
              <template #error><div class="image-error">无图</div></template>
            </el-image>
            <div v-else class="image-placeholder">无图</div>
          </template>
        </el-table-column>

        <el-table-column label="标题" min-width="150">
          <template #default="{ row }">
            <span>{{ row.title || '暂无标题' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="跳转链接" min-width="200">
          <template #default="{ row }">
            <span class="text-gray">{{ row.url || '无' }}</span>
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

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" background @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑轮播图' : '新增轮播图'" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="轮播图标题">
          <el-input v-model="form.title" placeholder="请输入标题（选填）" />
        </el-form-item>
        <el-form-item label="轮播图片" prop="image">
          <div style="width: 100%">
            <el-tabs v-model="imageTab">
              <el-tab-pane label="图片URL" name="url">
                <el-input v-model="form.image" placeholder="请输入图片URL地址" />
              </el-tab-pane>
              <el-tab-pane label="本地上传" name="upload">
                <el-upload action="/api/upload" :on-success="handleUploadSuccess" :show-file-list="false" accept="image/*">
                  <el-button size="small" type="primary">选择图片</el-button>
                </el-upload>
              </el-tab-pane>
            </el-tabs>
            <div class="image-preview" v-if="form.image">
              <el-image :src="form.image" style="width: 200px; height: 75px; border-radius: 8px" fit="cover" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.url" placeholder="请输入跳转链接（选填）" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const imageTab = ref('url')
const query = reactive({ pageNum: 1, pageSize: 10 })

const form = reactive({ title: '', image: '', url: '', sort: 0, status: 1 })
const rules = {
  image: [{ required: true, message: '请选择轮播图片', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/banner/list', { params: query })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { tableData.value = [] } finally { loading.value = false }
}

function resetForm() {
  editingId.value = null
  Object.assign(form, { title: '', image: '', url: '', sort: 0, status: 1 })
  imageTab.value = 'url'
}

function handleAdd() { resetForm(); dialogVisible.value = true }

function handleEdit(row) {
  editingId.value = row.id
  Object.assign(form, { title: row.title || '', image: row.image || '', url: row.url || '', sort: row.sort || 0, status: row.status })
  dialogVisible.value = true
}

function handleUploadSuccess(res) {
  if (res.code === 200) { form.image = res.data; ElMessage.success('上传成功') }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (editingId.value) {
      await request.put(`/admin/banner/${editingId.value}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/banner', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false; loadData()
  } catch {} finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/banner/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.page-title { font-size: 18px; font-weight: 600; color: #303133; }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 20px; padding-top: 20px; border-top: 1px solid #ebeef5; }
.image-placeholder, .image-error { width: 100%; height: 60px; background: #f5f7fa; border-radius: 8px; display: flex; justify-content: center; align-items: center; color: #c0c4cc; font-size: 12px; }
.image-preview { margin-top: 12px; padding: 8px; background: #f5f7fa; border-radius: 8px; display: inline-block; }
</style>
