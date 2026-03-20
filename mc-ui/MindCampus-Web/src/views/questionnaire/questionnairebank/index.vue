<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="题目类型" prop="type" style="width: 200px">
        <el-select v-model="queryParams.type" placeholder="请选择题目类型" clearable>
          <el-option v-for="dict in question_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="题干内容" prop="content" style="width: 400px">
        <el-input v-model="queryParams.content" placeholder="请输入题干内容" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['questionnaire:questionnairebank:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['questionnaire:questionnairebank:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['questionnaire:questionnairebank:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['questionnaire:questionnairebank:export']">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="MagicStick" @click="openAiDialog"
          v-hasPermi="['questionnaire:questionnairebank:add']">AI 生成</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionnairebankList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="题目编号" align="center" prop="bankId" />
      <el-table-column label="题目类型" align="center" prop="type">
        <template #default="scope">
          <dict-tag :options="question_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="题干内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="选择题选项" align="center" prop="options" :show-overflow-tooltip="true" />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['questionnaire:questionnairebank:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['questionnaire:questionnairebank:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['questionnaire:questionnairebank:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改题库管理对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="questionnairebankRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型" @change="handleTypeChange">
            <el-option v-for="dict in question_type" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题干内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题干内容" />
        </el-form-item>

        <!-- 选择题选项设置 -->
        <div v-if="form.type === 'choice'">
          <el-form-item label="选项设置">
            <div v-for="(option, index) in form.optionList" :key="index" class="option-item">
              <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              <el-input v-model="form.optionList[index]"
                :placeholder="'请输入选项' + String.fromCharCode(65 + index) + '的内容'" />
            </div>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看题目详情对话框 -->
    <el-dialog title="查看题目详情" v-model="viewDialogVisible" width="600px" append-to-body>
      <el-form ref="viewFormRef" :model="viewData" label-width="100px">
        <el-form-item label="题目类型">
          <el-tag :type="viewData.type === 'choice' ? '' : 'info'">
            {{ viewData.type === 'choice' ? '选择题' : '简答题' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="题干内容">
          <div class="view-content">{{ viewData.content }}</div>
        </el-form-item>
        <el-form-item v-if="viewData.type === 'choice' && viewOptions.length > 0" label="选择题选项">
          <div v-for="item in viewOptions" :key="item.label" class="view-option">
            <span class="option-tag">{{ item.label }}.</span>
            <span>{{ item.content }}</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- AI 生成题目对话框 -->
    <el-dialog title="AI 生成心理测评题目" v-model="aiDialogVisible" width="680px" append-to-body>
      <!-- 生成条件区 -->
      <el-form ref="aiFormRef" :model="aiForm" label-width="100px" style="margin-bottom: 16px;">
        <el-form-item label="题目类型" prop="type">
          <el-radio-group v-model="aiForm.type" size="default">
            <el-radio value="choice">选择题</el-radio>
            <el-radio value="short_answer">简答题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题目描述" prop="description">
          <el-input v-model="aiForm.description" type="textarea" :rows="3"
            placeholder="请描述题目主题，例如：关于大学生学业压力的自我认知、关于抑郁情绪的筛查、关于人际关系适应性的评估等" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="MagicStick" :loading="aiGenerating" @click="handleAiGenerate">
            {{ aiGenerating ? 'AI 生成中...' : '生成题目' }}
          </el-button>
          <el-button icon="RefreshLeft" @click="resetAiForm" :disabled="aiGenerating">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- AI 生成结果预览区 -->
      <div v-if="aiPreviewResult.content" class="ai-preview-box">
        <div class="ai-preview-header">
          <el-icon color="#409EFF"><InfoFilled /></el-icon>
          <span style="margin-left: 6px; font-weight: 600;">题目预览</span>
          <el-tag size="small" style="margin-left: 10px;">
            {{ aiForm.type === 'choice' ? '选择题' : '简答题' }}
          </el-tag>
        </div>

        <!-- 题干 -->
        <div class="preview-content">
          <div class="preview-label">题干：</div>
          <div class="preview-text">{{ aiPreviewResult.content }}</div>
        </div>

        <!-- 选择题选项（后端 optionList + 前端从 options 字符串兜底解析） -->
        <div v-if="aiForm.type === 'choice' && aiPreviewDisplayOptions.length > 0" class="preview-content">
          <div class="preview-label">选项：</div>
          <div v-for="item in aiPreviewDisplayOptions" :key="item.label" class="preview-option">
            <span class="option-tag">{{ item.label }}.</span>
            <span>{{ item.content }}</span>
          </div>
        </div>
        <el-alert
          v-else-if="aiForm.type === 'choice' && aiPreviewResult.content"
          type="warning"
          :closable="false"
          show-icon
          title="未解析到选择题选项，请重新生成或改用手动新增"
          style="margin-top: 8px"
        />
      </div>

      <!-- 空状态提示 -->
      <el-empty v-else-if="!aiGenerating" description="填写题目类型和描述后，点击「生成题目」即可预览 AI 生成的题目" :image-size="80" />

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :disabled="!aiPreviewResult.content || aiGenerating"
            @click="handleAiConfirm">确认添加</el-button>
          <el-button @click="aiDialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Questionnairebank">
import {
  addQuestionnairebank,
  delQuestionnairebank,
  generateQuestion,
  getQuestionnairebank,
  listQuestionnairebank,
  updateQuestionnairebank
} from "@/api/questionnaire/questionnairebank"

const { proxy } = getCurrentInstance()
const { question_type } = proxy.useDict('question_type')

const questionnairebankList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 查看题目详情相关
const viewDialogVisible = ref(false)
const viewFormRef = ref(null)
const viewData = ref({})
const viewOptions = computed(() => {
  const r = viewData.value
  if (!r || r.type !== 'choice') return []
  if (Array.isArray(r.optionList) && r.optionList.length > 0) {
    return r.optionList
  }
  if (r.options && typeof r.options === 'string') {
    try {
      const o = JSON.parse(r.options)
      if (o && typeof o === 'object' && !Array.isArray(o)) {
        return Object.keys(o)
          .sort()
          .map((k) => ({ label: k, content: o[k] }))
      }
    } catch {
      return []
    }
  }
  return []
})

// AI 生成相关
const aiDialogVisible = ref(false)
const aiGenerating = ref(false)
const aiFormRef = ref(null)
const aiPreviewResult = ref({})
const aiForm = ref({
  type: 'choice',
  description: ''
})

/** 预览用选项列表：优先用接口返回的 optionList，否则从 options JSON 字符串解析 */
const aiPreviewDisplayOptions = computed(() => {
  const r = aiPreviewResult.value
  if (!r || aiForm.value.type !== 'choice') return []
  if (Array.isArray(r.optionList) && r.optionList.length > 0) {
    return r.optionList
  }
  if (r.options && typeof r.options === 'string') {
    try {
      const o = JSON.parse(r.options)
      if (o && typeof o === 'object' && !Array.isArray(o)) {
        return Object.keys(o)
          .sort()
          .map((k) => ({ label: k, content: o[k] }))
      }
    } catch {
      return []
    }
  }
  return []
})

const data = reactive({
  form: {
    optionList: ['', '', '', '']
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    type: null,
    content: null,
  },
  rules: {
    type: [
      { required: true, message: "题目类型不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "题干内容不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询题库管理列表 */
function getList() {
  loading.value = true
  listQuestionnairebank(queryParams.value).then(response => {
    questionnairebankList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    bankId: null,
    type: null,
    content: null,
    options: null,
    optionList: ['', '', '', ''],
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("questionnairebankRef")
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

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.bankId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加题库管理"
}

/** 查看按钮操作 */
function handleView(row) {
  const _bankId = row.bankId
  getQuestionnairebank(_bankId).then(response => {
    viewData.value = response.data || {}
    viewDialogVisible.value = true
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _bankId = row.bankId || ids.value
  getQuestionnairebank(_bankId).then(response => {
    form.value = response.data
    if (form.value.type === 'choice' && form.value.options) {
      try {
        const parsedOptions = JSON.parse(form.value.options)
        if (typeof parsedOptions === 'object' && !Array.isArray(parsedOptions)) {
          form.value.optionList = ['', '', '', '']
          Object.keys(parsedOptions).forEach(key => {
            const index = key.charCodeAt(0) - 65
            if (index >= 0 && index < 4) {
              form.value.optionList[index] = parsedOptions[key]
            }
          })
        } else {
          form.value.optionList = [...parsedOptions, '', '', '', ''].slice(0, 4)
        }
      } catch (e) {
        form.value.optionList = ['', '', '', '']
      }
    } else {
      form.value.optionList = ['', '', '', '']
    }
    open.value = true
    title.value = "修改题库管理"
  })
}

/** 题目类型切换处理 */
function handleTypeChange(value) {
  if (value === 'choice') {
    form.value.optionList = ['', '', '', '']
  } else {
    form.value.optionList = []
  }
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["questionnairebankRef"].validate(valid => {
    if (valid) {
      if (form.value.type === 'choice' && form.value.optionList && form.value.optionList.length > 0) {
        const optionsObj = {}
        form.value.optionList.forEach((content, index) => {
          if (content && content.trim() !== '') {
            const key = String.fromCharCode(65 + index)
            optionsObj[key] = content.trim()
          }
        })
        form.value.options = JSON.stringify(optionsObj)
      } else {
        form.value.options = null
      }

      if (form.value.bankId != null) {
        updateQuestionnairebank(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addQuestionnairebank(form.value).then(response => {
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
  const _bankIds = row.bankId || ids.value
  proxy.$modal.confirm('是否确认删除题库管理编号为"' + _bankIds + '"的数据项？').then(function () {
    return delQuestionnairebank(_bankIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('questionnaire/questionnairebank/export', {
    ...queryParams.value
  }, `questionnairebank_${new Date().getTime()}.xlsx`)
}

/** 打开 AI 生成对话框 */
function openAiDialog() {
  resetAiForm()
  aiDialogVisible.value = true
}

/** 重置 AI 表单 */
function resetAiForm() {
  aiForm.value = {
    type: 'choice',
    description: ''
  }
  aiPreviewResult.value = {}
  proxy.resetForm("aiFormRef")
}

/** AI 生成题目 */
async function handleAiGenerate() {
  if (!aiForm.value.description || aiForm.value.description.trim() === '') {
    proxy.$modal.msgWarning("请输入题目描述")
    return
  }
  aiGenerating.value = true
  aiPreviewResult.value = {}
  try {
    const response = await generateQuestion({
      type: aiForm.value.type,
      description: aiForm.value.description.trim()
    })
    if (response.code === 200) {
      aiPreviewResult.value = response.data || {}
      if (!aiPreviewResult.value.content) {
        proxy.$modal.msgWarning("AI 返回内容解析失败，请检查描述是否合理后重试")
      }
    } else {
      proxy.$modal.msgError(response.msg || "AI 生成失败")
    }
  } catch (error) {
    console.error('AI 生成题目失败:', error)
    proxy.$modal.msgError("AI 生成题目失败，请稍后重试")
  } finally {
    aiGenerating.value = false
  }
}

/** 确认添加（将预览题目入库） */
function handleAiConfirm() {
  if (!aiPreviewResult.value.content) {
    proxy.$modal.msgWarning("请先生成题目")
    return
  }
  if (aiForm.value.type === 'choice' && aiPreviewDisplayOptions.value.length === 0) {
    proxy.$modal.msgWarning("选择题缺少有效选项，请重新生成或手动新增")
    return
  }

  let optionsStr = aiPreviewResult.value.options || null
  if (aiForm.value.type === 'choice' && !optionsStr && aiPreviewDisplayOptions.value.length > 0) {
    const obj = {}
    aiPreviewDisplayOptions.value.forEach((it) => {
      obj[it.label] = it.content
    })
    optionsStr = JSON.stringify(obj)
  }

  const dataToInsert = {
    type: aiForm.value.type,
    content: aiPreviewResult.value.content,
    options: optionsStr
  }

  addQuestionnairebank(dataToInsert).then(response => {
    if (response.code === 200) {
      proxy.$modal.msgSuccess("题目添加成功")
      aiDialogVisible.value = false
      getList()
    } else {
      proxy.$modal.msgError(response.msg || "添加失败")
    }
  })
}

getList()
</script>

<style scoped>
/* 选项管理样式 */
.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.option-item .el-input {
  flex: 1;
}

.option-label {
  min-width: 20px;
  font-weight: bold;
  color: #409eff;
  font-size: 14px;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 20px;
}

/* 对话框样式 */
.el-dialog {
  border-radius: 8px;
}

/* 选项设置区域样式 */
.option-item {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.option-item:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
}

/* AI 预览区域 */
.ai-preview-box {
  background: #f8faff;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 16px 20px;
}

.ai-preview-header {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
  font-size: 14px;
  color: #303133;
}

.preview-content {
  margin-bottom: 12px;
}

.preview-label {
  font-weight: 600;
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}

.preview-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.7;
  background: #fff;
  padding: 10px 14px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.preview-option {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 14px;
  color: #303133;
  background: #fff;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.option-tag {
  font-weight: 700;
  color: #409eff;
  min-width: 16px;
}

/* 查看详情对话框样式 */
.view-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.7;
  background: #f5f7fa;
  padding: 10px 14px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  word-break: break-all;
}

.view-option {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
  font-size: 14px;
  color: #303133;
  background: #f5f7fa;
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .option-item {
    flex-direction: column;
    gap: 5px;
  }

  .option-item .el-input {
    width: 100%;
  }
}
</style>
