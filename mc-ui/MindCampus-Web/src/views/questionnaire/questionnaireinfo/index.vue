<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="问卷标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入问卷标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="queryParams.description" placeholder="请输入描述" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in questionnaire_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型" prop="type" style="width: 200px">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option v-for="dict in questionnaire_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker v-model="queryParams.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择开始时间" clearable />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker v-model="queryParams.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="请选择结束时间" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['questionnaire:questionnaireinfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['questionnaire:questionnaireinfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['questionnaire:questionnaireinfo:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionnaireList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="问卷ID" align="center" prop="questionnaireId" width="80" />
      <el-table-column label="问卷标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="问卷描述" align="center" prop="description" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="questionnaire_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="type" width="120">
        <template #default="scope">
          <dict-tag :options="questionnaire_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="总分" align="center" prop="totalScore" width="80" />
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['questionnaire:questionnaireinfo:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['questionnaire:questionnaireinfo:edit']">修改</el-button>
          <el-button link type="primary" icon="Collection" @click="handleSend(scope.row)"
            v-hasPermi="['questionnaire:questionnaireinfo:send']">发送</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['questionnaire:questionnaireinfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 查看问卷对话框 -->
    <el-dialog :title="viewTitle" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="问卷标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="问卷描述">{{ viewForm.description }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="questionnaire_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          <dict-tag :options="questionnaire_type" :value="viewForm.type" />
        </el-descriptions-item>
        <el-descriptions-item label="总分">{{ viewForm.totalScore }} 分</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ parseTime(viewForm.startTime, '{y}-{m}-{d} {h}:{i}:{s}')
          }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ parseTime(viewForm.endTime, '{y}-{m}-{d} {h}:{i}:{s}')
          }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewForm.createTime, '{y}-{m}-{d} {h}:{i}:{s}')
          }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewForm.createBy }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>题目列表</el-divider>
      <div v-for="(q, index) in viewForm.questions" :key="index" class="mb10"
        style="border:1px solid #e4e7ed; border-radius:4px; padding:15px; margin-bottom:10px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:10px;">
          <h4 style="margin:0; color:#409eff;">题目 {{ index + 1 }}</h4>
          <el-tag :type="q.type === 'choice' ? 'success' : 'warning'">
            {{ q.type === 'choice' ? '选择题' : '简答题' }}
          </el-tag>
        </div>
        <p style="margin:10px 0; font-size:14px; line-height:1.5;">{{ q.content }}</p>

        <!-- 选择题选项显示 -->
        <div v-if="q.type === 'choice' && q.optionList && q.optionList.length > 0">
          <p style="margin:10px 0 5px 0; font-weight:bold; color:#606266;">选项：</p>
          <div style="margin-left:20px;">
            <div v-for="(opt, i) in q.optionList" :key="i"
              style="margin:5px 0; padding:5px; background:#f5f7fa; border-radius:3px;">
              {{ String.fromCharCode(65 + i) }}. {{ opt }}
            </div>
          </div>
          <p v-if="q.standardAnswer" style="margin:10px 0 5px 0; font-weight:bold; color:#e6a23c;">
            标准答案：{{ q.standardAnswer }}
          </p>
        </div>

        <div
          style="display:flex; justify-content:space-between; align-items:center; margin-top:10px; padding-top:10px; border-top:1px solid #e4e7ed;">
          <span style="color:#909399; font-size:12px;">分值：{{ q.score }} 分</span>
          <span style="color:#909399; font-size:12px;">排序：{{ q.orderNum }}</span>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改问卷对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="questionnaireRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="问卷标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入问卷标题" />
        </el-form-item>
        <el-form-item label="问卷描述" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入问卷描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in questionnaire_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="dict in questionnaire_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="总分">
          <el-input :value="calculateTotalScore()" disabled placeholder="根据题目分值自动计算" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择结束时间" />
        </el-form-item>

        <el-divider>题目列表</el-divider>
        <!-- 添加题目按钮 -->
        <div class="add-question-btn">
          <el-button type="primary" icon="Plus" @click="addQuestion">手动新增题目</el-button>
          <el-button type="success" icon="Collection" @click="openQuestionBankDialog">从题库选择</el-button>
        </div>

        <!-- 题目轮播图容器 -->
        <div class="question-carousel-container" v-if="form.questions.length > 0">
          <!-- 题目计数器 -->
          <div class="question-counter">
            <span>{{ currentQuestionIndex + 1 }} / {{ form.questions.length }}</span>
          </div>

          <!-- 轮播图主体 -->
          <div class="question-carousel" ref="questionCarouselRef">
            <div class="question-slide" v-for="(q, index) in form.questions" :key="index"
              :class="{ 'active': index === currentQuestionIndex }">
              <div class="question-card">
                <div class="question-header">
                  <h4>题目 {{ index + 1 }}</h4>
                  <el-button type="danger" size="small" icon="Delete" @click="removeQuestion(index)">删除题目</el-button>
                </div>

                <el-form-item label="题干内容">
                  <el-input v-model="q.content" placeholder="请输入题干内容" type="textarea" :rows="3" />
                </el-form-item>

                <el-form-item label="题目类型">
                  <el-select v-model="q.type" placeholder="请选择题目类型" @change="handleQuestionTypeChange(q)">
                    <el-option label="选择题" value="choice" />
                    <el-option label="简答题" value="short_answer" />
                  </el-select>
                </el-form-item>

                <!-- 选择题选项动态输入 -->
                <div v-if="q.type === 'choice'">
                  <el-form-item label="选项设置">
                    <div v-for="(opt, i) in q.optionList" :key="i" class="option-item">
                      <span class="option-label">{{ String.fromCharCode(65 + i) }}.</span>
                      <el-input v-model="q.optionList[i]"
                        :placeholder="'请输入选项' + String.fromCharCode(65 + i) + '的内容'" />
                    </div>
                  </el-form-item>

                  <el-form-item label="标准答案">
                    <el-select v-model="q.standardAnswer" placeholder="请选择正确答案" clearable>
                      <el-option v-for="(option, index) in q.optionList" :key="index"
                        :label="String.fromCharCode(65 + index) + '. ' + option"
                        :value="String.fromCharCode(65 + index)" :disabled="!option || option.trim() === ''" />
                    </el-select>
                  </el-form-item>
                </div>

                <el-form-item label="分值">
                  <el-input-number v-model="q.score" :min="0" :max="100" placeholder="请输入分值" />
                </el-form-item>
              </div>
            </div>
          </div>

          <!-- 轮播控制按钮 -->
          <div class="carousel-controls">
            <el-button :disabled="currentQuestionIndex === 0" @click="prevQuestion" icon="ArrowLeft">上一题</el-button>
            <el-button :disabled="currentQuestionIndex === form.questions.length - 1" @click="nextQuestion"
              icon="ArrowRight">下一题</el-button>
          </div>
        </div>

        <!-- 无题目时的提示 -->
        <div v-else class="no-questions">
          <el-empty description="暂无题目" />
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 题库选择对话框 -->
    <el-dialog title="从题库选择题目" v-model="questionBankOpen" width="1000px" append-to-body>
      <div class="question-bank-container">
        <!-- 搜索区域 -->
        <el-form :model="questionBankQuery" :inline="true" class="search-form">
          <el-form-item label="题目类型">
            <el-select v-model="questionBankQuery.type" placeholder="请选择题目类型" clearable>
              <el-option label="选择题" value="choice" />
              <el-option label="简答题" value="short_answer" />
            </el-select>
          </el-form-item>
          <el-form-item label="题干内容">
            <el-input v-model="questionBankQuery.content" placeholder="请输入题干内容" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="searchQuestionBank">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuestionBankQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 题库列表 -->
        <el-table v-loading="questionBankLoading" :data="questionBankList"
          @selection-change="handleQuestionBankSelectionChange" max-height="400">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="题目类型" align="center" prop="type" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.type === 'choice' ? 'success' : 'warning'">
                {{ scope.row.type === 'choice' ? '选择题' : '简答题' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="题干内容" align="center" prop="content" show-overflow-tooltip />
          <el-table-column label="分值" align="center" prop="score" width="80" />
        </el-table>

        <!-- 分页 -->
        <pagination v-show="questionBankTotal > 0" :total="questionBankTotal" v-model:page="questionBankQuery.pageNum"
          v-model:limit="questionBankQuery.pageSize" @pagination="searchQuestionBank" />
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="addSelectedQuestions">添加选中题目</el-button>
          <el-button @click="questionBankOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 发送问卷对话框 -->
    <el-dialog title="发送问卷" v-model="sendOpen" width="1000px" append-to-body>
      <div class="send-container">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="dept-panel">
              <div class="panel-header">
                <h4>选择部门</h4>
                <el-input v-model="deptName" placeholder="请输入部门名称" clearable prefix-icon="Search"
                  style="margin-bottom: 15px" />
              </div>
              <div class="dept-tree-container">
                <el-tree :data="deptOptions" :props="{ label: 'label', children: 'children' }"
                  :expand-on-click-node="false" :filter-node-method="filterNode" ref="deptTreeRef" node-key="id"
                  highlight-current default-expand-all @node-click="handleDeptNodeClick" />
              </div>
            </div>
          </el-col>
          <el-col :span="16">
            <div class="questionnaire-panel">
              <div class="panel-header">
                <h4>问卷信息</h4>
              </div>
              <div class="questionnaire-info">
                <el-descriptions :column="1" border>
                  <el-descriptions-item label="问卷标题">{{ sendForm.title }}</el-descriptions-item>
                  <el-descriptions-item label="问卷描述">{{ sendForm.description }}</el-descriptions-item>
                  <el-descriptions-item label="状态">
                    <dict-tag :options="questionnaire_status" :value="sendForm.status" />
                  </el-descriptions-item>
                  <el-descriptions-item label="类型">
                    <dict-tag :options="questionnaire_type" :value="sendForm.type" />
                  </el-descriptions-item>
                  <el-descriptions-item label="开始时间">{{ parseTime(sendForm.startTime, '{y}-{m}-{d} {h}:{i}:{s}')
                    }}</el-descriptions-item>
                  <el-descriptions-item label="结束时间">{{ parseTime(sendForm.endTime, '{y}-{m}-{d} {h}:{i}:{s}')
                    }}</el-descriptions-item>
                </el-descriptions>
                <div class="selected-dept-info" v-if="selectedDept">
                  <el-divider>
                    <el-icon>
                      <Check />
                    </el-icon>
                    已选择部门
                  </el-divider>
                  <div class="selected-dept-content">
                    <el-tag type="success" size="large" class="dept-tag">
                      <el-icon>
                        <OfficeBuilding />
                      </el-icon>
                      {{ selectedDept.label }}
                    </el-tag>
                    <div class="dept-details">
                      <p class="dept-id">部门ID: {{ selectedDept.id }}</p>
                      <p class="dept-path">{{ getDeptPath(selectedDept) }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmSend" :disabled="!selectedDept">确认发送</el-button>
          <el-button @click="sendOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="QuestionnaireInfo">
import {watch} from 'vue'
import {
  delQuestionnaire,
  getQuestions,
  listQuestionnaire,
  saveQuestionnaire,
  sendQuestionnaire
} from "@/api/questionnaire/questionnaireinfo"
import {listQuestionnairebank} from "@/api/questionnaire/questionnairebank"
import {deptTreeSelect} from "@/api/system/user"

const { proxy } = getCurrentInstance()
const { questionnaire_status, questionnaire_type } = proxy.useDict('questionnaire_status', 'questionnaire_type')

const questionnaireList = ref([])
const open = ref(false)
const viewOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const viewTitle = ref("")
const viewForm = ref({})

// 轮播图相关状态
const currentQuestionIndex = ref(0)
const questionCarouselRef = ref(null)

// 题库相关状态
const questionBankOpen = ref(false)
const questionBankList = ref([])
const questionBankLoading = ref(false)
const questionBankTotal = ref(0)
const selectedQuestions = ref([])
const questionBankQuery = ref({
  pageNum: 1,
  pageSize: 10,
  type: null,
  content: null
})

// 发送相关状态
const sendOpen = ref(false)
const sendForm = ref({})
const deptOptions = ref([])
const deptName = ref("")
const selectedDept = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: null,
    description: null,
    status: null,
    type: null,
    startTime: null,
    endTime: null,
  },
  rules: {
    title: [
      { required: true, message: "问卷标题不能为空", trigger: "blur" }
    ],
    description: [
      { required: true, message: "问卷描述不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
    type: [
      { required: true, message: "类型不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询问卷列表 */
function getList() {
  loading.value = true
  listQuestionnaire(queryParams.value).then(response => {
    questionnaireList.value = response.rows
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
    questionnaireId: null,
    title: null,
    description: null,
    status: null,
    type: null,
    startTime: null,
    endTime: null,
    questions: []
  }
  // 重置轮播图状态
  currentQuestionIndex.value = 0
  proxy.resetForm("questionnaireRef")
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
  ids.value = selection.map(item => item.questionnaireId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 查看按钮操作 */
function handleView(row) {
  viewForm.value = {
    questionnaireId: row.questionnaireId,
    title: row.title,
    description: row.description,
    status: row.status,
    type: row.type,
    totalScore: row.totalScore,
    startTime: row.startTime,
    endTime: row.endTime,
    createTime: row.createTime,
    createBy: row.createBy,
    questions: []
  }

  // 加载题目数据
  getQuestions(row.questionnaireId).then(res => {
    viewForm.value.questions = res.data || []
    // 初始化选项数组
    viewForm.value.questions.forEach(q => {
      if (q.type === 'choice' && !q.optionList) {
        if (q.options) {
          try {
            const parsedOptions = JSON.parse(q.options)
            if (typeof parsedOptions === 'object' && !Array.isArray(parsedOptions)) {
              // 对象格式 {"A": "内容1", "B": "内容2", ...}
              q.optionList = []
              Object.keys(parsedOptions).forEach(key => {
                const index = key.charCodeAt(0) - 65 // A=0, B=1, C=2, D=3
                if (index >= 0 && index < 4) {
                  q.optionList[index] = parsedOptions[key]
                }
              })
            } else {
              // 数组格式
              q.optionList = parsedOptions
            }
          } catch (e) {
            console.error('解析选项数据失败:', e)
            q.optionList = []
          }
        } else {
          q.optionList = []
        }
      }
    })
    viewOpen.value = true
    viewTitle.value = "查看问卷"
  }).catch(() => {
    // 如果加载题目失败，仍然打开查看对话框
    viewOpen.value = true
    viewTitle.value = "查看问卷"
  })
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加问卷"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _questionnaireId = row.questionnaireId || ids.value
  // 设置问卷基本信息
  form.value = {
    questionnaireId: row.questionnaireId,
    title: row.title,
    description: row.description,
    status: row.status,
    type: row.type,
    startTime: row.startTime,
    endTime: row.endTime,
    questions: []
  }

  // 加载题目数据
  getQuestions(_questionnaireId).then(res => {
    form.value.questions = res.data || []
    // 初始化选项数组
    form.value.questions.forEach(q => {
      if (q.type === 'choice' && !q.optionList) {
        if (q.options) {
          try {
            const parsedOptions = JSON.parse(q.options)
            if (typeof parsedOptions === 'object' && !Array.isArray(parsedOptions)) {
              // 对象格式 {"A": "内容1", "B": "内容2", ...}
              q.optionList = ['', '', '', '']
              Object.keys(parsedOptions).forEach(key => {
                const index = key.charCodeAt(0) - 65 // A=0, B=1, C=2, D=3
                if (index >= 0 && index < 4) {
                  q.optionList[index] = parsedOptions[key]
                }
              })
            } else {
              // 数组格式
              q.optionList = [...parsedOptions, '', '', '', ''].slice(0, 4)
            }
          } catch (e) {
            console.error('解析选项数据失败:', e)
            q.optionList = ['', '', '', '']
          }
        } else {
          q.optionList = ['', '', '', '']
        }
      }
    })
    open.value = true
    title.value = "修改问卷"
  }).catch(() => {
    // 如果加载题目失败，仍然打开对话框
    open.value = true
    title.value = "修改问卷"
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _questionnaireIds = row.questionnaireId || ids.value
  proxy.$modal.confirm('是否确认删除问卷编号为"' + _questionnaireIds + '"的数据项？').then(function () {
    return delQuestionnaire(_questionnaireIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

// 新增题目
function addQuestion() {
  form.value.questions.push({
    content: '',
    type: 'choice',
    optionList: ['', '', '', ''],
    standardAnswer: '',
    score: 0
  })
  // 新增题目后跳转到最后一题
  currentQuestionIndex.value = form.value.questions.length - 1
}

// 删除题目
function removeQuestion(index) {
  form.value.questions.splice(index, 1)
  // 删除题目后调整当前索引
  if (currentQuestionIndex.value >= form.value.questions.length) {
    currentQuestionIndex.value = Math.max(0, form.value.questions.length - 1)
  }
}

// 题目类型切换处理
function handleQuestionTypeChange(question) {
  if (question.type === 'choice') {
    // 如果是选择题，确保有4个选项
    if (!question.optionList || question.optionList.length === 0) {
      question.optionList = ['', '', '', '']
    }
  } else {
    // 如果是简答题，清空选项和标准答案
    question.optionList = []
    question.standardAnswer = ''
  }
}

// 计算总分
function calculateTotalScore() {
  if (!form.value.questions || form.value.questions.length === 0) {
    return 0
  }
  return form.value.questions.reduce((total, question) => {
    return total + (question.score || 0)
  }, 0)
}

// 轮播图控制函数
function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

function nextQuestion() {
  if (currentQuestionIndex.value < form.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

// 跳转到指定题目
function goToQuestion(index) {
  if (index >= 0 && index < form.value.questions.length) {
    currentQuestionIndex.value = index
  }
}

// 打开题库选择对话框
function openQuestionBankDialog() {
  questionBankOpen.value = true
  searchQuestionBank()
}

// 搜索题库
function searchQuestionBank() {
  questionBankLoading.value = true
  listQuestionnairebank(questionBankQuery.value).then(response => {
    questionBankList.value = response.rows
    questionBankTotal.value = response.total
    questionBankLoading.value = false
  })
}

// 重置题库搜索
function resetQuestionBankQuery() {
  questionBankQuery.value = {
    pageNum: 1,
    pageSize: 10,
    type: null,
    content: null
  }
  searchQuestionBank()
}

// 题库选择变化
function handleQuestionBankSelectionChange(selection) {
  selectedQuestions.value = selection
}

// 添加选中的题目
function addSelectedQuestions() {
  if (selectedQuestions.value.length === 0) {
    proxy.$modal.msgWarning("请选择要添加的题目")
    return
  }

  selectedQuestions.value.forEach(question => {
    const newQuestion = {
      content: question.content,
      type: question.type,
      optionList: ['', '', '', ''],
      standardAnswer: question.standardAnswer || '',
      score: question.score || 0
    }

    // 如果是选择题且有选项数据，解析选项
    if (question.type === 'choice' && question.options) {
      try {
        const parsedOptions = JSON.parse(question.options)
        if (typeof parsedOptions === 'object' && !Array.isArray(parsedOptions)) {
          // 对象格式 {"A": "内容1", "B": "内容2", ...}
          Object.keys(parsedOptions).forEach(key => {
            const index = key.charCodeAt(0) - 65 // A=0, B=1, C=2, D=3
            if (index >= 0 && index < 4) {
              newQuestion.optionList[index] = parsedOptions[key]
            }
          })
        } else {
          // 数组格式
          newQuestion.optionList = [...parsedOptions, '', '', '', ''].slice(0, 4)
        }
      } catch (e) {
        console.error('解析选项数据失败:', e)
      }
    }

    form.value.questions.push(newQuestion)
  })

  // 跳转到最后一题
  currentQuestionIndex.value = form.value.questions.length - 1
  questionBankOpen.value = false
  proxy.$modal.msgSuccess(`成功添加 ${selectedQuestions.value.length} 道题目`)
}

// 发送问卷相关方法
function handleSend(row) {
  sendForm.value = {
    questionnaireId: row.questionnaireId,
    title: row.title,
    description: row.description,
    status: row.status,
    type: row.type,
    startTime: row.startTime,
    endTime: row.endTime
  }
  selectedDept.value = null
  sendOpen.value = true
  getDeptTree()
}

// 获取部门树数据
function getDeptTree() {
  deptTreeSelect().then(response => {
    deptOptions.value = response.data
  })
}

// 部门节点点击事件
function handleDeptNodeClick(data) {
  selectedDept.value = data
}

// 部门树过滤方法
function filterNode(value, data) {
  if (!value) return true
  return data.label.indexOf(value) !== -1
}

// 确认发送
function confirmSend() {
  if (!selectedDept.value) {
    proxy.$modal.msgWarning("请选择要发送的部门")
    return
  }

  // 显示加载状态
  const loading = proxy.$loading({
    lock: true,
    text: '正在发送问卷...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  // 调用发送接口
  sendQuestionnaire(sendForm.value.questionnaireId, selectedDept.value.id).then(response => {
    loading.close()

    // 显示详细的发送结果
    const data = response.data || {}

    // 如果有详细统计信息
    if (data.sentCount !== undefined) {
      const { totalUsers, sentCount, skippedCount, noStudentCount } = data

      // 如果全部跳过（已存在或被禁用）
      if (sentCount === 0 && skippedCount === totalUsers) {
        proxy.$modal.msgWarning(`该部门的 ${totalUsers} 名学生无法发送问卷（可能已发送过或用户被禁用）`)
      }
      // 如果全部成功
      else if (sentCount === totalUsers) {
        proxy.$modal.msgSuccess(`问卷发送成功！已向 ${selectedDept.value.label} 的 ${sentCount} 名学生发送问卷`)
      }
      // 如果部分成功
      else if (sentCount > 0) {
        let message = `发送完成！成功 ${sentCount} 人`
        if (skippedCount > 0) {
          message += `，跳过 ${skippedCount} 人`
        }
        if (noStudentCount > 0) {
          message += `，${noStudentCount} 人无学生信息`
        }
        proxy.$modal.msgSuccess(message)
      }
      // 如果一个都没成功
      else {
        let message = '未发送任何问卷'
        if (skippedCount > 0) {
          message += `，${skippedCount} 人被跳过`
        }
        if (noStudentCount > 0) {
          message += `，${noStudentCount} 人无学生信息`
        }
        proxy.$modal.msgWarning(message)
      }
    } else {
      // 兼容旧版本返回格式
      proxy.$modal.msgSuccess(data.message || `问卷已成功发送到部门：${selectedDept.value.label}`)
    }

    sendOpen.value = false
  }).catch(error => {
    loading.close()
    console.error('发送问卷失败:', error)
    proxy.$modal.msgError('发送失败，请重试')
  })
}

// 监听部门名称搜索
watch(deptName, val => {
  if (proxy.$refs["deptTreeRef"]) {
    proxy.$refs["deptTreeRef"].filter(val)
  }
})

// 获取部门路径
function getDeptPath(dept) {
  if (!dept) return ''

  // 这里可以根据需要实现获取完整路径的逻辑
  // 目前简单返回部门名称
  return `路径: ${dept.label}`
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["questionnaireRef"].validate(valid => {
    if (valid) {
      // 保存前把选项数组转换为对象格式传给后端
      form.value.questions.forEach(q => {
        if (q.type === 'choice' && q.optionList && q.optionList.length > 0) {
          const optionsObj = {}
          q.optionList.forEach((content, index) => {
            if (content && content.trim() !== '') {
              const key = String.fromCharCode(65 + index) // A, B, C, D
              optionsObj[key] = content.trim()
            }
          })
          q.options = JSON.stringify(optionsObj)
        } else {
          q.options = null
        }
      })

      // 使用统一的保存接口
      saveQuestionnaire(form.value).then(response => {
        const message = form.value.questionnaireId != null ? "修改成功" : "新增成功"
        proxy.$modal.msgSuccess(message)
        open.value = false
        getList()
      })
    }
  })
}

getList()
</script>

<style scoped>
/* 题目轮播图样式 */
.question-carousel-container {
  margin: 20px 0;
}

.question-counter {
  text-align: center;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.question-carousel {
  position: relative;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
  min-height: 400px;
}

.question-slide {
  display: none;
  padding: 20px;
  transition: all 0.3s ease;
}

.question-slide.active {
  display: block;
}

.question-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.question-header h4 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.option-item .el-input {
  flex: 1;
}

.carousel-controls {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.no-questions {
  text-align: center;
  padding: 40px 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px dashed #d9d9d9;
}

.add-question-btn {
  text-align: center;
  margin-top: 20px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #b3d8ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-card {
    padding: 15px;
  }

  .carousel-controls {
    flex-direction: column;
    gap: 10px;
  }

  .carousel-controls .el-button {
    width: 100%;
  }
}

/* 题目类型切换动画 */
.question-slide {
  animation: slideIn 0.3s ease-in-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 选项输入框样式优化 */
.option-item .el-input {
  position: relative;
}

.option-item .el-input::before {
  content: attr(data-label);
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #909399;
  font-size: 12px;
  pointer-events: none;
}

/* 题库选择对话框样式 */
.question-bank-container {
  max-height: 600px;
  overflow-y: auto;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.option-label {
  min-width: 20px;
  font-weight: bold;
  color: #409eff;
  font-size: 14px;
}

/* 发送弹窗样式 */
.send-container {
  height: 450px;
  overflow: hidden;
}

.dept-panel {
  height: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
}

.panel-header {
  padding: 8px 15px;
  border-bottom: 1px solid #e4e7ed;
  background: white;
  border-radius: 8px 8px 0 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.panel-header h4 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.dept-tree-container {
  height: calc(100% - 40px);
  overflow: hidden;
  padding: 10px;
  background: white;
}

.dept-tree-container .el-tree {
  background: transparent;
}

.dept-tree-container .el-tree-node__content {
  padding: 8px 12px;
  border-radius: 6px;
  margin: 2px 0;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.dept-tree-container .el-tree-node__content:hover {
  background: #f0f9ff;
  border-color: #409eff;
  transform: translateX(3px);
}

.dept-tree-container .el-tree-node.is-current>.el-tree-node__content {
  background: #409eff;
  color: white;
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.dept-tree-container .el-tree-node__label {
  font-weight: 500;
  font-size: 14px;
}

.questionnaire-panel {
  height: 100%;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: white;
}

.questionnaire-info {
  padding: 10px;
  height: calc(100% - 40px);
  overflow: hidden;
  background: #fafafa;
}

.questionnaire-info .el-descriptions {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.questionnaire-info .el-descriptions__label {
  font-weight: 600;
  color: #606266;
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.questionnaire-info .el-descriptions__content {
  color: #303133;
  font-weight: 500;
  padding: 4px 8px;
  font-size: 12px;
}

.questionnaire-info .el-descriptions__table {
  margin: 0;
}

.questionnaire-info .el-descriptions__table .el-descriptions__cell {
  padding: 4px 8px;
}

.selected-dept-info {
  padding: 8px 15px;
  border-top: 1px solid #e4e7ed;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: 0 0 8px 8px;
  min-height: 80px;
}

.selected-dept-info .el-divider {
  margin: 0 0 8px 0;
  border-color: #409eff;
}

.selected-dept-info .el-divider__text {
  color: #409eff;
  font-weight: 600;
  font-size: 12px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  padding: 0 10px;
}

.selected-dept-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.dept-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
  transition: all 0.3s ease;
}

.dept-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}

.dept-tag .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.dept-details {
  text-align: center;
  color: #606266;
  font-size: 13px;
  line-height: 1.4;
}

.dept-id {
  margin: 0 0 3px 0;
  font-weight: 500;
  color: #909399;
}

.dept-path {
  margin: 0;
  color: #909399;
  font-style: italic;
}
</style>
