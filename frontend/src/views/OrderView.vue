<template>
  <div class="page">
    <h1 class="page-title">订单管理</h1>
    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.orderNo" placeholder="订单号" clearable style="width: 210px" />
          <el-input v-model="query.customerName" placeholder="客户姓名" clearable style="width: 160px" />
          <el-select v-model="query.paymentMethod" placeholder="支付方式" clearable style="width: 130px">
            <el-option v-for="item in paymentMethods" :key="item" :label="item" :value="item" />
          </el-select>
          <el-select v-model="query.debtStatus" placeholder="欠款状态" clearable style="width: 140px">
            <el-option label="无欠款" :value="0" />
            <el-option label="分期中" :value="1" />
            <el-option label="已结清" :value="2" />
          </el-select>
          <el-select v-model="query.payStatus" placeholder="支付状态" clearable style="width: 140px">
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
          </el-select>
          <el-select v-model="query.orderStatus" placeholder="订单状态" clearable style="width: 140px">
            <el-option label="待服务" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
          <el-date-picker v-model="dateRange" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" />
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <div class="table-actions">
          <el-button :icon="Download" :loading="exporting" @click="handleExport">导出订单</el-button>
          <el-button type="primary" :icon="Plus" :disabled="isReadOnly" @click="openDialog">新增订单</el-button>
        </div>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="customerName" label="客户" min-width="110" />
        <el-table-column prop="serviceItemName" label="主项目" min-width="140" />
        <el-table-column prop="cardType" label="卡类型" width="90" />
        <el-table-column prop="originalAmount" label="原价" width="105">
          <template #default="{ row }">{{ money(row.originalAmount || row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠" width="100">
          <template #default="{ row }">{{ money(row.discountAmount) }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="应收" width="105">
          <template #default="{ row }">{{ money(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已收" width="105">
          <template #default="{ row }">{{ money(row.paidAmount) }}</template>
        </el-table-column>
        <el-table-column prop="debtAmount" label="欠款" width="105">
          <template #default="{ row }">
            <span :class="{ 'debt-text': Number(row.debtAmount || 0) > 0 }">{{ money(row.debtAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="monthlyPayment" label="月付计划" width="110">
          <template #default="{ row }">{{ money(row.monthlyPayment) }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="debtStatus" label="欠款状态" width="105">
          <template #default="{ row }">
            <el-tag :type="debtTag(row.debtStatus)">{{ debtStatus(row.debtStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付" width="95">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : 'info'">{{ payStatus(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.orderStatus === 1 ? 'success' : row.orderStatus === 2 ? 'info' : 'warning'">{{ orderStatus(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" :disabled="isReadOnly || row.orderStatus === 2" @click="openPayment(row)">收款</el-button>
              <el-button link type="success" :disabled="isReadOnly || row.payStatus !== 1 || row.orderStatus !== 0" @click="complete(row.id)">完成</el-button>
              <el-button link type="danger" :disabled="isReadOnly || row.orderStatus === 1 || row.orderStatus === 2" @click="cancel(row.id)">取消</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :total="total" @change="load" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="新增订单 / 次卡购买" width="680px">
      <el-form :model="form" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="预约ID"><el-input-number v-model="form.appointmentId" :min="1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID"><el-input-number v-model="form.customerId" :min="1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主项目ID"><el-input-number v-model="form.serviceItemId" :min="1" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="卡类型">
              <el-select v-model="form.cardType">
                <el-option label="次卡" value="次卡" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价/卡费"><el-input-number v-model="form.originalAmount" :min="0" :precision="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优惠金额"><el-input-number v-model="form.discountAmount" :min="0" :precision="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应收金额">
              <el-input :model-value="money(formReceivable)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已收金额"><el-input-number v-model="form.paidAmount" :min="0" :max="formReceivable" :precision="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="欠款金额">
              <el-input :model-value="money(formDebt)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="每月计划"><el-input-number v-model="form.monthlyPayment" :min="0" :precision="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式">
              <el-select v-model="form.paymentMethod">
                <el-option v-for="item in paymentMethods" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" placeholder="可记录朋友代用、转交、优惠说明等" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="isReadOnly" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="paymentVisible" title="收款 / 更新欠款" width="480px">
      <el-form :model="paymentForm" label-width="110px">
        <el-form-item label="应收金额"><el-input :model-value="money(paymentForm.amount)" disabled /></el-form-item>
        <el-form-item label="累计已收"><el-input-number v-model="paymentForm.paidAmount" :min="0" :max="paymentForm.amount" :precision="2" /></el-form-item>
        <el-form-item label="剩余欠款"><el-input :model-value="money(paymentDebt)" disabled /></el-form-item>
        <el-form-item label="每月计划"><el-input-number v-model="paymentForm.monthlyPayment" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="paymentForm.paymentMethod">
            <el-option v-for="item in paymentMethods" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button type="primary" :disabled="isReadOnly" @click="submitPayment">保存收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { OrderInfo, orderApi } from '@/api/modules'
import { isReadOnlyAdmin } from '@/utils/auth'

const paymentMethods = ['微信', '支付宝', '现金', '次卡']

const query = reactive({
  page: 1,
  pageSize: 10,
  orderNo: '',
  customerName: '',
  paymentMethod: '',
  payStatus: undefined as number | undefined,
  debtStatus: undefined as number | undefined,
  orderStatus: undefined as number | undefined,
  beginTime: '',
  endTime: ''
})
const dateRange = ref<[string, string] | undefined>()
const list = ref<OrderInfo[]>([])
const total = ref(0)
const exporting = ref(false)
const dialogVisible = ref(false)
const paymentVisible = ref(false)
const isReadOnly = computed(isReadOnlyAdmin)
const form = reactive<OrderInfo>({
  appointmentId: undefined,
  customerId: undefined,
  serviceItemId: undefined,
  cardType: '次卡',
  originalAmount: 0,
  discountAmount: 0,
  amount: 0,
  paidAmount: 0,
  monthlyPayment: 0,
  paymentMethod: '现金',
  remark: ''
})
const paymentForm = reactive({
  id: undefined as number | undefined,
  amount: 0,
  paidAmount: 0,
  monthlyPayment: 0,
  paymentMethod: '现金'
})

const requestQuery = computed(() => ({
  ...query,
  paymentMethod: query.paymentMethod || undefined,
  beginTime: dateRange.value?.[0] || '',
  endTime: dateRange.value?.[1] || ''
}))

const formReceivable = computed(() => Math.max(Number(form.originalAmount || 0) - Number(form.discountAmount || 0), 0))
const formDebt = computed(() => Math.max(formReceivable.value - Number(form.paidAmount || 0), 0))
const paymentDebt = computed(() => Math.max(Number(paymentForm.amount || 0) - Number(paymentForm.paidAmount || 0), 0))

const money = (value?: number) => `¥${Number(value || 0).toFixed(2)}`
const payStatus = (status?: number) => status === 1 ? '已支付' : status === 2 ? '已退款' : '未支付'
const orderStatus = (status?: number) => status === 1 ? '已完成' : status === 2 ? '已取消' : '待服务'
const debtStatus = (status?: number) => status === 1 ? '分期中' : status === 2 ? '已结清' : '无欠款'
const debtTag = (status?: number) => status === 1 ? 'warning' : status === 2 ? 'success' : 'info'
const warnReadOnly = () => {
  ElMessage.warning('当前账号为只读账号，不能修改数据')
}

const load = async () => {
  const data = await orderApi.page(requestQuery.value)
  list.value = data.records
  total.value = data.total
}

const reset = () => {
  Object.assign(query, {
    page: 1,
    pageSize: 10,
    orderNo: '',
    customerName: '',
    paymentMethod: '',
    payStatus: undefined,
    debtStatus: undefined,
    orderStatus: undefined,
    beginTime: '',
    endTime: ''
  })
  dateRange.value = undefined
  load()
}

const openDialog = () => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  Object.assign(form, {
    appointmentId: undefined,
    customerId: undefined,
    serviceItemId: undefined,
    cardType: '次卡',
    originalAmount: 0,
    discountAmount: 0,
    amount: 0,
    paidAmount: 0,
    monthlyPayment: 0,
    paymentMethod: '现金',
    remark: ''
  })
  dialogVisible.value = true
}

const openPayment = (row: OrderInfo) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  Object.assign(paymentForm, {
    id: row.id,
    amount: Number(row.amount || 0),
    paidAmount: Number(row.paidAmount || 0),
    monthlyPayment: Number(row.monthlyPayment || 0),
    paymentMethod: row.paymentMethod || '现金'
  })
  paymentVisible.value = true
}

const submit = async () => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  await orderApi.save({
    ...form,
    amount: formReceivable.value
  })
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const submitPayment = async () => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (!paymentForm.id) return
  await orderApi.updatePayment(paymentForm.id, {
    paidAmount: paymentForm.paidAmount,
    monthlyPayment: paymentForm.monthlyPayment,
    paymentMethod: paymentForm.paymentMethod
  })
  ElMessage.success(paymentDebt.value > 0 ? '收款已记录，仍有欠款' : '收款已结清')
  paymentVisible.value = false
  load()
}

const complete = async (id?: number) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (!id) return
  await orderApi.complete(id)
  ElMessage.success('操作成功')
  load()
}

const cancel = async (id?: number) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (!id) return
  await ElMessageBox.confirm('确认取消该订单？', '提示', { type: 'warning' })
  await orderApi.cancel(id)
  ElMessage.success('操作成功')
  load()
}

const downloadBlob = (blob: Blob, fileName: string) => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
  window.URL.revokeObjectURL(url)
}

const handleExport = async () => {
  exporting.value = true
  try {
    const blob = await orderApi.exportOrders(requestQuery.value)
    const date = new Date().toISOString().slice(0, 10)
    downloadBlob(blob, `订单列表-${date}.xlsx`)
    ElMessage.success('导出成功')
  } finally {
    exporting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.debt-text {
  color: #c2410c;
  font-weight: 700;
}
</style>
