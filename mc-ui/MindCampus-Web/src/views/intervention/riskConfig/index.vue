<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="风险等级" prop="riskLevel">
        <el-input v-model="queryParams.riskLevel" placeholder="请输入风险等级" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="最小分数" prop="minScore">
        <el-input v-model="queryParams.minScore" placeholder="请输入最小分数" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="最大分数" prop="maxScore">
        <el-input v-model="queryParams.maxScore" placeholder="请输入最大分数" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['intervention:risk:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['intervention:risk:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['intervention:risk:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['intervention:risk:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配置ID" align="center" prop="configId" />
      <el-table-column label="风险等级" align="center" prop="riskLevel" />
      <el-table-column label="最小分数" align="center" prop="minScore" />
      <el-table-column label="最大分数" align="center" prop="maxScore" />
      <el-table-column label="通知模板" align="center" prop="notificationTemplate" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['intervention:risk:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['intervention:risk:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:risk:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改风险等级配置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="风险等级" prop="riskLevel">
          <el-input v-model="form.riskLevel" placeholder="请输入风险等级" />
        </el-form-item>
        <el-form-item label="最小分数" prop="minScore">
          <el-input v-model="form.minScore" type="number" placeholder="请输入最小分数" />
        </el-form-item>
        <el-form-item label="最大分数" prop="maxScore">
          <el-input v-model="form.maxScore" type="number" placeholder="请输入最大分数" />
        </el-form-item>
        <el-form-item label="通知模板" prop="notificationTemplate">
          <el-input v-model="form.notificationTemplate" type="textarea" placeholder="请输入通知模板" />
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

<script setup name="RiskConfig">
import { ref, reactive, toRefs, onMounted, getCurrentInstance } from 'vue'
import { listRiskConfig, getRiskConfig, addRiskConfig, updateRiskConfig, delRiskConfig } from '@/api/intervention/riskConfig'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance()

const configList = ref([])
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
    configId: null,
    riskLevel: null,
    minScore: null,
    maxScore: null,
    notificationTemplate: null
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    riskLevel: null,
    minScore: null,
    maxScore: null
  },
  rules: {
    riskLevel: [
      { required: true, message: '风险等级不能为空', trigger: 'blur' }
    ],
    minScore: [
      { required: true, message: '最小分数不能为空', trigger: 'blur' }
    ],
    maxScore: [
      { required: true, message: '最大分数不能为空', trigger: 'blur' }
    ],
    notificationTemplate: [
      { required: true, message: '通知模板不能为空', trigger: 'blur' }
    ]
  }
})

const { form, queryParams, rules } = toRefs(data)

/** 查询风险等级配置列表 */
function getList() {
  loading.value = true
  listRiskConfig(queryParams.value).then(response => {
    configList.value = response.rows
    total.value = response.total
    loading.value = false
  })
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
  ids.value = selection.map(item => item.configId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加风险等级配置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const configId = row.configId || ids.value
  getRiskConfig(configId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改风险等级配置"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  reset()
  getRiskConfig(row.configId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "查看风险等级配置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["formRef"].validate(valid => {
    if (valid) {
      if (form.value.configId != null) {
        updateRiskConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRiskConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 重置表单 */
function reset() {
  form.value = {
    configId: null,
    riskLevel: null,
    minScore: null,
    maxScore: null,
    notificationTemplate: null
  }
  proxy.resetForm("formRef")
}

/** 删除按钮操作 */
function handleDelete(row) {
  const configIds = row.configId || ids.value
  proxy.$modal.confirm('是否确认删除风险等级配置编号为"' + configIds + '"的数据项？').then(function () {
    return delRiskConfig(configIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('intervention/risk/export', {
    ...queryParams.value
  }, `intervention-risk-${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
})
</script>
