<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="帖子编号" prop="postId">
        <el-input v-model="queryParams.postId" placeholder="请输入帖子编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="评论内容" prop="content">
        <el-input v-model="queryParams.content" placeholder="请输入评论内容" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="是否匿名" prop="isAnonymous" style="width: 200px">
        <el-select v-model="queryParams.isAnonymous" placeholder="请选择是否匿名" clearable>
          <el-option v-for="dict in comment_is_anonymous" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in comment_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['community:comment:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['community:comment:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评论编号" align="center" prop="commentId" width="80" />
      <el-table-column label="帖子编号" align="center" prop="postId" width="80" />
      <el-table-column label="用户头像" align="center" width="80">
        <template #default="scope">
          <el-avatar :size="40" :src="getAvatarUrl(scope.row)" icon="UserFilled" />
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center" prop="userName" width="120" />
      <el-table-column label="评论内容" align="center" prop="content" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="回复给" align="center" width="120">
        <template #default="scope">
          <span v-if="scope.row.replyToUserName">@{{ scope.row.replyToUserName }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="点赞数" align="center" prop="likeCount" width="80" />
      <el-table-column label="是否匿名" align="center" prop="isAnonymous" width="100">
        <template #default="scope">
          <dict-tag :options="comment_is_anonymous" :value="scope.row.isAnonymous" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="comment_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="评论时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)"
            v-hasPermi="['community:comment:query']">详情</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['community:comment:remove']">删除</el-button>
          <el-button link type="warning" v-if="scope.row.status === '0'" icon="Lock" @click="handleBlock(scope.row)"
            v-hasPermi="['community:comment:edit']">屏蔽</el-button>
          <el-button link type="success" v-else icon="Unlock" @click="handleUnblock(scope.row)"
            v-hasPermi="['community:comment:edit']">取消屏蔽</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 查看评论详情对话框 -->
    <el-dialog title="评论详情" v-model="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评论编号">{{ viewForm.commentId }}</el-descriptions-item>
        <el-descriptions-item label="帖子编号">{{ viewForm.postId }}</el-descriptions-item>
        <el-descriptions-item label="用户头像" :span="1">
          <el-avatar :size="50" :src="getAvatarUrl(viewForm)" icon="UserFilled" />
        </el-descriptions-item>
        <el-descriptions-item label="用户名">{{ viewForm.userName || '匿名用户' }}</el-descriptions-item>
        <el-descriptions-item label="是否匿名" :span="2">
          <dict-tag :options="comment_is_anonymous" :value="viewForm.isAnonymous" />
        </el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <dict-tag :options="comment_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="点赞数">{{ viewForm.likeCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="回复给">
          <span v-if="viewForm.replyToUserName">@{{ viewForm.replyToUserName }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="评论内容" :span="2">
          <div style="white-space: pre-wrap; word-break: break-all;">{{ viewForm.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="评论时间" :span="2">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CommunityComment">
import {changeCommentStatus, delComment, getComment, listComment} from "@/api/community/comment"

const { proxy } = getCurrentInstance()
const { comment_is_anonymous, comment_status } = proxy.useDict(
  'comment_is_anonymous',
  'comment_status'
)

const commentList = ref([])
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
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    postId: null,
    userName: null,
    content: null,
    isAnonymous: null,
    status: null,
  }
})

const { queryParams } = toRefs(data)

/** 查询评论管理列表 */
function getList() {
  loading.value = true
  listComment(queryParams.value).then(response => {
    commentList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
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
  ids.value = selection.map(item => item.commentId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 查看详情 */
function handleView(row) {
  const _commentId = row.commentId
  getComment(_commentId).then(response => {
    viewForm.value = response.data || row
    viewOpen.value = true
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _commentIds = row.commentId || ids.value
  proxy.$modal.confirm('是否确认删除评论编号为"' + _commentIds + '"的数据项？').then(function () {
    return delComment(_commentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('community/comment/export', {
    ...queryParams.value
  }, `comment_${new Date().getTime()}.xlsx`)
}

/** 屏蔽评论 */
function handleBlock(row) {
  proxy.$modal.confirm('确认要屏蔽该评论吗？').then(function () {
    return changeCommentStatus({ commentId: row.commentId, status: '1' })
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("屏蔽成功")
  }).catch(() => { })
}

/** 取消屏蔽评论 */
function handleUnblock(row) {
  proxy.$modal.confirm('确认要取消屏蔽该评论吗？').then(function () {
    return changeCommentStatus({ commentId: row.commentId, status: '0' })
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("取消屏蔽成功")
  }).catch(() => { })
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

getList()
</script>
