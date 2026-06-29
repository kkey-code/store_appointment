<template>
  <div class="page">
    <h1 class="page-title">服务项目</h1>
    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.name" placeholder="项目名称" clearable style="width: 200px" />
          <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增项目</el-button>
      </div>
      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="项目名称" min-width="160" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price || 0 }}</template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="120">
          <template #default="{ row }">{{ row.duration || 0 }} 分钟</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="220" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="190" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button
                link
                :type="row.status === 1 ? 'danger' : 'success'"
                :icon="row.status === 1 ? Bottom : Top"
                @click="toggleStatus(row)"
              >
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :total="total" @change="load" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑服务项目' : '新增服务项目'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="时长"><el-input-number v-model="form.duration" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bottom, Plus, Refresh, Search, Top } from '@element-plus/icons-vue'
import { ServiceItem, serviceItemApi } from '@/api/modules'

const query = reactive({ page: 1, pageSize: 10, name: '', status: undefined as number | undefined })
const list = ref<ServiceItem[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<ServiceItem>({ name: '', price: 0, duration: 60, description: '', status: 1 })

const load = async () => {
  const data = await serviceItemApi.page(query)
  list.value = data.records
  total.value = data.total
}

const reset = () => {
  Object.assign(query, { page: 1, pageSize: 10, name: '', status: undefined })
  load()
}

const openDialog = (row?: ServiceItem) => {
  Object.assign(form, row || { id: undefined, name: '', price: 0, duration: 60, description: '', status: 1 })
  dialogVisible.value = true
}

const submit = async () => {
  if (form.id) {
    await serviceItemApi.update(form)
  } else {
    await serviceItemApi.save(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const toggleStatus = async (row: ServiceItem) => {
  if (!row.id) return
  const nextStatus = row.status === 1 ? 0 : 1
  const actionText = nextStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确认${actionText}该服务项目？`, '提示', { type: 'warning' })
  await serviceItemApi.update({ ...row, status: nextStatus })
  ElMessage.success(`${actionText}成功`)
  load()
}

onMounted(load)
</script>
