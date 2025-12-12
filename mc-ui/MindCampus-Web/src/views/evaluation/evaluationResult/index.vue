<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="问卷标题" prop="questionnaireTitle">
        <el-input v-model="queryParams.questionnaireTitle" placeholder="请输入问卷标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>

      <el-form-item label="AI分析状态" prop="aiStatus" style="width: 250px; white-space: nowrap;">
        <el-select v-model="queryParams.aiStatus" placeholder="请选择AI分析状态" clearable>
          <el-option v-for="dict in ai_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="已读标识" prop="readStatus" style="width: 240px">
        <el-select v-model="queryParams.readStatus" placeholder="请选择已读标识" clearable>
          <el-option v-for="dict in read_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="完成标识" prop="completionStatus" style="width: 240px">
        <el-select v-model="queryParams.completionStatus" placeholder="请选择完成标识" clearable>
          <el-option v-for="dict in completion_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['evaluation:evaluationResult:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['evaluation:evaluationResult:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['evaluation:evaluationResult:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['evaluation:evaluationResult:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="evaluationResultList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="测评结果ID" align="center" prop="resultId" />
      <el-table-column label="学生名称" align="center" prop="studentName" />
      <el-table-column label="问卷标题" align="center" prop="questionnaireTitle" />

      <el-table-column label="风险等级" align="center" prop="riskLevel">
        <template #default="scope">
          <dict-tag :options="risk_level" :value="scope.row.riskLevel" />
        </template>
      </el-table-column>
      <el-table-column label="AI分析状态" align="center" prop="aiStatus">
        <template #default="scope">
          <dict-tag :options="ai_status" :value="scope.row.aiStatus" />
        </template>
      </el-table-column>
      <el-table-column label="已读标识" align="center" prop="readStatus">
        <template #default="scope">
          <dict-tag :options="read_status" :value="scope.row.readStatus" />
        </template>
      </el-table-column>
      <el-table-column label="完成标识" align="center" prop="completionStatus">
        <template #default="scope">
          <dict-tag :options="completion_status" :value="scope.row.completionStatus" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewAiResult(scope.row)" v-if="scope.row.aiStatus === '1'">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['evaluation:evaluationResult:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['evaluation:evaluationResult:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改心理测评结果对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="evaluationResultRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生ID" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" clearable filterable>
            <el-option v-for="student in studentList" :key="student.studentId"
              :label="`${student.name} (${student.studentNo})`" :value="student.studentId" />
          </el-select>
        </el-form-item>
        <el-form-item label="问卷ID" prop="questionnaireId">
          <el-select v-model="form.questionnaireId" placeholder="请选择问卷" clearable filterable>
            <el-option v-for="questionnaire in questionnaireList" :key="questionnaire.questionnaireId"
              :label="questionnaire.title" :value="questionnaire.questionnaireId" />
          </el-select>
        </el-form-item>
        <!-- 编辑模式下显示这些字段 -->
        <template v-if="isEditMode">

          <el-form-item label="AI 分析结果" prop="aiAnalysis">
            <el-input v-model="form.aiAnalysis" placeholder="请输入AI 分析结果" />
          </el-form-item>
          <el-form-item label="AI分析状态" prop="aiStatus">
            <el-select v-model="form.aiStatus" placeholder="请选择AI分析状态">
              <el-option v-for="dict in ai_status" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="已读标识" prop="readStatus">
            <el-select v-model="form.readStatus" placeholder="请选择已读标识">
              <el-option v-for="dict in read_status" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="完成标识" prop="completionStatus">
            <el-select v-model="form.completionStatus" placeholder="请选择完成标识">
              <el-option v-for="dict in completion_status" :key="dict.value" :label="dict.label"
                :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
      <!-- AI分析结果查看弹窗 -->
      <el-dialog
        v-model="aiResultDialogVisible"
        title="AI分析结果详情"
        width="800px"
        append-to-body
      >
        <div class="ai-result-container">
          <div class="ai-result-header">
            <div class="total-score">
              <h3>总得分</h3>
              <div class="score-value">{{ aiResultData.total_score }}</div>
              <div class="risk-level">风险等级：{{ aiResultData.risk_level }}</div>
            </div>
          </div>
          
          <div class="chart-container">
            <h3>各维度得分</h3>
            <div id="radar-chart" style="width: 100%; height: 400px;"></div>
          </div>
          
          <div class="analysis-content">
            <h3>主要问题</h3>
            <ul>
              <li v-for="(issue, index) in aiResultData.main_issues" :key="index">{{ issue }}</li>
            </ul>
            
            <h3>建议</h3>
            <ul>
              <li v-for="(suggestion, index) in aiResultData.suggestions" :key="index">{{ suggestion }}</li>
            </ul>
            
            <h3>详细分析</h3>
            <p>{{ aiResultData.detailed_analysis }}</p>
          </div>
        </div>
      </el-dialog>
    </div>
  </template>

<script setup name="EvaluationResult">
import { addEvaluationResult, delEvaluationResult, getEvaluationResult, listEvaluationResult, updateEvaluationResult } from "@/api/evaluation/evaluationResult"
import {listInfo} from "@/api/student/info"
import {listQuestionnaire} from "@/api/questionnaire/questionnaireinfo"
import * as echarts from "echarts"
import { nextTick } from "vue"

