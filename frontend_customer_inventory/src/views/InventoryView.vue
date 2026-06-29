<template>
  <div class="page">
    <div class="page-heading">
      <h1 class="page-title">库存管理</h1>
      <el-button type="primary" :icon="Plus" :disabled="isReadonly" @click="openDialog()">新增库存</el-button>
    </div>

    <div class="panel">
      <div class="toolbar">
        <div class="search-form">
          <el-input v-model="query.name" placeholder="物品名称" clearable style="width: 180px" />
          <el-input v-model="query.category" placeholder="分类" clearable style="width: 160px" />
          <el-select v-model="query.status" placeholder="状态" clearable style="width: 130px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="load">查询</el-button>
          <el-button :icon="Refresh" @click="reset">重置</el-button>
        </div>
      </div>

      <el-table :data="list" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="物品名称" min-width="150" />
        <el-table-column prop="category" label="分类" min-width="110" />
        <el-table-column label="当前库存" min-width="130">
          <template #default="{ row }">
            <span class="stock-cell">
              {{ formatNumber(row.quantity) }} {{ row.unit || '' }}
              <el-tag v-if="isLowStock(row)" type="warning">低库存</el-tag>
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" min-width="110">
          <template #default="{ row }">{{ formatNumber(row.safetyStock) }}</template>
        </el-table-column>
        <el-table-column prop="costPrice" label="成本价" min-width="110">
          <template #default="{ row }">￥{{ formatNumber(row.costPrice) }}</template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'info' : 'success'">{{ row.status === 0 ? '停用' : '启用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="170" show-overflow-tooltip />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="danger" :disabled="isReadonly" @click="remove(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @current-change="load"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑库存' : '新增库存'" width="620px">
      <el-form :model="form" label-width="92px">
        <el-form-item label="物品名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="form.category" placeholder="如：耗材、产品、工具" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如：盒、瓶、个" />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="form.quantity" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="安全库存">
          <el-input-number v-model="form.safetyStock" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="成本价">
          <el-input-number v-model="form.costPrice" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="form.supplier" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
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
import { InventoryItem, inventoryApi } from '@/api/modules'

const query = reactive<{ page: number; pageSize: number; name: string; category: string; status?: number }>({
  page: 1,
  pageSize: 10,
  name: '',
  category: '',
  status: undefined
})
const list = ref<InventoryItem[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isReadonly = computed(() => localStorage.getItem('store_admin_role') === 'readonly')

const emptyInventory = (): InventoryItem => ({
  id: undefined,
  name: '',
  category: '',
  unit: '',
  quantity: 0,
  safetyStock: 0,
  costPrice: 0,
  supplier: '',
  status: 1,
  remark: ''
})

const form = reactive<InventoryItem>(emptyInventory())

const load = async () => {
  const data = await inventoryApi.page(query as unknown as Record<string, unknown>)
  list.value = data.records || []
  total.value = data.total || 0
}

const reset = () => {
  Object.assign(query, { page: 1, pageSize: 10, name: '', category: '', status: undefined })
  load()
}

const handleSizeChange = () => {
  query.page = 1
  load()
}

const openDialog = (row?: InventoryItem) => {
  if (isReadonly.value && !row) return
  Object.assign(form, row || emptyInventory())
  dialogVisible.value = true
}

const submit = async () => {
  if (isReadonly.value) {
    ElMessage.warning('当前账号为只读账号，不能修改数据')
    return
  }
  if (!form.name?.trim()) {
    ElMessage.warning('请输入物品名称')
    return
  }
  if (form.id) {
    await inventoryApi.update(form)
  } else {
    await inventoryApi.save(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const remove = async (id?: number) => {
  if (!id) return
  if (isReadonly.value) {
    ElMessage.warning('当前账号为只读账号，不能修改数据')
    return
  }
  await ElMessageBox.confirm('确认删除该库存记录？', '提示', { type: 'warning' })
  await inventoryApi.remove(id)
  ElMessage.success('删除成功')
  load()
}

const isLowStock = (row: InventoryItem) => {
  const quantity = Number(row.quantity || 0)
  const safetyStock = Number(row.safetyStock || 0)
  return safetyStock > 0 && quantity <= safetyStock
}

const formatNumber = (value?: number) => Number(value || 0).toFixed(2)

onMounted(load)
</script>

<style scoped>
.stock-cell {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}
</style>
