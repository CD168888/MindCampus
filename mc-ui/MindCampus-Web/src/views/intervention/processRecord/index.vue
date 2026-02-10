<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="通知ID" prop="notificationId">
        <el-input v-model="queryParams.notificationId" placeholder="请输入通知ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['intervention:process:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['intervention:process:edit']">修改</el-button>
      </el-col>
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
      <el-table-column label="记录ID" align="center" prop="recordId" />
      <el-table-column label="通知ID" align="center" prop="notificationId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="处理内容" align="center" prop="processContent" :show-overflow-tooltip="true" />
      <el-table-column label="处理结果" align="center" prop="processResult" />
      <el-table-column label="处理时间" align="center" prop="processTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.processTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['intervention:process:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['intervention:process:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:process:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改干预处理记录对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="processRecordRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="通知ID" prop="notificationId">
          <el-input v-model="form.notificationId" placeholder="请输入通知ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="处理内容" prop="processContent">
          <el-input v-model="form.processContent" type="textarea" placeholder="请输入处理内容" />
        </el-form-item>
        <el-form-item label="处理结果" prop="processResult">
          <el-input v-model="form.processResult" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProcessRecord">
import { listProcessRecord, getProcessRecord, addProcessRecord, updateProcessRecord, delProcessRecord } from '@/api/intervention/processRecord'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import { parseTime } from '@/utils/ruoyi'
import { nextTick } from 'vue'

const { proxy } = getCurrentInstance()

const recordList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    notificationId: null,
    userId: null,
    processResult: null
  },
  rules: {
    notificationId: [
      { required: true, message: '通知ID不能为空', trigger: 'blur' }
    ],
    userId: [
      { required: true, message: '用户ID不能为空', trigger: 'blur' }
    ],
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

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加干预处理记录"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const recordId = row.recordId || ids.value
  getProcessRecord(recordId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改干预处理记录"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  reset()
  getProcessRecord(row.recordId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "查看干预处理记录"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["processRecordRef"].validate(valid => {
    if (valid) {
      if (form.value.recordId != null) {
        updateProcessRecord(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addProcessRecord(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 重置表单 */
function reset() {
  form.value = {
    recordId: null,
    notificationId: null,
    userId: null,
    processContent: null,
    processResult: null
  }
  proxy.resetForm("processRecordRef")
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
