<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="学生姓名" prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="帖子内容" prop="content">
        <el-input v-model="queryParams.content" placeholder="请输入帖子内容" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="是否匿名" prop="isAnonymous" style="width: 250px">
        <el-select v-model="queryParams.isAnonymous" placeholder="请选择是否匿名" clearable>
          <el-option v-for="dict in post_is_anonymous" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 250px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in post_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['community:community:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['community:community:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['community:community:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['community:community:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="帖子ID" align="center" prop="postId" width="80" />
      <el-table-column label="用户头像" align="center" width="80">
        <template #default="scope">
          <el-avatar :size="40" :src="getAvatarUrl(scope.row)" icon="UserFilled" />
        </template>
      </el-table-column>
      <el-table-column label="学生姓名" align="center" prop="studentName" width="100" />
      <el-table-column label="帖子标题" align="center" prop="title" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="帖子内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="是否匿名" align="center" prop="isAnonymous" width="100">
        <template #default="scope">
          <dict-tag :options="post_is_anonymous" :value="scope.row.isAnonymous" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="post_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="评论数" align="center" prop="commentCount" width="80" />
      <el-table-column label="发布时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['community:community:query']">详情</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['community:community:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['community:community:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改帖子管理对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="postRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" clearable filterable style="width: 100%"
            @change="handleStudentChange">
            <el-option v-for="student in studentList" :key="student.studentId"
              :label="`${student.name} (${student.studentNo})`" :value="student.studentId" />
          </el-select>
        </el-form-item>
        <el-form-item label="帖子标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入帖子标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="帖子内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入帖子内容" maxlength="2000"
            show-word-limit />
        </el-form-item>
        <el-form-item label="是否匿名" prop="isAnonymous">
          <el-radio-group v-model="form.isAnonymous">
            <el-radio v-for="dict in post_is_anonymous" :key="dict.value" :label="dict.value">{{ dict.label
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in post_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
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

    <!-- 查看帖子详情和评论对话框 -->
    <el-dialog title="帖子详情" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="帖子ID">{{ postDetail.postId }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ postDetail.studentName }}</el-descriptions-item>
        <el-descriptions-item label="是否匿名" :span="2">
          <dict-tag :options="post_is_anonymous" :value="postDetail.isAnonymous" />
        </el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <dict-tag :options="post_status" :value="postDetail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="帖子内容" :span="2">
          <div style="white-space: pre-wrap; word-break: break-all;">{{ postDetail.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ parseTime(postDetail.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">评论列表 ({{ commentList.length }})</el-divider>

      <div v-if="commentList.length === 0" style="text-align: center; padding: 40px; color: #999;">
        <el-empty description="暂无评论" :image-size="80" />
      </div>

      <div v-else style="margin-top: 15px;">
        <el-card v-for="comment in commentList" :key="comment.commentId" shadow="hover"
          style="margin-bottom: 15px; cursor: default;">
          <div style="display: flex; gap: 15px;">
            <!-- 用户头像 -->
            <el-avatar :size="50" :src="getCommentAvatarUrl(comment)" icon="UserFilled" />

            <!-- 评论内容 -->
            <div style="flex: 1;">
              <!-- 用户信息行 -->
              <div style="margin-bottom: 8px; display: flex; align-items: center; gap: 10px;">
                <span style="font-weight: 600; color: #303133;">{{ comment.userName || '匿名用户' }}</span>
                <el-tag size="small" v-if="comment.studentName" type="info">{{ comment.studentName }}</el-tag>
                <dict-tag :options="comment_is_anonymous" :value="comment.isAnonymous" />
                <dict-tag :options="comment_status" :value="comment.status" />
              </div>

              <!-- 评论内容 -->
              <div
                style="color: #606266; line-height: 1.6; margin-bottom: 8px; white-space: pre-wrap; word-break: break-all;">
                {{ comment.content }}
              </div>

              <!-- 时间和点赞 -->
              <div style="display: flex; align-items: center; gap: 15px; font-size: 13px; color: #909399;">
                <span>
                  <el-icon>
                    <Clock />
                  </el-icon>
                  {{ parseTime(comment.createTime) }}
                </span>
                <span v-if="comment.likeCount > 0">
                  <el-icon>
                    <Star />
                  </el-icon>
                  {{ comment.likeCount }} 点赞
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CommunityPost">
import {addPost, delPost, getPost, listPost, updatePost} from "@/api/community/post"
import {listInfo} from "@/api/student/info"

const { proxy } = getCurrentInstance()
const { post_is_anonymous, post_status, comment_is_anonymous, comment_status } = proxy.useDict(
  'post_is_anonymous',
  'post_status',
  'comment_is_anonymous',
  'comment_status'
)

const postList = ref([])
const open = ref(false)
const viewOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const postDetail = ref({})
const commentList = ref([])
const commentTreeData = ref([])
const studentList = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentName: null,
    content: null,
    isAnonymous: null,
    status: null,
  },
  rules: {
    studentId: [
      { required: true, message: "学生ID不能为空", trigger: "blur" }
    ],
    title: [
      { required: true, message: "帖子标题不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "帖子内容不能为空", trigger: "blur" }
    ],
    isAnonymous: [
      { required: true, message: "是否匿名不能为空", trigger: "change" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询帖子管理列表 */
function getList() {
  loading.value = true
  listPost(queryParams.value).then(response => {
    postList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 获取学生列表 */
function getStudentList() {
  listInfo({ pageNum: 1, pageSize: 1000, status: '0' }).then(response => {
    studentList.value = response.rows || []
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
    postId: null,
    studentId: null,
    userId: null,
    userName: null,
    userAvatar: null,
    title: null,
    content: null,
    isAnonymous: "1",
    status: "0",
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("postRef")
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
  ids.value = selection.map(item => item.postId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getStudentList()
  open.value = true
  title.value = "添加帖子"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getStudentList()
  const _postId = row.postId || ids.value
  getPost(_postId).then(response => {
    form.value = response.post
    open.value = true
    title.value = "修改帖子"
  })
}

/** 查看详情 */
function handleView(row) {
  const _postId = row.postId
  getPost(_postId).then(response => {
    postDetail.value = response.post
    commentList.value = response.comments || []
    // 将评论转换为树形结构数据
    commentTreeData.value = commentList.value.map(comment => ({
      ...comment,
      children: [] // 当前数据表不支持回复，所以children为空
    }))
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["postRef"].validate(valid => {
    if (valid) {
      if (form.value.postId != null) {
        updatePost(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPost(form.value).then(response => {
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
  const _postIds = row.postId || ids.value
  proxy.$modal.confirm('是否确认删除帖子编号为"' + _postIds + '"的数据项？删除帖子将同时删除关联的评论！').then(function () {
    return delPost(_postIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('community/post/export', {
    ...queryParams.value
  }, `post_${new Date().getTime()}.xlsx`)
}

/** 学生选择变化 - 用户信息由后端自动填充 */
function handleStudentChange(studentId) {
  // 注意：用户信息（userId, userName, userAvatar）
  // 将在后端提交时自动根据studentId填充，前端无需处理
}

/** 获取头像URL */
function getAvatarUrl(row) {
  if (!row.userAvatar || row.userAvatar.trim() === '') {
    return ''
  }
  if (row.userAvatar.startsWith('http')) {
    return row.userAvatar
  }
  return import.meta.env.VITE_APP_BASE_API + row.userAvatar
}

/** 获取评论头像URL */
function getCommentAvatarUrl(comment) {
  if (!comment.userAvatar || comment.userAvatar.trim() === '') {
    return ''
  }
  if (comment.userAvatar.startsWith('http')) {
    return comment.userAvatar
  }
  return import.meta.env.VITE_APP_BASE_API + comment.userAvatar
}

getList()
</script>

<style scoped>
.el-tree {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}
</style>
