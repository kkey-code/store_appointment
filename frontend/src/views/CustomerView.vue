<template>
  <div class="page">
    <h1 class="page-title">客户管理</h1>
    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.name" placeholder="姓名" clearable style="width: 180px" />
          <el-input v-model="query.phone" placeholder="手机号" clearable style="width: 180px" />
          <el-select v-model="query.level" placeholder="客户等级" clearable style="width: 150px">
            <el-option label="普通" value="普通" />
            <el-option label="VIP" value="VIP" />
            <el-option label="大客户" value="大客户" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <div class="table-actions">
          <el-button :icon="Download" :loading="exporting" @click="handleExport">导出客户</el-button>
          <el-button type="primary" :icon="Plus" :disabled="isReadOnly" @click="openDialog()">新增客户</el-button>
        </div>
      </div>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="150" />
        <el-table-column prop="gender" label="性别" width="90">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="birthday" label="生日" min-width="120" />
        <el-table-column prop="level" label="等级" width="100" />
        <el-table-column prop="source" label="来源" min-width="120" />
        <el-table-column prop="remark" label="备注" min-width="160" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" :disabled="isReadOnly" @click="openDialog(row)">编辑</el-button>
              <el-button link type="danger" :disabled="isReadOnly" @click="remove(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :total="total" @change="load" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑客户' : '新增客户'" width="540px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生日"><el-date-picker v-model="form.birthday" value-format="YYYY-MM-DD" type="date" /></el-form-item>
        <el-form-item label="等级">
          <el-select v-model="form.level" placeholder="请选择客户等级">
            <el-option label="普通" value="普通" />
            <el-option label="VIP" value="VIP" />
            <el-option label="大客户" value="大客户" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="form.source" placeholder="请选择客户来源" clearable>
            <el-option label="老客" value="老客" />
            <el-option label="抖音" value="抖音" />
            <el-option label="美团" value="美团" />
            <el-option label="特客" value="特客" />
            <el-option label="转介绍" value="转介绍" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="isReadOnly" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { Customer, customerApi } from '@/api/modules'
import { isReadOnlyAdmin } from '@/utils/auth'

const query = reactive({ page: 1, pageSize: 10, name: '', phone: '', level: '' })
const list = ref<Customer[]>([])
const total = ref(0)
const exporting = ref(false)
const dialogVisible = ref(false)
const form = reactive<Customer>({ name: '', phone: '', gender: 2, birthday: '', level: '普通', source: '老客', remark: '' })
const isReadOnly = computed(isReadOnlyAdmin)

const warnReadOnly = () => {
  ElMessage.warning('当前账号为只读账号，不能修改数据')
}

const load = async () => {
  const data = await customerApi.page(query)
  list.value = data.records
  total.value = data.total
}

const reset = () => {
  Object.assign(query, { page: 1, pageSize: 10, name: '', phone: '', level: '' })
  load()
}

const openDialog = (row?: Customer) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  Object.assign(form, row || { id: undefined, name: '', phone: '', gender: 2, birthday: '', level: '普通', source: '老客', remark: '' })
  dialogVisible.value = true
}

const submit = async () => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (form.id) {
    await customerApi.update(form)
  } else {
    await customerApi.save(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const remove = async (id?: number) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (!id) return
  await ElMessageBox.confirm('确认删除该客户？有关联预约或订单时会被拦截。', '提示', { type: 'warning' })
  await customerApi.remove(id)
  ElMessage.success('删除成功')
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
    const blob = await customerApi.exportCustomers(query)
    const date = new Date().toISOString().slice(0, 10)
    downloadBlob(blob, `客户列表-${date}.xlsx`)
    ElMessage.success('导出成功')
  } finally {
    exporting.value = false
  }
}

onMounted(load)
</script>
