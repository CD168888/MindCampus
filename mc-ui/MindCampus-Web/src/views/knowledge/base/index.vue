<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="知识库名称" prop="kbName">
        <el-input v-model="queryParams.kbName" placeholder="请输入知识库名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入分类" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status" style="width: 280px;">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="0" />
          <el-option label="禁用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd"
          v-hasPermi="['knowledge:base:add']">新增知识库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
          v-hasPermi="['knowledge:base:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
          v-hasPermi="['knowledge:base:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格区域 -->
    <el-table v-loading="loading" :data="knowledgeBaseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="知识库ID" align="center" prop="kbId" width="100" />
      <el-table-column label="知识库名称" align="center" prop="kbName" show-overflow-tooltip />
      <el-table-column label="描述" align="center" prop="kbDesc" show-overflow-tooltip />
      <el-table-column label="分类" align="center" prop="category" width="150" />
      <el-table-column label="文档数量" align="center" prop="docCount" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="300" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewDocuments(scope.row)"
            v-hasPermi="['knowledge:document:query']">文档管理</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
            v-hasPermi="['knowledge:base:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
            v-hasPermi="['knowledge:base:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-if="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="知识库名称" prop="kbName">
          <el-input v-model="form.kbName" placeholder="请输入知识库名称" />
        </el-form-item>
        <el-form-item label="知识库描述" prop="kbDesc">
          <el-input v-model="form.kbDesc" type="textarea" :rows="3" placeholder="请输入知识库描述" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" clearable allow-create filterable>
            <el-option label="心理健康指南" value="心理健康指南" />
            <el-option label="干预方案" value="干预方案" />
            <el-option label="政策法规" value="政策法规" />
            <el-option label="科普知识" value="科普知识" />
            <el-option label="咨询记录" value="咨询记录" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 文档管理对话框 -->
    <el-dialog title="文档管理" v-model="docDialog.visible" width="900px" append-to-body>
      <div v-if="docDialog.kbName" style="margin-bottom: 16px; font-weight: 500;">
        当前知识库：{{ docDialog.kbName }}
      </div>
      <div style="margin-bottom: 16px;">
        <el-upload ref="uploadRef" :action="uploadUrl" :headers="uploadHeaders"
          :data="{ kbId: docDialog.kbId }" :before-upload="beforeUpload"
          :on-success="handleUploadSuccess" :on-error="handleUploadError"
          :auto-upload="false" :limit="5" accept=".txt,.md,.pdf,.docx,.doc">
          <el-button type="primary" plain icon="Files">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">支持 txt/md/pdf/docx 格式，单文件不超过 10MB</div>
          </template>
        </el-upload>
        <div style="margin-top: 8px;">
          <el-button type="primary" icon="Upload" @click="submitUpload">确认上传</el-button>
        </div>
      </div>
      <el-table v-loading="docLoading" :data="documentList">
        <el-table-column label="文件名" align="left" prop="fileName" show-overflow-tooltip />
        <el-table-column label="文件类型" align="center" prop="fileType" width="100" />
        <el-table-column label="文件大小" align="center" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="向量化状态" align="center" prop="embeddingStatus" width="140">
          <template #default="scope">
            <el-tag v-if="scope.row.embeddingStatus === '2'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.embeddingStatus === '1'" type="warning">处理中</el-tag>
            <el-tag v-else-if="scope.row.embeddingStatus === '3'" type="danger">失败</el-tag>
            <el-tag v-else type="info">待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文档块数" align="center" prop="chunkCount" width="100" />
        <el-table-column label="上传时间" align="center" prop="createTime" width="160" />
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button link type="primary" icon="Refresh" @click="handleReEmbed(scope.row)"
              :disabled="scope.row.embeddingStatus === '1'">重新向量化</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelDocument(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup name="KnowledgeBase">
import { listKnowledgeBase, addKnowledgeBase, updateKnowledgeBase, delKnowledgeBase } from '@/api/knowledge/base';
import { listDocument, uploadDocument, delDocument, reEmbedDocument } from '@/api/knowledge/document';
import { getToken } from '@/utils/auth';
import { getCurrentInstance } from 'vue';
const { proxy } = getCurrentInstance();

const loading = ref(true);
const docLoading = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const knowledgeBaseList = ref([]);
const documentList = ref([]);
const showSearch = ref(true);
const queryRef = ref(null);
const formRef = ref(null);
const uploadRef = ref(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  kbName: undefined,
  category: undefined,
  status: undefined,
});

