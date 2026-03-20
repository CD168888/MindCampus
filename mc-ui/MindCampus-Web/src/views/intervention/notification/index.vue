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
      <el-table-column label="通知编号" align="center" prop="notificationId" width="80" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="辅导员" align="center" prop="userName" />
      <el-table-column label="班级" align="center" prop="deptName" />
      <el-table-column label="风险等级" align="center" prop="riskLevel">
        <template #default="scope">
          <el-tag :type="scope.row.riskLevel === '高' ? 'danger' : scope.row.riskLevel === '中' ? 'warning' : 'success'">
            {{ formatRiskLevel(scope.row.riskLevel) }}
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewRecord(scope.row)" v-hasPermi="['intervention:notification:query']">查看记录</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:notification:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 查看记录对话框 -->
    <el-dialog title="干预处理记录" v-model="openRecord" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="通知ID">{{ recordForm.notificationId }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ recordForm.studentName }}</el-descriptions-item>
        <el-descriptions-item label="风险等级">
          <el-tag :type="recordForm.riskLevel === '高' ? 'danger' : recordForm.riskLevel === '中' ? 'warning' : 'success'">
            {{ formatRiskLevel(recordForm.riskLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ parseTime(recordForm.sendTime) }}</el-descriptions-item>
        <el-descriptions-item label="通知内容" :span="2">{{ recordForm.notificationContent }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">处理记录</el-divider>

      <el-form ref="recordFormRef" :model="recordForm" :rules="recordRules" label-width="100px">
        <el-form-item label="处理内容" prop="processContent">
          <el-input 
            v-model="recordForm.processContent" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入处理内容" 
            :disabled="recordForm.processStatus === '2'"
          />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="recordForm.status" :disabled="recordForm.processStatus === '2'">
            <el-radio v-for="dict in record_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果" prop="processResult">
          <el-input 
            v-model="recordForm.processResult" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入处理结果" 
            :disabled="recordForm.processStatus === '2'"
          />
        </el-form-item>
        <el-form-item label="处理时间" v-if="recordForm.processTime">
          <span>{{ parseTime(recordForm.processTime) }}</span>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRecordForm" :disabled="recordForm.processStatus === '2'">保存处理记录</el-button>
          <el-button @click="cancelRecord">关 闭</el-button>
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
            <el-tag type="danger">{{ formatRiskLevel(scope.row.riskLevel) }}</el-tag>
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
  updateNotification,
  viewRecord
} from '@/api/intervention/notification'
import {updateProcessRecord} from '@/api/intervention/processRecord'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import {parseTime} from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()
const { record_status } = proxy.useDict('record_status')

const notificationList = ref([])
const open = ref(false)
const openGenerate = ref(false)
const openRecord = ref(false)
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

// 记录表单
const recordForm = ref({})

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
  },
  recordRules: {
    processContent: [
      { required: true, message: '处理内容不能为空', trigger: 'blur' }
    ],
    processResult: [
      { required: true, message: '处理结果不能为空', trigger: 'blur' }
    ]
  }
})

const { form, queryParams, rules, recordRules } = toRefs(data)

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

/** 格式化风险等级展示：低→低风险 中→中度风险 高→高风险 */
function formatRiskLevel(level) {
  const map = { '低': '低风险', '中': '中度风险', '高': '高风险' }
  return map[level] || level || '-'
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

/** 查看记录按钮操作 */
function handleViewRecord(row) {
  viewRecord(row.notificationId).then(response => {
    recordForm.value = {
      recordId: response.data?.recordId,
      notificationId: row.notificationId,
      studentName: row.studentName,
      riskLevel: row.riskLevel,
      sendTime: row.sendTime,
      notificationContent: row.notificationContent,
      processContent: response.data?.processContent || '',
      status: response.data?.status || '0',
      processResult: response.data?.processResult || '',
      processTime: response.data?.processTime,
      processStatus: row.processStatus
    }
    openRecord.value = true
    getList()
  })
}

/** 提交处理记录 */
function submitRecordForm() {
  proxy.$refs['recordFormRef'].validate(valid => {
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
        recordId: recordForm.value.recordId,
        notificationId: recordForm.value.notificationId,
        processContent: recordForm.value.processContent,
        status: recordForm.value.status,
        processResult: recordForm.value.processResult,
        processTime: processTimeStr
      }
      updateProcessRecord(data).then(() => {
        proxy.$modal.msgSuccess('保存成功')
        openRecord.value = false
        getList()
      })
    }
  })
}

/** 取消记录对话框 */
function cancelRecord() {
  openRecord.value = false
  recordForm.value = {}
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
