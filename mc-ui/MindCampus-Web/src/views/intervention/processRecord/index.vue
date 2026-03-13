<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="处理状态" prop="status" style="width: 240px">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable>
          <el-option v-for="dict in record_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理结果" prop="processResult">
        <el-input v-model="queryParams.processResult" placeholder="请输入处理结果" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['intervention:process:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['intervention:process:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录编号" align="center" prop="recordId" width="80" />
      <el-table-column label="通知编号" align="center" prop="notificationId" width="80" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="通知内容" align="center" prop="notificationContent" :show-overflow-tooltip="true" width="200" />
      <el-table-column label="处理内容" align="center" prop="processContent" :show-overflow-tooltip="true" width="200" />
      <el-table-column label="处理状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="record_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="处理结果" align="center" prop="processResult" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="处理时间" align="center" prop="processTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.processTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['intervention:process:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['intervention:process:edit']">填写</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:process:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 查看对话框 -->
    <el-dialog title="查看干预处理记录" v-model="openView" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录编号">{{ form.recordId }}</el-descriptions-item>
        <el-descriptions-item label="通知编号">{{ form.notificationId }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ form.studentName }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <dict-tag :options="record_status" :value="form.status" />
        </el-descriptions-item>
        <el-descriptions-item label="通知内容" :span="2">{{ form.notificationContent }}</el-descriptions-item>
        <el-descriptions-item label="处理内容" :span="2">{{ form.processContent || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ form.processResult || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ parseTime(form.processTime) || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 填写对话框 -->
    <el-dialog title="填写处理记录" v-model="openEdit" width="600px" append-to-body>
      <el-form ref="processRecordRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="记录编号">
          <el-input v-model="form.recordId" disabled />
        </el-form-item>
        <el-form-item label="通知编号">
          <el-input v-model="form.notificationId" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="form.studentName" disabled />
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input v-model="form.notificationContent" type="textarea" :autosize="{ minRows: 3, maxRows: 10 }" disabled />
        </el-form-item>
        <el-form-item label="处理内容" prop="processContent">
          <el-input v-model="form.processContent" type="textarea" :rows="4" placeholder="请输入处理内容" />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in record_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果" prop="processResult">
          <el-input v-model="form.processResult" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="openEdit = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProcessRecord">
import { listProcessRecord, getProcessRecord, updateProcessRecord, delProcessRecord } from '@/api/intervention/processRecord'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()
const { record_status } = proxy.useDict('record_status')

const recordList = ref([])
const openView = ref(false)
const openEdit = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentName: null,
    status: null,
    processResult: null
  },
  rules: {
    processContent: [
      { required: true, message: '处理内容不能为空', trigger: 'blur' }
    ],
    processResult: [
      { required: true, message: '处理结果不能为空', trigger: 'blur' }
    ]
  }
})

const { form, queryParams, rules } = toRefs(data)

/** 查询干预处理记录列表 */
function getList() {
  loading.value = true
  listProcessRecord(queryParams.value).then(response => {
    recordList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.recordId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 查看按钮操作 */
function handleView(row) {
  getProcessRecord(row.recordId).then(response => {
    form.value = response.data
    openView.value = true
  })
}

/** 填写按钮操作 */
function handleUpdate(row) {
  getProcessRecord(row.recordId).then(response => {
    form.value = response.data
    openEdit.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["processRecordRef"].validate(valid => {
    if (valid) {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      const processTimeStr = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
      
      const data = {
        recordId: form.value.recordId,
        notificationId: form.value.notificationId,
        processContent: form.value.processContent,
        status: form.value.status,
        processResult: form.value.processResult,
        processTime: processTimeStr
      }
      updateProcessRecord(data).then(response => {
        proxy.$modal.msgSuccess("填写成功")
        openEdit.value = false
        getList()
      })
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const recordIds = row.recordId || ids.value
  proxy.$modal.confirm('是否确认删除干预处理记录编号为"' + recordIds + '"的数据项？').then(function () {
    return delProcessRecord(recordIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('intervention/process/export', {
    ...queryParams.value
  }, `intervention-process-${new Date().getTime()}.xlsx`)
}

getList()
</script>
