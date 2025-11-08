<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入文章标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="queryParams.author" placeholder="请输入作者" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="文章分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入文章分类" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in article_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['recommendArticle:recommendArticle:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['recommendArticle:recommendArticle:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['recommendArticle:recommendArticle:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['recommendArticle:recommendArticle:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recommendArticleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="文章ID" align="center" prop="articleId" width="80" />
      <el-table-column label="文章标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="作者" align="center" prop="author" width="120" />
      <el-table-column label="文章分类" align="center" prop="category" width="120" />
      <el-table-column label="阅读量" align="center" prop="readCount" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="article_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['recommendArticle:recommendArticle:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['recommendArticle:recommendArticle:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['recommendArticle:recommendArticle:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改心理文章推荐对话框 -->
    <el-dialog :title="title" v-model="open" width="1000px" append-to-body>
      <el-form ref="recommendArticleRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入文章标题" />
        </el-form-item>
        <el-form-item label="文章摘要" prop="summary">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="请输入文章摘要" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="文章分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入文章分类" />
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="15" placeholder="请输入文章内容（Markdown格式）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in article_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
    <el-dialog title="文章详情" v-model="viewOpen" width="1000px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文章ID">{{ viewForm.articleId }}</el-descriptions-item>
        <el-descriptions-item label="文章标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ viewForm.author || '-' }}</el-descriptions-item>
        <el-descriptions-item label="文章分类">{{ viewForm.category || '-' }}</el-descriptions-item>
        <el-descriptions-item label="阅读量">{{ viewForm.readCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="article_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="文章摘要" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.summary || '-' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="文章内容" :span="2">
          <div style="white-space: pre-wrap; max-height: 400px; overflow-y: auto;">{{ viewForm.content || '-' }}</div>
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

<script setup name="RecommendArticle">
import {
  addRecommendArticle,
  delRecommendArticle,
  getRecommendArticle,
  listRecommendArticle,
  updateRecommendArticle
} from "@/api/recommend/recommendArticle"

const { proxy } = getCurrentInstance()
const { article_status } = proxy.useDict('article_status')

const recommendArticleList = ref([])
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
    title: null,
    author: null,
    category: null,
    status: null,
  },
  rules: {
    title: [
      { required: true, message: "文章标题不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "文章内容不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询心理文章推荐列表 */
function getList() {
  loading.value = true
  listRecommendArticle(queryParams.value).then(response => {
    recommendArticleList.value = response.rows
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
    articleId: null,
    title: null,
    content: null,
    summary: null,
    author: null,
    readCount: 0,
    category: null,
    status: '0',
    remark: null
  }
  proxy.resetForm("recommendArticleRef")
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
  ids.value = selection.map(item => item.articleId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加心理文章推荐"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _articleId = row.articleId || ids.value[0]
  getRecommendArticle(_articleId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改心理文章推荐"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  const _articleId = row.articleId
  getRecommendArticle(_articleId).then(response => {
    viewForm.value = response.data
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["recommendArticleRef"].validate(valid => {
    if (valid) {
      if (form.value.articleId != null) {
        updateRecommendArticle(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRecommendArticle(form.value).then(response => {
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
  const _articleIds = row.articleId || ids.value
  proxy.$modal.confirm('是否确认删除心理文章推荐编号为"' + _articleIds + '"的数据项？').then(function () {
    return delRecommendArticle(_articleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('recommend/recommendArticle/export', {
    ...queryParams.value
  }, `recommendArticle_${new Date().getTime()}.xlsx`)
}

getList()
</script>
