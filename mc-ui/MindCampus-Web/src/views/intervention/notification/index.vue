<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生ID" prop="studentId">
        <el-input v-model="queryParams.studentId" placeholder="请输入学生ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="通知类型" prop="notificationType">
        <el-input v-model="queryParams.notificationType" placeholder="请输入通知类型" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="阅读状态" prop="readStatus" style="width: 240px">
        <el-select v-model="queryParams.readStatus" placeholder="请选择阅读状态" clearable>
          <el-option label="未读" value="0" />
          <el-option label="已读" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="processStatus" style="width: 240px">
        <el-select v-model="queryParams.processStatus" placeholder="请选择处理状态" clearable>
          <el-option label="未处理" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="已处理" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['intervention:notification:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['intervention:notification:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['intervention:notification:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['intervention:notification:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="通知ID" align="center" prop="notificationId" />
      <el-table-column label="学生ID" align="center" prop="studentId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="部门ID" align="center" prop="deptId" />
      <el-table-column label="通知类型" align="center" prop="notificationType" />
      <el-table-column label="通知内容" align="center" prop="notificationContent" :show-overflow-tooltip="true" />
      <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.sendTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="阅读状态" align="center" prop="readStatus">
        <template #default="scope">
          <el-tag :type="scope.row.readStatus === '0' ? 'info' : 'success'">
            {{ scope.row.readStatus === '0' ? '未读' : '已读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="processStatus">
        <template #default="scope">
          <el-tag :type="scope.row.processStatus === '0' ? 'info' : scope.row.processStatus === '1' ? 'warning' : 'success'">
            {{ scope.row.processStatus === '0' ? '未处理' : scope.row.processStatus === '1' ? '处理中' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['intervention:notification:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['intervention:notification:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:notification:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改干预通知对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="notificationRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="结果ID" prop="resultId">
          <el-input v-model="form.resultId" placeholder="请输入结果ID" />
        </el-form-item>
        <el-form-item label="学生ID" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="通知类型" prop="notificationType">
          <el-input v-model="form.notificationType" placeholder="请输入通知类型" />
        </el-form-item>
        <el-form-item label="通知内容" prop="notificationContent">
          <el-input v-model="form.notificationContent" type="textarea" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="阅读状态" prop="readStatus">
          <el-radio-group v-model="form.readStatus">
            <el-radio label="0">未读</el-radio>
            <el-radio label="1">已读</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理状态" prop="processStatus">
          <el-radio-group v-model="form.processStatus">
            <el-radio label="0">未处理</el-radio>
            <el-radio label="1">处理中</el-radio>
            <el-radio label="2">已处理</el-radio>
          </el-radio-group>
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

<script setup name="Notification">
import { listNotification, getNotification, addNotification, updateNotification, delNotification } from '@/api/intervention/notification'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import { parseTime } from '@/utils/ruoyi'
import { nextTick } from 'vue'

const { proxy } = getCurrentInstance()

const notificationList = ref([])
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
    studentId: null,
    notificationType: null,
    readStatus: null,
    processStatus: null
  },
  rules: {
    studentId: [
      { required: true, message: '学生ID不能为空', trigger: 'blur' }
    ],
    userId: [
      { required: true, message: '用户ID不能为空', trigger: 'blur' }
    ],
    notificationType: [
      { required: true, message: '通知类型不能为空', trigger: 'blur' }
    ],
    notificationContent: [
      { required: true, message: '通知内容不能为空', trigger: 'blur' }
    ]
  }
})

const { form, queryParams, rules } = toRefs(data)

/** 查询干预通知列表 */
function getList() {
  loading.value = true
  listNotification(queryParams.value).then(response => {
    notificationList.value = response.rows
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
  ids.value = selection.map(item => item.notificationId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加干预通知"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const notificationId = row.notificationId || ids.value
  getNotification(notificationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改干预通知"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  reset()
  getNotification(row.notificationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "查看干预通知"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["notificationRef"].validate(valid => {
    if (valid) {
      if (form.value.notificationId != null) {
        updateNotification(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addNotification(form.value).then(response => {
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
    notificationId: null,
    resultId: null,
    studentId: null,
    userId: null,
    deptId: null,
    notificationType: null,
    notificationContent: null,
    readStatus: '0',
    processStatus: '0'
  }
  proxy.resetForm("notificationRef")
}

/** 删除按钮操作 */
function handleDelete(row) {
  const notificationIds = row.notificationId || ids.value
  proxy.$modal.confirm('是否确认删除干预通知编号为"' + notificationIds + '"的数据项？').then(function () {
    return delNotification(notificationIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('intervention/notification/export', {
    ...queryParams.value
  }, `intervention-notification-${new Date().getTime()}.xlsx`)
}

getList()
</script>
