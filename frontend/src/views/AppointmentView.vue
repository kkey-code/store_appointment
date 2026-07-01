<template>
  <div class="page">
    <h1 class="page-title">预约管理</h1>
    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.customerName" placeholder="客户姓名" clearable style="width: 160px" />
          <el-input v-model="query.employeeName" placeholder="员工姓名" clearable style="width: 160px" />
          <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-date-picker v-model="dateRange" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" />
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增预约</el-button>
      </div>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="customerName" label="客户" min-width="120" />
        <el-table-column prop="customerPhone" label="客户电话" min-width="140" />
        <el-table-column prop="employeeName" label="员工" min-width="120" />
        <el-table-column prop="serviceItemName" label="服务项目" min-width="150" />
        <el-table-column prop="appointmentTime" label="预约时间" min-width="170" />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="appointmentTag(row.status)">{{ appointmentStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="success" :disabled="row.status !== 1" @click="complete(row.id)">完成</el-button>
              <el-button link type="danger" :disabled="row.status === 2 || row.status === 3" @click="cancel(row.id)">取消</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :total="total" @change="load" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑预约' : '新增预约'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户ID"><el-input-number v-model="form.customerId" :min="1" /></el-form-item>
        <el-form-item label="员工ID"><el-input-number v-model="form.employeeId" :min="1" /></el-form-item>
        <el-form-item label="主项目ID"><el-input-number v-model="form.serviceItemId" :min="1" /></el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="form.appointmentTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { Appointment, appointmentApi } from '@/api/modules'

const statusOptions = [
  { label: '待确认', value: 0 },
  { label: '已确认', value: 1 },
  { label: '已完成', value: 2 },
  { label: '已取消', value: 3 }
]

const query = reactive({ page: 1, pageSize: 10, customerName: '', employeeName: '', status: undefined as number | undefined, beginTime: '', endTime: '' })
const dateRange = ref<[string, string] | undefined>()
const list = ref<Appointment[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<Appointment>({ customerId: undefined, employeeId: undefined, serviceItemId: undefined, appointmentTime: '', status: 0, remark: '' })

const requestQuery = computed(() => ({
  ...query,
  beginTime: dateRange.value?.[0] || '',
  endTime: dateRange.value?.[1] || ''
}))

const appointmentStatus = (status?: number) => statusOptions.find((item) => item.value === status)?.label || '-'
const appointmentTag = (status?: number) => status === 2 ? 'success' : status === 3 ? 'info' : status === 1 ? 'warning' : 'primary'

const load = async () => {
  const data = await appointmentApi.page(requestQuery.value)
  list.value = data.records
  total.value = data.total
}

const reset = () => {
  Object.assign(query, { page: 1, pageSize: 10, customerName: '', employeeName: '', status: undefined, beginTime: '', endTime: '' })
  dateRange.value = undefined
  load()
}

const openDialog = (row?: Appointment) => {
  Object.assign(form, row || { id: undefined, customerId: undefined, employeeId: undefined, serviceItemId: undefined, appointmentTime: '', status: 0, remark: '' })
  dialogVisible.value = true
}

const submit = async () => {
  if (form.id) {
    await appointmentApi.update(form)
  } else {
    await appointmentApi.save(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const cancel = async (id?: number) => {
  if (!id) return
  await ElMessageBox.confirm('确认取消该预约？', '提示', { type: 'warning' })
  await appointmentApi.cancel(id)
  ElMessage.success('操作成功')
  load()
}

const complete = async (id?: number) => {
  if (!id) return
  await appointmentApi.complete(id)
  ElMessage.success('操作成功')
  load()
}

onMounted(load)
</script>
