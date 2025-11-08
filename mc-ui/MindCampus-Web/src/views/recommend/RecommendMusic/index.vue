<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="音乐标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入音乐标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="演唱者" prop="artist">
        <el-input v-model="queryParams.artist" placeholder="请输入演唱者" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="音乐风格" prop="genre">
        <el-input v-model="queryParams.genre" placeholder="请输入音乐风格" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in music_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['recommendMusic:recommendMusic:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['recommendMusic:recommendMusic:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['recommendMusic:recommendMusic:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['recommendMusic:recommendMusic:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recommendMusicList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="音乐ID" align="center" prop="musicId" width="80" />
      <el-table-column label="音乐标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="演唱者" align="center" prop="artist" width="120" />
      <el-table-column label="音乐风格" align="center" prop="genre" width="120" />
      <el-table-column label="时长" align="center" prop="duration" width="100">
        <template #default="scope">
          <span>{{ formatDuration(scope.row.duration) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="封面图" align="center" prop="coverUrl" width="100">
        <template #default="scope">
          <image-preview v-if="scope.row.coverUrl" :src="scope.row.coverUrl" :width="50" :height="50" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="music_status" :value="scope.row.status" />
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
            v-hasPermi="['recommendMusic:recommendMusic:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['recommendMusic:recommendMusic:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['recommendMusic:recommendMusic:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改心理音乐推荐对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="recommendMusicRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="音乐标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入音乐标题" />
        </el-form-item>
        <el-form-item label="音频文件" prop="mp3Url">
          <file-upload v-model="form.mp3Url" :limit="1" :file-size="100" :file-type="['mp3']" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverUrl">
          <image-upload v-model="form.coverUrl" :limit="1" />
        </el-form-item>
        <el-form-item label="演唱者/作者" prop="artist">
          <el-input v-model="form.artist" placeholder="请输入演唱者/作者" />
        </el-form-item>
        <el-form-item label="音乐风格" prop="genre">
          <el-input v-model="form.genre" placeholder="请输入音乐风格" />
        </el-form-item>
        <el-form-item label="时长（秒）" prop="duration">
          <el-input-number v-model="form.duration" :min="0" placeholder="上传音频后自动计算" :disabled="calculatingDuration" />
          <span v-if="calculatingDuration" style="margin-left: 10px; color: #409eff; font-size: 12px;">正在计算时长...</span>
        </el-form-item>
        <el-form-item label="音乐简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入音乐简介" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in music_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
    <el-dialog title="音乐详情" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="音乐ID">{{ viewForm.musicId }}</el-descriptions-item>
        <el-descriptions-item label="音乐标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="演唱者/作者">{{ viewForm.artist || '-' }}</el-descriptions-item>
        <el-descriptions-item label="音乐风格">{{ viewForm.genre || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ formatDuration(viewForm.duration) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="music_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="封面图" :span="2">
          <image-preview v-if="viewForm.coverUrl" :src="viewForm.coverUrl" :width="200" :height="200" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="音频文件" :span="2">
          <el-link v-if="viewForm.mp3Url" :href="viewForm.mp3Url" target="_blank" type="primary">
            {{ viewForm.mp3Url }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="音乐简介" :span="2">
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

<script setup name="RecommendMusic">
import {
  addRecommendMusic,
  delRecommendMusic,
  getRecommendMusic,
  listRecommendMusic,
  updateRecommendMusic
} from "@/api/recommend/recommendMusic"

const { proxy } = getCurrentInstance()
const { music_status } = proxy.useDict('music_status')

const recommendMusicList = ref([])
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
    artist: null,
    genre: null,
    status: null,
  },
  rules: {
    title: [
      { required: true, message: "音乐标题不能为空", trigger: "blur" }
    ],
    mp3Url: [
      { required: true, message: "音频文件不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

// 监听音频文件变化，自动计算时长
watch(() => form.value.mp3Url, (newUrl, oldUrl) => {
  // 只在新增时自动计算，或者URL变化且没有时长时计算
  if (newUrl && newUrl !== oldUrl && (!form.value.duration || form.value.musicId == null)) {
    calculateAudioDuration(newUrl)
  }
}, { immediate: false })

// 计算音频时长
function calculateAudioDuration(url) {
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

  const audio = new Audio(fullUrl)

  // 设置超时，避免长时间等待
  const timeout = setTimeout(() => {
    calculatingDuration.value = false
    audio.src = ''
    console.warn('计算音频时长超时')
  }, 10000) // 10秒超时

  audio.addEventListener('loadedmetadata', () => {
    clearTimeout(timeout)
    const duration = Math.floor(audio.duration)
    if (duration > 0 && !isNaN(duration)) {
      form.value.duration = duration
      console.log('自动计算音频时长:', duration, '秒')
    }
    calculatingDuration.value = false
    audio.src = ''
  })

  audio.addEventListener('error', (e) => {
    clearTimeout(timeout)
    calculatingDuration.value = false
    console.warn('无法加载音频文件获取时长:', e)
    audio.src = ''
  })

  // 触发加载
  audio.load()
}

/** 查询心理音乐推荐列表 */
function getList() {
  loading.value = true
  listRecommendMusic(queryParams.value).then(response => {
    recommendMusicList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 格式化时长（秒转分:秒）
function formatDuration(seconds) {
  if (!seconds) return '-'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
  nextTick(() => {
    proxy.resetForm("recommendMusicRef")
  })
}

// 表单重置
function reset() {
  form.value = {
    musicId: null,
    title: null,
    mp3Url: null,
    coverUrl: null,
    artist: null,
    genre: null,
    duration: null,
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
  ids.value = selection.map(item => item.musicId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  console.log('handleAdd 被调用')
  reset()
  open.value = true
  title.value = "添加心理音乐推荐"
  console.log('open.value:', open.value)
  console.log('title.value:', title.value)
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _musicId = row.musicId || ids.value[0]
  getRecommendMusic(_musicId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改心理音乐推荐"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  const _musicId = row.musicId
  getRecommendMusic(_musicId).then(response => {
    viewForm.value = response.data
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["recommendMusicRef"].validate(valid => {
    if (valid) {
      if (form.value.musicId != null) {
        updateRecommendMusic(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRecommendMusic(form.value).then(response => {
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
  const _musicIds = row.musicId || ids.value
  proxy.$modal.confirm('是否确认删除心理音乐推荐编号为"' + _musicIds + '"的数据项？').then(function () {
    return delRecommendMusic(_musicIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('recommend/recommendMusic/export', {
    ...queryParams.value
  }, `recommendMusic_${new Date().getTime()}.xlsx`)
}

getList()
</script>
