<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="辅导员" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入辅导员姓名" clearable @keyup.enter="handleQuery" />
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
          <el-option label="待处理" value="0" />
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
          v-hasPermi="['intervention:notification:add']">批量生成</el-button>
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
      <el-table-column label="通知ID" align="center" prop="notificationId" width="80" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="辅导员" align="center" prop="userName" />
      <el-table-column label="班级" align="center" prop="className" />
      <el-table-column label="风险等级" align="center" prop="riskLevel">
        <template #default="scope">
          <el-tag :type="scope.row.riskLevel === '高' ? 'danger' : scope.row.riskLevel === '中' ? 'warning' : 'success'">
            {{ scope.row.riskLevel || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通知类型" align="center" prop="notificationType" />
      <el-table-column label="通知内容" align="center" prop="notificationContent" :show-overflow-tooltip="true" width="300" />
      <el-table-column label="发送时间" align="center" prop="sendTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.sendTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="阅读状态" align="center" prop="readStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.readStatus === '0' ? 'info' : 'success'">
            {{ scope.row.readStatus === '0' ? '未读' : '已读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="processStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.processStatus === '0' ? 'info' : scope.row.processStatus === '1' ? 'warning' : 'success'">
            {{ scope.row.processStatus === '0' ? '待处理' : scope.row.processStatus === '1' ? '处理中' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
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
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="notificationRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" disabled />
        </el-form-item>
        <el-form-item label="辅导员" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" disabled />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" disabled />
        </el-form-item>
        <el-form-item label="通知类型" prop="notificationType">
          <el-input v-model="form.notificationType" placeholder="请输入通知类型" />
        </el-form-item>
        <el-form-item label="通知内容" prop="notificationContent">
          <el-input v-model="form.notificationContent" type="textarea" :rows="4" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="阅读状态" prop="readStatus">
          <el-radio-group v-model="form.readStatus">
            <el-radio label="0">未读</el-radio>
            <el-radio label="1">已读</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理状态" prop="processStatus">
          <el-radio-group v-model="form.processStatus">
            <el-radio label="0">待处理</el-radio>
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

    <!-- 生成通知对话框 - 选择高风险未通知的学生 -->
    <el-dialog title="生成干预通知" v-model="openGenerate" width="900px" append-to-body>
      <div style="margin-bottom: 15px;">
        <el-alert title="请选择需要生成干预通知的高风险评测结果" type="info" :closable="false" />
      </div>
      <el-table v-loading="highRiskLoading" :data="highRiskList" @selection-change="handleHighRiskSelectionChange" max-height="400">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="学生姓名" align="center" prop="studentName" />
        <el-table-column label="学号" align="center" prop="studentNo" />
        <el-table-column label="年级" align="center" prop="grade" />
        <el-table-column label="专业" align="center" prop="major" />
        <el-table-column label="班级" align="center" prop="className" />
        <el-table-column label="风险等级" align="center" prop="riskLevel" width="100">
          <template #default="scope">
            <el-tag type="danger">{{ scope.row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="问卷标题" align="center" prop="questionnaireTitle" :show-overflow-tooltip="true" />
        <el-table-column label="评测时间" align="center" prop="evaluationTime" width="160">
          <template #default="scope">
            <span>{{ parseTime(scope.row.evaluationTime) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="generateLoading" @click="handleGenerate">生成通知</el-button>
          <el-button @click="cancelGenerate">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Notification">
import {
  addNotification,
  delNotification,
  generateNotification,
  getNotification,
  listHighRiskUnnotified,
  listNotification,
  updateNotification
} from '@/api/intervention/notification'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import {parseTime} from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()

const notificationList = ref([])
const open = ref(false)
const openGenerate = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')

// 高风险未通知相关
const highRiskLoading = ref(false)
const highRiskList = ref([])
const highRiskSelected = ref([])
const generateLoading = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentName: null,
    userName: null,
    notificationType: null,
    readStatus: null,
    processStatus: null
  },
  rules: {
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
  proxy.resetForm('queryRef')
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.notificationId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 - 打开高风险未通知选择对话框 */
function handleAdd() {
  openGenerate.value = true
  getHighRiskList()
}

/** 获取高风险未通知列表 */
function getHighRiskList() {
  highRiskLoading.value = true
  highRiskSelected.value = []
  listHighRiskUnnotified().then(response => {
    highRiskList.value = response.data
    highRiskLoading.value = false
  })
}

/** 高风险选择变化 */
function handleHighRiskSelectionChange(selection) {
  highRiskSelected.value = selection
}

/** 生成通知 */
function handleGenerate() {
  if (highRiskSelected.value.length === 0) {
    proxy.$modal.msgWarning('请选择至少一条记录')
    return
  }
  generateLoading.value = true
  const resultIds = highRiskSelected.value.map(item => item.resultId)
  generateNotification(resultIds).then(() => {
    proxy.$modal.msgSuccess('通知生成成功')
    openGenerate.value = false
    getList()
  }).finally(() => {
    generateLoading.value = false
  })
}

/** 取消生成 */
function cancelGenerate() {
  openGenerate.value = false
  highRiskList.value = []
  highRiskSelected.value = []
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const notificationId = row.notificationId || ids.value
  getNotification(notificationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改干预通知'
  })
}

/** 查看按钮操作 */
function handleView(row) {
  reset()
  getNotification(row.notificationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '查看干预通知'
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['notificationRef'].validate(valid => {
    if (valid) {
      if (form.value.notificationId != null) {
        updateNotification(form.value).then(response => {
          proxy.$modal.msgSuccess('修改成功')
          open.value = false
          getList()
        })
      } else {
        addNotification(form.value).then(response => {
          proxy.$modal.msgSuccess('新增成功')
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
  proxy.resetForm('notificationRef')
}

/** 删除按钮操作 */
function handleDelete(row) {
  const notificationIds = row.notificationId || ids.value
  proxy.$modal.confirm('是否确认删除干预通知编号为"' + notificationIds + '"的数据项？').then(function () {
    return delNotification(notificationIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('intervention/notification/export', {
    ...queryParams.value
  }, `intervention-notification-${new Date().getTime()}.xlsx`)
}

getList()
</script>
