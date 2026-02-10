<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="学生ID" prop="studentId">
        <el-input v-model="queryParams.studentId" placeholder="请输入学生ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="部门ID" prop="deptId">
        <el-input v-model="queryParams.deptId" placeholder="请输入部门ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['intervention:relation:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['intervention:relation:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['intervention:relation:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['intervention:relation:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="relationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="关系ID" align="center" prop="relationId" />
      <el-table-column label="学生ID" align="center" prop="studentId" />
      <el-table-column label="部门ID" align="center" prop="deptId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)" v-hasPermi="['intervention:relation:query']">查看</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['intervention:relation:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['intervention:relation:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改用户部门关系对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="userDeptRelationRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生ID" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
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

<script setup name="UserDeptRelation">
import { listUserDeptRelation, getUserDeptRelation, addUserDeptRelation, updateUserDeptRelation, delUserDeptRelation } from '@/api/intervention/userDeptRelation'
import Pagination from '@/components/Pagination'
import RightToolbar from '@/components/RightToolbar'
import { parseTime } from '@/utils/ruoyi'
import { nextTick } from 'vue'

const { proxy } = getCurrentInstance()

const relationList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    studentId: null,
    deptId: null,
    userId: null
  },
  rules: {
    studentId: [
      { required: true, message: '学生ID不能为空', trigger: 'blur' }
    ],
    deptId: [
      { required: true, message: '部门ID不能为空', trigger: 'blur' }
    ],
    userId: [
      { required: true, message: '用户ID不能为空', trigger: 'blur' }
    ]
  }
})

const { form, queryParams, rules } = toRefs(data)

/** 查询用户部门关系列表 */
function getList() {
  loading.value = true
  listUserDeptRelation(queryParams.value).then(response => {
    relationList.value = response.rows
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
  ids.value = selection.map(item => item.relationId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加用户部门关系"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const relationId = row.relationId || ids.value
  getUserDeptRelation(relationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改用户部门关系"
  })
}

/** 查看按钮操作 */
function handleView(row) {
  reset()
  getUserDeptRelation(row.relationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "查看用户部门关系"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["userDeptRelationRef"].validate(valid => {
    if (valid) {
      if (form.value.relationId != null) {
        updateUserDeptRelation(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUserDeptRelation(form.value).then(response => {
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
    relationId: null,
    studentId: null,
    deptId: null,
    userId: null
  }
  proxy.resetForm("userDeptRelationRef")
}

/** 删除按钮操作 */
function handleDelete(row) {
  const relationIds = row.relationId || ids.value
  proxy.$modal.confirm('是否确认删除用户部门关系编号为"' + relationIds + '"的数据项？').then(function () {
    return delUserDeptRelation(relationIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('intervention/relation/export', {
    ...queryParams.value
  }, `intervention-relation-${new Date().getTime()}.xlsx`)
}

getList()
</script>
