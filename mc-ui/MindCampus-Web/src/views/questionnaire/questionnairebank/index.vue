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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionnairebankList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="题库题目ID" align="center" prop="bankId" />
      <el-table-column label="题目类型" align="center" prop="type">
        <template #default="scope">
          <dict-tag :options="question_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="题干内容" align="center" prop="content" />
      <el-table-column label="选择题选项" align="center" prop="options" />
      <el-table-column label="标准答案" align="center" prop="standardAnswer" />
      <el-table-column label="分值" align="center" prop="score" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
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

          <el-form-item label="标准答案" prop="standardAnswer">
            <el-select v-model="form.standardAnswer" placeholder="请选择正确答案" clearable>
              <el-option v-for="(option, index) in form.optionList" :key="index"
                :label="String.fromCharCode(65 + index) + '. ' + option" :value="String.fromCharCode(65 + index)"
                :disabled="!option || option.trim() === ''" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="分值" prop="score">
          <el-input-number v-model="form.score" :min="0" :max="100" placeholder="请输入分值" />
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

<script setup name="Questionnairebank">
import {
  addQuestionnairebank,
  delQuestionnairebank,
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

const data = reactive({
  form: {
    optionList: ['', '', '', ''] // 初始化选项列表，固定为4个空选项
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
    standardAnswer: [
      { required: true, message: "标准答案不能为空", trigger: "blur" }
    ],
    score: [
      { required: true, message: "分值不能为空", trigger: "blur" }
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

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    bankId: null,
    type: null,
    content: null,
    options: null,
    optionList: ['', '', '', ''],
    standardAnswer: null,
    score: null,
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

// 多选框选中数据
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

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _bankId = row.bankId || ids.value
  getQuestionnairebank(_bankId).then(response => {
    form.value = response.data
    // 如果是选择题且有选项数据，解析选项
    if (form.value.type === 'choice' && form.value.options) {
      try {
        const parsedOptions = JSON.parse(form.value.options)
        // 如果是对象格式 {"A": "内容1", "B": "内容2", ...}
        if (typeof parsedOptions === 'object' && !Array.isArray(parsedOptions)) {
          form.value.optionList = ['', '', '', '']
          // 按A、B、C、D顺序填充选项内容
          Object.keys(parsedOptions).forEach(key => {
            const index = key.charCodeAt(0) - 65 // A=0, B=1, C=2, D=3
            if (index >= 0 && index < 4) {
              form.value.optionList[index] = parsedOptions[key]
            }
          })
        } else {
          // 如果是数组格式，保持兼容性
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

// 题目类型切换处理
function handleTypeChange(value) {
  if (value === 'choice') {
    // 如果是选择题，固定为A、B、C、D四个选项
    form.value.optionList = ['', '', '', '']
  } else {
    // 如果是简答题，清空选项和标准答案
    form.value.optionList = []
    form.value.standardAnswer = ''
  }
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["questionnairebankRef"].validate(valid => {
    if (valid) {
      // 处理选项数据 - 转换为对象格式
      if (form.value.type === 'choice' && form.value.optionList && form.value.optionList.length > 0) {
        const optionsObj = {}
        form.value.optionList.forEach((content, index) => {
          if (content && content.trim() !== '') {
            const key = String.fromCharCode(65 + index) // A, B, C, D
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

/* 按钮样式 */
.el-button--small {
  padding: 5px 10px;
  font-size: 12px;
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
