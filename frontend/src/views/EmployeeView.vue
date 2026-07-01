<template>
  <div class="page">
    <h1 class="page-title">员工管理</h1>
    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.name" placeholder="姓名" clearable style="width: 180px" />
          <el-input v-model="query.phone" placeholder="手机号" clearable style="width: 180px" />
          <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" :disabled="isReadOnly" @click="openDialog()">新增员工</el-button>
      </div>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="150" />
        <el-table-column prop="gender" label="性别" width="90">
          <template #default="{ row }">{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="position" label="职位" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" :disabled="isReadOnly" @click="openDialog(row)">编辑</el-button>
              <el-button
                link
                :type="row.status === 1 ? 'danger' : 'success'"
                :icon="row.status === 1 ? CircleClose : CircleCheck"
                :disabled="isReadOnly"
                @click="toggleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :total="total" @change="load" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑员工' : '新增员工'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职位"><el-input v-model="form.position" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { CircleCheck, CircleClose, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { Employee, employeeApi } from '@/api/modules'
import { isReadOnlyAdmin } from '@/utils/auth'

const query = reactive({ page: 1, pageSize: 10, name: '', phone: '', status: undefined as number | undefined })
const list = ref<Employee[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<Employee>({ name: '', phone: '', gender: 1, position: '', status: 1, remark: '' })
const isReadOnly = computed(isReadOnlyAdmin)

const warnReadOnly = () => {
  ElMessage.warning('当前账号为只读账号，不能修改数据')
}

const load = async () => {
  const data = await employeeApi.page(query)
  list.value = data.records
  total.value = data.total
}

const reset = () => {
  Object.assign(query, { page: 1, pageSize: 10, name: '', phone: '', status: undefined })
  load()
}

const openDialog = (row?: Employee) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  Object.assign(form, row || { id: undefined, name: '', phone: '', gender: 1, position: '', status: 1, remark: '' })
  dialogVisible.value = true
}

const submit = async () => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (form.id) {
    await employeeApi.update(form)
  } else {
    await employeeApi.save(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const toggleStatus = async (row: Employee) => {
  if (isReadOnly.value) {
    warnReadOnly()
    return
  }
  if (!row.id) return
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 1 ? '启用' : '禁用'
  await ElMessageBox.confirm(`确认${actionText}该员工？`, '提示', { type: 'warning' })
  await employeeApi.update({ ...row, status: nextStatus })
  ElMessage.success(`${actionText}成功`)
  load()
}

onMounted(load)
</script>
