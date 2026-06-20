<template>
  <div class="order-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">订单管理</span>
        </div>
      </template>

      <div class="filter-bar">
        <el-select v-model="query.status" placeholder="订单状态" clearable style="width:130px" @change="loadData">
          <el-option label="待付款" :value="0" />
          <el-option label="已付款" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="搜索订单号" clearable style="width:200px;margin-left:12px" @clear="loadData" @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" style="margin-left:12px" @click="loadData">
          <el-icon><Search /></el-icon>搜索
        </el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column label="商品信息" min-width="300">
          <template #default="{ row }">
            <div v-if="row.orderItems && row.orderItems.length > 0" class="order-items-cell">
              <div v-for="item in row.orderItems.slice(0, 2)" :key="item.id" class="order-item-row">
                <el-image 
                  :src="item.skuImage" 
                  style="width: 40px; height: 40px; border-radius: 6px; flex-shrink: 0" 
                  fit="cover"
                >
                  <template #error><div class="image-error">无图</div></template>
                </el-image>
                <div class="item-info">
                  <div class="item-name">{{ item.spuName }}</div>
                  <div class="item-sku">{{ item.skuTitle }} x{{ item.quantity }}</div>
                </div>
              </div>
              <div v-if="row.orderItems.length > 2" class="more-items">+{{ row.orderItems.length - 2 }}件商品</div>
            </div>
            <span v-else class="text-muted">无商品信息</span>
          </template>
        </el-table-column>
        <el-table-column label="收货信息" min-width="200">
          <template #default="{ row }">
            <div class="receiver-info">
              <div class="receiver-name">{{ row.receiverName || '-' }}</div>
              <div class="receiver-phone">{{ row.receiverPhone || '-' }}</div>
              <div class="receiver-address">{{ row.receiverAddress || '-' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" min-width="100" align="center">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="orderStatusType(row.status)" size="small">{{ orderStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="120">
          <template #default="{ row }">
            <span class="text-muted">{{ row.remark || '无' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template #default="{ row }">
            <span class="text-muted">{{ row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" plain @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" plain @click="handleCancel(row)">取消</el-button>
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

    <el-dialog v-model="detailVisible" title="订单详情" width="750px" :close-on-click-modal="false">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="orderStatusType(currentOrder.status)">{{ orderStatusLabel(currentOrder.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="订单金额"><span class="price-text">¥{{ currentOrder.totalAmount }}</span></el-descriptions-item>
          <el-descriptions-item label="实付金额"><span class="price-text">¥{{ currentOrder.payAmount }}</span></el-descriptions-item>
          <el-descriptions-item label="运费">¥{{ currentOrder.freightAmount }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.payType === 0 ? '微信' : '支付宝' }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="收货电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentOrder.payTime || '未支付' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ currentOrder.deliverTime || '未发货' }}</el-descriptions-item>
          <el-descriptions-item label="收货时间">{{ currentOrder.receiveTime || '未收货' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-items-section" v-if="detailItems.length > 0">
          <div class="detail-items-title">订单商品</div>
          <el-table :data="detailItems" stripe style="width: 100%">
            <el-table-column label="商品图片" width="80" align="center">
              <template #default="{ row }">
                <el-image :src="row.skuImage" style="width: 50px; height: 50px; border-radius: 6px" fit="cover">
                  <template #error><div class="image-error">无图</div></template>
                </el-image>
              </template>
            </el-table-column>
            <el-table-column prop="spuName" label="商品名称" min-width="150" />
            <el-table-column prop="skuTitle" label="规格" width="120" />
            <el-table-column label="单价" width="100" align="center">
              <template #default="{ row }">
                <span class="price-text">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="小计" width="100" align="center">
              <template #default="{ row }">
                <span class="price-text">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const currentOrder = ref(null)
const detailItems = ref([])
const query = reactive({ status: '', keyword: '', pageNum: 1, pageSize: 10 })

function orderStatusType(s) { return { 0:'warning', 1:'primary', 2:'info', 3:'success', 4:'danger' }[s] || 'info' }
function orderStatusLabel(s) { return { 0:'待付款', 1:'已付款', 2:'已发货', 3:'已完成', 4:'已取消' }[s] || '未知' }

function resetQuery() {
  query.status = ''
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/admin/order/list', { params: query })
    const orders = res.data?.records || []
    total.value = res.data?.total || 0
    
    const ordersWithItems = await Promise.all(orders.map(async (order) => {
      try {
        const itemsRes = await request.get(`/admin/order/${order.id}/items`)
        return { ...order, orderItems: itemsRes.data || [] }
      } catch {
        return { ...order, orderItems: [] }
      }
    }))
    
    tableData.value = ordersWithItems
  } catch { 
    tableData.value = [] 
  } finally { 
    loading.value = false 
  }
}

async function handleShip(row) {
  await ElMessageBox.confirm('确认发货？', '提示', { type: 'info' })
  try { await request.put(`/admin/order/${row.id}/deliver`); ElMessage.success('发货成功'); loadData() } catch {}
}

async function handleCancel(row) {
  await ElMessageBox.confirm('确认取消订单？', '提示', { type: 'warning' })
  try { await request.delete(`/admin/order/${row.id}`); ElMessage.success('已取消'); loadData() } catch {}
}

async function handleDetail(row) {
  try {
    const res = await request.get(`/admin/order/${row.id}`)
    currentOrder.value = res.data
    const itemsRes = await request.get(`/admin/order/${row.id}/items`)
    detailItems.value = itemsRes.data || []
    detailVisible.value = true
  } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.order-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--border);
}

.order-items-cell {
  padding: 4px 0;
}

.order-item-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.order-item-row:last-child {
  margin-bottom: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 13px;
  color: var(--text-primary);
  line-height: 1.4;
}

.item-sku {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
}

.more-items {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.text-muted {
  color: var(--text-muted);
  font-size: 12px;
}

.receiver-info {
  line-height: 1.5;
}

.receiver-name {
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.receiver-phone, .receiver-address {
  font-size: 12px;
  color: var(--text-muted);
}

.receiver-address {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.price-text {
  color: var(--accent);
  font-weight: 600;
}

.image-error {
  width: 50px;
  height: 50px;
  background: var(--bg-page);
  border-radius: 6px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--text-muted);
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border);
}

.detail-items-section {
  margin-top: 24px;
}

.detail-items-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
</style>
