<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="问卷标题" prop="questionnaireTitle">
        <el-input v-model="queryParams.questionnaireTitle" placeholder="请输入问卷标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="题目类型" prop="type" style="width: 200px">
        <el-select v-model="queryParams.type" placeholder="请选择题目类型" clearable>
          <el-option label="选择题" value="choice" />
          <el-option label="简答题" value="short_answer" />
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
          v-hasPermi="['questionnaireAnswer:questionnaireAnswer:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['questionnaireAnswer:questionnaireAnswer:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['questionnaireAnswer:questionnaireAnswer:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['questionnaireAnswer:questionnaireAnswer:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionnaireAnswerList" @selection-change="handleSelectionChange"
      :span-method="objectSpanMethod" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="测评结果ID" align="center" prop="resultId" width="120" />
      <el-table-column label="学生姓名" align="center" prop="studentName" width="120">
        <template #default="scope">
          <span>{{ scope.row.studentName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="问卷标题" align="center" prop="questionnaireTitle" width="200" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.questionnaireTitle || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="答题记录ID" align="center" prop="answerId" width="100" />
      <el-table-column label="题目类型" align="center" prop="type" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 'choice'" type="success">选择题</el-tag>
          <el-tag v-else-if="scope.row.type === 'short_answer'" type="info">简答题</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="题干内容" align="center" prop="content" width="250" show-overflow-tooltip />
      <el-table-column label="用户答案" align="center" prop="userAnswer" width="200" show-overflow-tooltip />

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['questionnaireAnswer:questionnaireAnswer:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['questionnaireAnswer:questionnaireAnswer:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['questionnaireAnswer:questionnaireAnswer:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改心理测评答题记录对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="questionnaireAnswerRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="测评结果ID" prop="resultId">
          <el-input v-model="form.resultId" placeholder="请输入测评结果ID" :disabled="form.answerId != null" />
        </el-form-item>
        <el-form-item label="问卷ID" prop="questionnaireId">
          <el-input v-model="form.questionnaireId" placeholder="请输入问卷ID" :disabled="form.answerId != null" />
        </el-form-item>
        <el-form-item label="题目ID" prop="questionId">
          <el-input v-model="form.questionId" placeholder="请输入题目ID" :disabled="form.answerId != null" />
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型" :disabled="form.answerId != null">
            <el-option label="选择题" value="choice" />
            <el-option label="简答题" value="short_answer" />
          </el-select>
        </el-form-item>
        <el-form-item label="题干内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题干内容"
            :disabled="form.answerId != null" />
        </el-form-item>
        <el-form-item label="选择题选项" prop="options" v-if="form.type === 'choice'">
          <el-input v-model="form.options" type="textarea" :rows="4" placeholder="请输入选项（JSON格式）"
            :disabled="form.answerId != null" />
        </el-form-item>

        <el-form-item label="用户答案" prop="userAnswer">
          <el-input v-model="form.userAnswer" type="textarea" :rows="3" placeholder="请输入用户答案" />
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="答题记录详情" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="答题记录ID">{{ viewForm.answerId }}</el-descriptions-item>
        <el-descriptions-item label="测评结果ID">{{ viewForm.resultId }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ viewForm.studentName }}</el-descriptions-item>
        <el-descriptions-item label="问卷标题">{{ viewForm.questionnaireTitle }}</el-descriptions-item>
        <el-descriptions-item label="题目类型">
          <el-tag v-if="viewForm.type === 'choice'" type="success">选择题</el-tag>
          <el-tag v-else-if="viewForm.type === 'short_answer'" type="info">简答题</el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="题干内容" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="用户答案" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.userAnswer }}</div>
        </el-descriptions-item>

        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="QuestionnaireAnswer">
import {
  addQuestionnaireAnswer,
  delQuestionnaireAnswer,
  getQuestionnaireAnswer,
  listQuestionnaireAnswer,
  updateQuestionnaireAnswer
} from "@/api/questionnaire/questionnaireAnswer"

const { proxy } = getCurrentInstance()
const { is_correct } = proxy.useDict('is_correct')

const questionnaireAnswerList = ref([])
const open = ref(false)
const viewOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const viewForm = ref({})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    resultId: null,
    questionnaireId: null,
    questionnaireTitle: null,
    questionId: null,
    type: null,
    studentId: null,
    studentName: null,
  },
  rules: {
    resultId: [
      { required: true, message: "测评结果ID不能为空", trigger: "blur" }
    ],
    questionnaireId: [
      { required: true, message: "问卷ID不能为空", trigger: "blur" }
    ],
    questionId: [
      { required: true, message: "题目ID不能为空", trigger: "blur" }
    ],
    type: [
      { required: true, message: "题目类型不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "题干内容不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 用于存储合并单元格的信息
const spanArr = ref([])
let pos = 0

/** 查询心理测评答题记录列表 */
function getList() {
  loading.value = true
  listQuestionnaireAnswer(queryParams.value).then(response => {
    questionnaireAnswerList.value = response.rows
    total.value = response.total
    loading.value = false
    // 调试：检查学生姓名是否正确获取
    if (questionnaireAnswerList.value.length > 0) {
      console.log('答题记录数据示例:', questionnaireAnswerList.value[0])
      console.log('学生姓名:', questionnaireAnswerList.value[0].studentName)
    }
    // 计算合并单元格
    handleSpanArr()
  }).catch(error => {
    loading.value = false
    console.error('查询答题记录失败:', error)
  })
}

// 计算合并单元格
function handleSpanArr() {
  spanArr.value = []
  pos = 0
  if (questionnaireAnswerList.value.length === 0) {
    return
  }

  // 遍历数据，计算每个分组的大小
  for (let i = 0; i < questionnaireAnswerList.value.length; i++) {
    if (i === 0) {
      spanArr.value.push(1)
      pos = 0
    } else {
      const current = questionnaireAnswerList.value[i]
      const prev = questionnaireAnswerList.value[i - 1]

      // 判断是否属于同一组（result_id + questionnaire_id 相同）
      if (current.resultId === prev.resultId &&
        current.questionnaireId === prev.questionnaireId) {
        // 同一组，第一列合并，其他列不合并
        spanArr.value[pos] += 1
        spanArr.value.push(0)
      } else {
        // 新组
        spanArr.value.push(1)
        pos = i
      }
    }
  }
}

// 表格合并单元格方法
function objectSpanMethod({ row, column, rowIndex, columnIndex }) {
  // 需要合并的列：测评结果ID(1)、学生姓名(2)、问卷标题(3)
  if (columnIndex === 1 || columnIndex === 2 || columnIndex === 3) {
    const _row = spanArr.value[rowIndex]
    const _col = _row > 0 ? 1 : 0
    return {
      rowspan: _row,
      colspan: _col
    }
  }
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    answerId: null,
    resultId: null,
    questionnaireId: null,
    questionId: null,
    type: null,
    content: null,
    options: null,
    userAnswer: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("questionnaireAnswerRef")
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
  ids.value = selection.map(item => item.answerId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加心理测评答题记录"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _answerId = row.answerId || ids.value[0]
  getQuestionnaireAnswer(_answerId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改心理测评答题记录"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  const _answerId = row.answerId
  getQuestionnaireAnswer(_answerId).then(response => {
    viewForm.value = response.data
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["questionnaireAnswerRef"].validate(valid => {
    if (valid) {
      if (form.value.answerId != null) {
        updateQuestionnaireAnswer(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addQuestionnaireAnswer(form.value).then(response => {
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
  const _answerIds = row.answerId || ids.value
  proxy.$modal.confirm('是否确认删除心理测评答题记录编号为"' + _answerIds + '"的数据项？').then(function () {
    return delQuestionnaireAnswer(_answerIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('questionnaireAnswer/questionnaireAnswer/export', {
    ...queryParams.value
  }, `questionnaireAnswer_${new Date().getTime()}.xlsx`)
}

getList()
</script>
