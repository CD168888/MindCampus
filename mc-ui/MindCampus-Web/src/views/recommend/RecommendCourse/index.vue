<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="课程标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入课程标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="讲师" prop="lecturer">
        <el-input v-model="queryParams.lecturer" placeholder="请输入讲师" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="课程难度" prop="level" style="width: 200px">
        <el-select v-model="queryParams.level" placeholder="请选择课程难度" clearable>
          <el-option v-for="dict in course_level" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in course_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['recommendCourse:recommendCourse:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['recommendCourse:recommendCourse:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['recommendCourse:recommendCourse:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['recommendCourse:recommendCourse:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recommendCourseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程ID" align="center" prop="courseId" width="80" />
      <el-table-column label="课程标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="讲师" align="center" prop="lecturer" width="120" />
      <el-table-column label="课程难度" align="center" prop="level" width="120">
        <template #default="scope">
          <dict-tag :options="course_level" :value="scope.row.level" />
        </template>
      </el-table-column>
      <el-table-column label="时长" align="center" prop="duration" width="100">
        <template #default="scope">
          <span>{{ formatDuration(scope.row.duration) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="章节数" align="center" prop="chapters" width="100" />
      <el-table-column label="封面图" align="center" prop="coverUrl" width="100">
        <template #default="scope">
          <image-preview v-if="scope.row.coverUrl" :src="scope.row.coverUrl" :width="50" :height="50" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="course_status" :value="scope.row.status" />
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
            v-hasPermi="['recommendCourse:recommendCourse:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['recommendCourse:recommendCourse:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['recommendCourse:recommendCourse:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改心理课程推荐对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="recommendCourseRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入课程标题" />
        </el-form-item>
        <el-form-item label="视频文件" prop="mp4Url">
          <file-upload v-model="form.mp4Url" :limit="1" :file-size="500" :file-type="['mp4']" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverUrl">
          <image-upload v-model="form.coverUrl" :limit="1" />
        </el-form-item>
        <el-form-item label="讲师" prop="lecturer">
          <el-input v-model="form.lecturer" placeholder="请输入讲师" />
        </el-form-item>
        <el-form-item label="视频时长" prop="duration">
          <el-input v-model="durationFormatted" placeholder="上传视频后自动计算" disabled style="width: 100%;" />
          <span v-if="calculatingDuration" style="margin-left: 10px; color: #409eff; font-size: 12px;">正在计算时长...</span>
        </el-form-item>
        <el-form-item label="章节数" prop="chapters">
          <el-input-number v-model="form.chapters" :min="0" placeholder="请输入章节数" />
        </el-form-item>
        <el-form-item label="课程难度" prop="level">
          <el-select v-model="form.level" placeholder="请选择课程难度">
            <el-option v-for="dict in course_level" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in course_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
    <el-dialog title="课程详情" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程ID">{{ viewForm.courseId }}</el-descriptions-item>
        <el-descriptions-item label="课程标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="讲师">{{ viewForm.lecturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程难度">
          <dict-tag :options="course_level" :value="viewForm.level" />
        </el-descriptions-item>
        <el-descriptions-item label="时长">{{ formatDuration(viewForm.duration) }}</el-descriptions-item>
        <el-descriptions-item label="章节数">{{ viewForm.chapters || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="course_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="封面图" :span="2">
          <image-preview v-if="viewForm.coverUrl" :src="viewForm.coverUrl" :width="200" :height="200" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="视频文件" :span="2">
          <el-link v-if="viewForm.mp4Url" :href="viewForm.mp4Url" target="_blank" type="primary">
            {{ viewForm.mp4Url }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="课程简介" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.description || '-' }}</div>
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

<script setup name="RecommendCourse">
import {
  addRecommendCourse,
  delRecommendCourse,
  getRecommendCourse,
  listRecommendCourse,
  updateRecommendCourse
} from "@/api/recommend/recommendCourse"

const { proxy } = getCurrentInstance()
const { course_status, course_level } = proxy.useDict('course_status', 'course_level')

const recommendCourseList = ref([])
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
const calculatingDuration = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: null,
    lecturer: null,
    level: null,
    status: null,
  },
  rules: {
    title: [
      { required: true, message: "课程标题不能为空", trigger: "blur" }
    ],
    mp4Url: [
      { required: true, message: "视频文件不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 计算属性：将秒数转换为时分秒格式
const durationFormatted = computed({
  get: () => {
    if (!form.value.duration) return ''
    return formatDuration(form.value.duration)
  },
  set: (value) => {
    // 保持原始值不变，因为我们只在显示时使用格式化值
  }
})

// 监听视频文件变化，自动计算时长
watch(() => form.value.mp4Url, (newUrl, oldUrl) => {
  if (newUrl && newUrl !== oldUrl) {
    // 上传或修改视频文件时，自动计算时长
    calculateVideoDuration(newUrl)
  } else if (!newUrl && oldUrl) {
    // 删除视频文件时，清空时长
    form.value.duration = null
  }
}, { immediate: false })

// 计算视频时长
function calculateVideoDuration(url) {
  if (!url) return

  calculatingDuration.value = true

  // 构建完整的 URL
  const baseUrl = import.meta.env.VITE_APP_BASE_API
  let fullUrl = url

  // 如果URL不是完整路径，需要拼接baseUrl
  if (!url.startsWith('http')) {
    if (url.startsWith('/')) {
      fullUrl = baseUrl + url
    } else {
      fullUrl = baseUrl + '/' + url
    }
  }

  const video = document.createElement('video')
  video.preload = 'metadata'

  // 设置超时，避免长时间等待
  const timeout = setTimeout(() => {
    calculatingDuration.value = false
    video.src = ''
    video.load()
    console.warn('计算视频时长超时')
  }, 15000) // 15秒超时（视频文件可能较大）

  video.addEventListener('loadedmetadata', () => {
    clearTimeout(timeout)
    const duration = Math.floor(video.duration)
    if (duration > 0 && !isNaN(duration)) {
      form.value.duration = duration
      console.log('自动计算视频时长:', duration, '秒')
    }
    calculatingDuration.value = false
    // 清理
    video.src = ''
    video.load()
  })

  video.addEventListener('error', (e) => {
    clearTimeout(timeout)
    calculatingDuration.value = false
    console.warn('无法加载视频文件获取时长:', e)
    // 清理
    video.src = ''
    video.load()
  })

  // 设置视频源并触发加载
  video.src = fullUrl
}

/** 查询心理课程推荐列表 */
function getList() {
  loading.value = true
  listRecommendCourse(queryParams.value).then(response => {
    recommendCourseList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 格式化时长（秒转时:分:秒）
function formatDuration(seconds) {
  if (!seconds) return '-'
  const hours = Math.floor(seconds / 3600)
  const mins = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  if (hours > 0) {
    return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
  nextTick(() => {
    proxy.resetForm("recommendCourseRef")
  })
}

// 表单重置
function reset() {
  form.value = {
    courseId: null,
    title: null,
    mp4Url: null,
    coverUrl: null,
    lecturer: null,
    duration: null,
    chapters: null,
    level: null,
    description: null,
    status: '0',
    remark: null
  }
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
  ids.value = selection.map(item => item.courseId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  console.log('handleAdd 被调用')
  reset()
  open.value = true
  title.value = "添加心理课程推荐"
  console.log('open.value:', open.value)
  console.log('title.value:', title.value)
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _courseId = row.courseId || ids.value[0]
  getRecommendCourse(_courseId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改心理课程推荐"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  const _courseId = row.courseId
  getRecommendCourse(_courseId).then(response => {
    viewForm.value = response.data
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["recommendCourseRef"].validate(valid => {
    if (valid) {
      if (form.value.courseId != null) {
        updateRecommendCourse(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRecommendCourse(form.value).then(response => {
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
  const _courseIds = row.courseId || ids.value
  proxy.$modal.confirm('是否确认删除心理课程推荐编号为"' + _courseIds + '"的数据项？').then(function () {
    return delRecommendCourse(_courseIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('recommend/recommendCourse/export', {
    ...queryParams.value
  }, `recommendCourse_${new Date().getTime()}.xlsx`)
}

getList()
</script>
