<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入联系电话" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="queryParams.email" placeholder="请输入邮箱" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="办公室地址" prop="office" style="white-space: nowrap;">
        <el-input v-model="queryParams.office" placeholder="请输入办公室地址" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 200px">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in counselor_status" :key="dict.value" :label="dict.label" :value="dict.value" />
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
          v-hasPermi="['counselor:counselorinfo:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['counselor:counselorinfo:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['counselor:counselorinfo:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport"
          v-hasPermi="['counselor:counselorinfo:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="counselorinfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="辅导员ID" align="center" prop="counselorId" />
      <el-table-column label="用户id" align="center" prop="userId" />
      <el-table-column label="姓名" align="center" prop="name" />
      <el-table-column label="联系电话" align="center" prop="phone" />
      <el-table-column label="邮箱" align="center" prop="email" />
      <el-table-column label="办公室地址" align="center" prop="office" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="counselor_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['counselor:counselorinfo:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['counselor:counselorinfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改辅导员管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="counselorinfoRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="用户ID" :disabled="true" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="userNickName">
          <el-select v-model="form.userId" placeholder="请选择用户昵称" :disabled="isEditMode" clearable
            @change="handleUserChange">
            <el-option v-for="user in unbindUsers" :key="user.userId" :label="user.nickName" :value="user.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="办公室地址" prop="office" style="white-space: nowrap;">
          <el-input v-model="form.office" placeholder="请输入办公室地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in counselor_status" :key="dict.value" :label="dict.label"
              :value="dict.value"></el-option>
          </el-select>
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

<script setup name="Counselorinfo">
import {
  addCounselorinfo,
  delCounselorinfo,
  getCounselorinfo,
  listCounselorinfo,
  listUnbindUsers,
  updateCounselorinfo
} from "@/api/counselor/counselorinfo"

const { proxy } = getCurrentInstance()
const { counselor_status } = proxy.useDict('counselor_status')

const counselorinfoList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const unbindUsers = ref([])
const isEditMode = ref(false)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    phone: null,
    email: null,
    office: null,
    status: null,
  },
  rules: {
    userId: [
      { required: true, message: "用户id不能为空", trigger: "blur" }
    ],
    name: [
      { required: true, message: "姓名不能为空", trigger: "blur" }
    ],
    phone: [
      { required: true, message: "联系电话不能为空", trigger: "blur" }
    ],
    email: [
      { required: true, message: "邮箱不能为空", trigger: "blur" }
    ],
    office: [
      { required: true, message: "办公室地址不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询辅导员管理列表 */
function getList() {
  loading.value = true
  listCounselorinfo(queryParams.value).then(response => {
    counselorinfoList.value = response.rows
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
    counselorId: null,
    userId: null,
    name: null,
    phone: null,
    email: null,
    office: null,
    status: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("counselorinfoRef")
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
  ids.value = selection.map(item => item.counselorId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  isEditMode.value = false
  loadUnbindUsers()
  open.value = true
  title.value = "添加辅导员管理"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  isEditMode.value = true
  const _counselorId = row.counselorId || ids.value
  getCounselorinfo(_counselorId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改辅导员管理"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["counselorinfoRef"].validate(valid => {
    if (valid) {
      if (form.value.counselorId != null) {
        updateCounselorinfo(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCounselorinfo(form.value).then(response => {
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
  const _counselorIds = row.counselorId || ids.value
  proxy.$modal.confirm('是否确认删除辅导员管理编号为"' + _counselorIds + '"的数据项？').then(function () {
    return delCounselorinfo(_counselorIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => { })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('counselor/counselorinfo/export', {
    ...queryParams.value
  }, `counselorinfo_${new Date().getTime()}.xlsx`)
}

/** 加载未绑定的用户列表 */
function loadUnbindUsers() {
  listUnbindUsers().then(response => {
    unbindUsers.value = response.data
  })
}

/** 处理用户选择变化 */
function handleUserChange(userId) {
  const selectedUser = unbindUsers.value.find(user => user.userId === userId)
  if (selectedUser) {
    form.value.userId = selectedUser.userId
  }
}

getList()
</script>