const { proxy } = getCurrentInstance()
const { completion_status, read_status, risk_level, ai_status } = proxy.useDict('completion_status', 'read_status', 'risk_level', 'ai_status')

const evaluationResultList = ref([])
const studentList = ref([])
const questionnaireList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const isEditMode = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentId: null,
    studentName: null,
    questionnaireId: null,
    questionnaireTitle: null,
    aiStatus: null,
    readStatus: null,
    completionStatus: null,
  },
  rules: {
    studentId: [
      { required: true, message: "学生ID不能为空", trigger: "blur" }
    ],
    questionnaireId: [
      { required: true, message: "问卷ID不能为空", trigger: "blur" }
    ],

    aiAnalysis: [
      { required: false, message: "AI 分析结果不能为空", trigger: "blur" }
    ],
    aiStatus: [
      { required: false, message: "AI分析状态不能为空", trigger: "change" }
    ],
    readStatus: [
      { required: false, message: "已读标识不能为空", trigger: "change" }
    ],
    completionStatus: [
      { required: false, message: "完成标识不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// AI分析结果弹窗相关
const aiResultDialogVisible = ref(false)
const aiResultData = ref({})
let radarChart = null

/** 查询心理测评结果列表 */
function getList() {
  loading.value = true
  listEvaluationResult(queryParams.value).then(response => {
    evaluationResultList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 查看AI分析结果 */
function handleViewAiResult(row) {
  if (row.aiAnalysis) {
    aiResultData.value = JSON.parse(row.aiAnalysis)
    aiResultDialogVisible.value = true
    // 等待DOM渲染完成后初始化图表
    nextTick(() => {
      initRadarChart()
    })
  }
}

/** 初始化雷达图 */
function initRadarChart() {
  const chartDom = document.getElementById('radar-chart')
  if (radarChart) {
    radarChart.dispose()
  }
  radarChart = echarts.init(chartDom)
  
  const indicators = [
    { name: '焦虑', max: 100 },
    { name: '抑郁', max: 100 },
    { name: '压力', max: 100 },
    { name: '社交功能', max: 100 },
    { name: '睡眠质量', max: 100 },
    { name: '情绪稳定性', max: 100 },
    { name: '自我效能感', max: 100 }
  ]
  
  const option = {
    tooltip: {},
    radar: {
      indicator: indicators,
      radius: '70%'
    },
    series: [{
      name: '心理评估得分',
      type: 'radar',
      data: [{
        value: [
          aiResultData.value.indicators.anxiety_score || 0,
          aiResultData.value.indicators.depression_score || 0,
          aiResultData.value.indicators.stress_score || 0,
          aiResultData.value.indicators.social_score || 0,
          aiResultData.value.indicators.sleep_score || 0,
          aiResultData.value.indicators.emotion_score || 0,
          aiResultData.value.indicators.self_efficacy_score || 0
        ],
        name: '得分'
      }]
    }]
  }
  
  option && radarChart.setOption(option)
}

// 监听窗口大小变化，调整图表大小
window.addEventListener('resize', () => {
  if (radarChart) {
    radarChart.resize()
  }
})

/** 获取学生列表 */
function getStudentList() {
  listInfo({ pageNum: 1, pageSize: 1000 }).then(response => {
    studentList.value = response.rows || []
  })
}

/** 获取问卷列表 */
function getQuestionnaireList() {
  listQuestionnaire({ pageNum: 1, pageSize: 1000 }).then(response => {
    questionnaireList.value = response.rows || []
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    resultId: null,
    studentId: null,
    questionnaireId: null,
    aiAnalysis: null,
    aiStatus: null,
    readStatus: null,
    completionStatus: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("evaluationResultRef")
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
  ids.value = selection.map(item => item.resultId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  isEditMode.value = false
  getStudentList()
  getQuestionnaireList()
  open.value = true
  title.value = "添加心理测评结果"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  isEditMode.value = true
  getStudentList()
  getQuestionnaireList()
  const _resultId = row.resultId || ids.value
  getEvaluationResult(_resultId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改心理测评结果"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["evaluationResultRef"].validate(valid => {
    if (valid) {
      if (form.value.resultId != null) {
        updateEvaluationResult(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addEvaluationResult(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _resultIds = row.resultId || ids.value
  proxy.$modal.confirm('是否确认删除心理测评结果编号为"' + _resultIds + '"的数据项？').then(function () {
    return delEvaluationResult(_resultIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('evaluation/evaluationResult/export', {
    ...queryParams.value
  }, `evaluationResult_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped>
.ai-result-container {
  padding: 20px;
}

.ai-result-header {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.total-score {
  text-align: center;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.risk-level {
  font-size: 18px;
  font-weight: bold;
  color: #F56C6C;
}

.chart-container {
  margin-bottom: 30px;
}

.analysis-content {
  margin-top: 30px;
}

.analysis-content h3 {
  font-size: 18px;
  font-weight: bold;
  margin: 20px 0 10px;
  color: #303133;
}

.analysis-content ul {
  margin-left: 20px;
  padding: 0;
}

.analysis-content li {
  margin: 8px 0;
  color: #606266;
  line-height: 1.5;
}

.analysis-content p {
  color: #606266;
  line-height: 1.8;
  text-indent: 2em;
}
</style>
