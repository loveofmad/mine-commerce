<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>
      <div class="filter-bar">
        <el-input v-model="query.keyword" placeholder="搜索用户名/手机号" clearable style="width:240px" @clear="loadData" @keyup.enter="loadData" />
        <el-button type="primary" style="margin-left:12px" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-image :src="row.avatar" style="width:40px;height:40px;border-radius:50%" fit="cover" v-if="row.avatar" />
            <span v-else style="color:#999">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="memberLevel" label="等级" width="70">
          <template #default="{ row }"><el-tag size="small">V{{ row.memberLevel }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="150" />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" style="margin-top:16px;justify-content:flex-end" @size-change="loadData" @current-change="loadData" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑用户" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="头像">
          <div style="width:100%">
            <el-tabs v-model="avatarTab">
              <el-tab-pane label="图片URL" name="url">
                <el-input v-model="form.avatar" placeholder="请输入头像URL" />
              </el-tab-pane>
              <el-tab-pane label="本地上传" name="upload">
                <el-upload action="/api/upload" :on-success="handleAvatarUpload" :show-file-list="false" accept="image/*">
                  <el-button size="small">选择图片</el-button>
                </el-upload>
              </el-tab-pane>
            </el-tabs>
            <el-image v-if="form.avatar" :src="form.avatar" style="width:80px;height:80px;margin-top:8px;border-radius:50%" fit="cover" />
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-input-number v-model="form.memberLevel" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="积分">
          <el-input-number v-model="form.points" :min="0" />
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
import request from '@/utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const avatarTab = ref('url')
const query = reactive({ keyword: '', pageNum: 1, pageSize: 10 })
const form = reactive({ username: '', nickname: '', phone: '', avatar: '', memberLevel: 1, points: 0 })
const rules = { nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try { const res = await request.get('/admin/user/list', { params: query }); tableData.value = res.data?.records || []; total.value = res.data?.total || 0 }
  catch { tableData.value = [] } finally { loading.value = false }
}

function resetForm() { editingId.value = null; Object.assign(form, { username: '', nickname: '', phone: '', avatar: '', memberLevel: 1, points: 0 }); avatarTab.value = 'url' }

function handleEdit(row) {
  editingId.value = row.id
  Object.assign(form, { username: row.username, nickname: row.nickname || '', phone: row.phone || '', avatar: row.avatar || '', memberLevel: row.memberLevel || 1, points: row.points || 0 })
  dialogVisible.value = true
}

function handleAvatarUpload(res) { if (res.code === 200) { form.avatar = res.data; ElMessage.success('上传成功') } }

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try { await request.put('/admin/user', { ...form, id: editingId.value }); ElMessage.success('修改成功'); dialogVisible.value = false; loadData() }
  catch {} finally { submitLoading.value = false }
}

async function handleStatusChange(row) {
  try { await request.put(`/admin/user/${row.id}/status`, null, { params: { status: row.status } }); ElMessage.success('状态更新成功') }
  catch { row.status = row.status === 1 ? 0 : 1 }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/user/${row.id}`); ElMessage.success('删除成功'); loadData() } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.filter-bar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