const form = ref({
  kbId: undefined,
  kbName: '',
  kbDesc: '',
  category: '',
  status: '0',
});

const dialog = reactive({
  visible: false,
  title: '',
});

const docDialog = reactive({
  visible: false,
  kbId: undefined,
  kbName: '',
});

const rules = {
  kbName: [{ required: true, message: '知识库名称不能为空', trigger: 'blur' }],
};

const uploadUrl = import.meta.env.VITE_APP_BASE_API + '/knowledge/document/upload';
const uploadHeaders = { Authorization: 'Bearer ' + getToken() };

function formatFileSize(size) {
  if (!size) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(size) / Math.log(k));
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

function getList() {
  loading.value = true;
  listKnowledgeBase(queryParams).then(response => {
    knowledgeBaseList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}

function resetQuery() {
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  queryParams.kbName = undefined;
  queryParams.category = undefined;
  queryParams.status = undefined;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.kbId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function handleAdd() {
  form.value = { kbId: undefined, kbName: '', kbDesc: '', category: '', status: '0' };
  dialog.title = '新增知识库';
  dialog.visible = true;
}

function handleUpdate(row) {
  const item = row.kbId ? row : knowledgeBaseList.value.find(v => v.kbId === row.kbId);
  form.value = { ...item };
  dialog.title = '修改知识库';
  dialog.visible = true;
}

function submitForm() {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.kbId) {
        updateKnowledgeBase(form.value).then(response => {
          proxy.$modal.msgSuccess('修改成功');
          dialog.visible = false;
          getList();
        });
      } else {
        addKnowledgeBase(form.value).then(response => {
          proxy.$modal.msgSuccess('新增成功');
          dialog.visible = false;
          getList();
        });
      }
    }
  });
}

function handleDelete(row) {
  const kbs = row.kbId ? [row.kbId] : ids.value;
  proxy.$modal.confirm('是否确认删除所选知识库？删除知识库将同时删除其下所有文档。').then(() => {
    return delKnowledgeBase(kbs);
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功');
    getList();
  }).catch(() => {});
}

function handleViewDocuments(row) {
  docDialog.kbId = row.kbId;
  docDialog.kbName = row.kbName;
  docDialog.visible = true;
  loadDocuments();
}

function loadDocuments() {
  docLoading.value = true;
  listDocument({ kbId: docDialog.kbId }).then(response => {
    documentList.value = response.rows;
    docLoading.value = false;
  });
}

function beforeUpload(file) {
  const isValidSize = file.size / 1024 / 1024 < 10;
  const isValidType = ['.txt', '.md', '.pdf', '.docx', '.doc'].some(ext =>
    file.name.toLowerCase().endsWith(ext)
  );
  if (!isValidType) {
    proxy.$modal.msgError('只支持 txt/md/pdf/docx/doc 格式的文件');
    return false;
  }
  if (!isValidSize) {
    proxy.$modal.msgError('文件大小不能超过 10MB');
    return false;
  }
  return true;
}

function submitUpload() {
  uploadRef.value.submit();
}

function handleUploadSuccess(response) {
  proxy.$modal.msgSuccess('文件上传成功，正在后台向量化中...');
  setTimeout(loadDocuments, 2000);
}

function handleUploadError() {
  proxy.$modal.msgError('文件上传失败');
}

function handleReEmbed(row) {
  proxy.$modal.confirm('确定要重新向量化该文档吗？').then(() => {
    return reEmbedDocument(row.docId);
  }).then(() => {
    proxy.$modal.msgSuccess('重新向量化任务已启动');
    loadDocuments();
  }).catch(() => {});
}

function handleDelDocument(row) {
  proxy.$modal.confirm('确定要删除该文档吗？').then(() => {
    return delDocument(row.docId);
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功');
    loadDocuments();
  }).catch(() => {});
}

onMounted(() => {
  getList();
});
</script>
