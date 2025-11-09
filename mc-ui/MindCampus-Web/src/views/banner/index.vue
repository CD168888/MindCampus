<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="轮播标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入轮播标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in banner_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['banner:banner:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['banner:banner:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['banner:banner:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['banner:banner:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="轮播图ID" align="center" prop="bannerId" width="100" />
      <el-table-column label="轮播标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="轮播图" align="center" prop="imageUrl" width="120">
        <template #default="scope">
          <image-preview v-if="scope.row.imageUrl" :src="scope.row.imageUrl" :width="100" :height="60" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="跳转链接" align="center" prop="linkUrl" show-overflow-tooltip width="200">
        <template #default="scope">
          <el-link v-if="scope.row.linkUrl" :href="scope.row.linkUrl" target="_blank" type="primary">
            {{ scope.row.linkUrl }}
          </el-link>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="显示顺序" align="center" prop="sortOrder" width="100" />
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
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="banner_status" :value="scope.row.status" />
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
            v-hasPermi="['banner:banner:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['banner:banner:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['banner:banner:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改轮播图对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="bannerRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="轮播标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入轮播标题" />
        </el-form-item>
        <el-form-item label="轮播图" prop="imageUrl">
          <image-upload v-model="form.imageUrl" :limit="1" />
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" placeholder="越小越靠前，默认为0" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择开始时间（可选）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择结束时间（可选）"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in banner_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
    <el-dialog title="轮播图详情" v-model="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="轮播图ID">{{ viewForm.bannerId }}</el-descriptions-item>
        <el-descriptions-item label="轮播标题">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="显示顺序">{{ viewForm.sortOrder || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="banner_status" :value="viewForm.status" />
        </el-descriptions-item>
        <el-descriptions-item label="轮播图" :span="2">
          <image-preview v-if="viewForm.imageUrl" :src="viewForm.imageUrl" :width="400" :height="240" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="跳转链接" :span="2">
          <el-link v-if="viewForm.linkUrl" :href="viewForm.linkUrl" target="_blank" type="primary">
            {{ viewForm.linkUrl }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间" :span="2">
          {{ parseTime(viewForm.startTime) || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="结束时间" :span="2">
          {{ parseTime(viewForm.endTime) || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          <div style="white-space: pre-wrap;">{{ viewForm.remark || '-' }}</div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewOpen = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Banner">
import {addBanner, delBanner, getBanner, listBanner, updateBanner} from "@/api/banner/banner"

const { proxy } = getCurrentInstance()
const { banner_status } = proxy.useDict('banner_status')

const bannerList = ref([])
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
    status: null,
  },
  rules: {
    title: [
      { required: true, message: "轮播标题不能为空", trigger: "blur" }
    ],
    imageUrl: [
      { required: true, message: "轮播图不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询轮播图列表 */
function getList() {
  loading.value = true
  listBanner(queryParams.value).then(response => {
    bannerList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
  nextTick(() => {
    proxy.resetForm("bannerRef")
  })
}

// 表单重置
function reset() {
  form.value = {
    bannerId: null,
    title: null,
    imageUrl: null,
    linkUrl: null,
    sortOrder: 0,
    startTime: null,
    endTime: null,
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
  ids.value = selection.map(item => item.bannerId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加轮播图"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _bannerId = row.bannerId || ids.value[0]
  getBanner(_bannerId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改轮播图"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  const _bannerId = row.bannerId
  getBanner(_bannerId).then(response => {
    viewForm.value = response.data
    viewOpen.value = true
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["bannerRef"].validate(valid => {
    if (valid) {
      if (form.value.bannerId != null) {
        updateBanner(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBanner(form.value).then(response => {
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
  const _bannerIds = row.bannerId || ids.value
  proxy.$modal.confirm('是否确认删除轮播图编号为"' + _bannerIds + '"的数据项？').then(function () {
    return delBanner(_bannerIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('banner/banner/export', {
    ...queryParams.value
  }, `banner_${new Date().getTime()}.xlsx`)
}

getList()
</script>

