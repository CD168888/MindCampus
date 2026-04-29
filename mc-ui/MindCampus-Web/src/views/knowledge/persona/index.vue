<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="学号" prop="studentNo">
        <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="年级" prop="grade">
        <el-input v-model="queryParams.grade" placeholder="请输入年级" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" icon="Refresh" @click="handleFullSync">全量同步</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="studentList">
      <el-table-column label="学号" align="center" prop="studentNo" width="150" />
      <el-table-column label="姓名" align="center" prop="name" width="120" />
      <el-table-column label="性别" align="center" prop="gender" width="80">
        <template #default="scope">
          {{ scope.row.gender === '0' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column label="年级" align="center" prop="grade" width="100" />
      <el-table-column label="专业" align="center" prop="major" show-overflow-tooltip />
      <el-table-column label="班级" align="center" prop="className" width="150" />
      <el-table-column label="操作" align="center" width="280" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewProfile(scope.row)">查看图谱</el-button>
          <el-button link type="success" icon="DataAnalysis" @click="handleViewGraph(scope.row)">可视化</el-button>
          <el-button link type="warning" icon="Edit" @click="handleSyncStudent(scope.row)">同步</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-if="total > 0" :total="total" v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 图谱详情对话框 -->
    <el-dialog title="学生画像图谱" v-model="profileDialog.visible" width="900px" append-to-body>
      <el-descriptions :column="2" border v-if="profileDialog.data">
        <el-descriptions-item label="学号">{{ profileDialog.data.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ profileDialog.data.name }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ profileDialog.data.grade }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ profileDialog.data.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ profileDialog.data.className }}</el-descriptions-item>
        <el-descriptions-item label="最新风险等级">
          <el-tag :type="getRiskType(profileDialog.data.latestRiskLevel)">
            {{ profileDialog.data.latestRiskLevel || '暂无' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最新评估分数">
          {{ profileDialog.data.latestScore || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="最新情绪状态">
          {{ profileDialog.data.latestEmotion || '暂无' }}
          <span v-if="profileDialog.data.emotionIntensity">
            (强度 {{ profileDialog.data.emotionIntensity }}/10)
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="性格特点" :span="2">
          {{ profileDialog.data.personality || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="沟通风格" :span="2">
          {{ profileDialog.data.communicationStyle || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="兴趣爱好" :span="2">
          {{ profileDialog.data.interests && profileDialog.data.interests.length ? profileDialog.data.interests.join('、') : '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="关注事项" :span="2">
          {{ profileDialog.data.concerns && profileDialog.data.concerns.length ? profileDialog.data.concerns.join('、') : '暂无' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider>历史评估记录</el-divider>
      <el-table v-if="profileDialog.data && profileDialog.data.assessmentHistory" :data="profileDialog.data.assessmentHistory" size="small">
        <el-table-column prop="questionnaireTitle" label="问卷标题" show-overflow-tooltip />
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="riskLevel" label="风险等级" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getRiskType(scope.row.riskLevel)">{{ scope.row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assessmentDate" label="评估日期" width="160" />
      </el-table>
      <div v-else style="text-align: center; color: #999;">暂无历史评估记录</div>
    </el-dialog>

    <!-- 图谱可视化对话框 -->
    <el-dialog title="知识图谱可视化" v-model="graphDialog.visible" width="90%" top="2vh" append-to-body>
      <knowledge-graph 
        :nodes="graphDialog.nodes" 
        :edges="graphDialog.edges" 
        :loading="graphDialog.loading"
        @nodeClick="handleNodeClick"
        @sync="handleSyncStudent(graphDialog.currentRow)"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { getStudentProfile, getGraphData, getFullGraphData, triggerFullSync, syncStudent } from '@/api/knowledge/persona';
import { listInfo } from '@/api/student/info';
import KnowledgeGraph from '@/components/KnowledgeGraph/index.vue';
import { ElMessage } from 'element-plus';

const { proxy } = getCurrentInstance();

const loading = ref(false);
const total = ref(0);
const studentList = ref([]);
const showSearch = ref(true);
const queryRef = ref(null);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  studentNo: undefined,
  name: undefined,
  grade: undefined,
});

const profileDialog = reactive({
  visible: false,
  data: null,
});

const graphDialog = reactive({
  visible: false,
  loading: false,
  nodes: [],
  edges: [],
  currentRow: null,
});

function getList() {
  loading.value = true;
  listInfo(queryParams).then(response => {
    studentList.value = response.rows;
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
  queryParams.studentNo = undefined;
  queryParams.name = undefined;
  queryParams.grade = undefined;
  handleQuery();
}

function getRiskType(level) {
  if (!level) return 'info';
  if (level.includes('高')) return 'danger';
  if (level.includes('中')) return 'warning';
  return 'success';
}

function handleViewProfile(row) {
  getStudentProfile(row.userId).then(response => {
    profileDialog.data = response.data || {};
    profileDialog.visible = true;
  }).catch(() => {
    profileDialog.data = {};
    profileDialog.visible = true;
  });
}

function handleViewGraph(row) {
  graphDialog.visible = true;
  graphDialog.loading = true;
  graphDialog.nodes = [];
  graphDialog.edges = [];
  graphDialog.currentRow = row;
  
  getGraphData(row.userId).then(response => {
    if (response.data) {
      graphDialog.nodes = response.data.nodes || [];
      graphDialog.edges = response.data.edges || [];
    }
    graphDialog.loading = false;
  }).catch(() => {
    graphDialog.loading = false;
  });
}

function handleNodeClick(node) {
  console.log('点击节点:', node);
}

function handleSyncStudent(row) {
  proxy.$modal.confirm('确认同步该学生的图谱数据到 Neo4j？')
    .then(() => {
      return syncStudent(row.studentId);
    })
    .then(() => {
      ElMessage.success('同步成功');
    })
    .catch(() => {});
}

function handleFullSync() {
  proxy.$modal.confirm('确认执行全量同步？此操作将从 MySQL 同步所有学生数据到 Neo4j，可能需要较长时间。')
    .then(() => {
      proxy.$modal.loading('同步中，请稍候...');
      return triggerFullSync();
    })
    .then((response) => {
      proxy.$modal.closeLoading();
      if (response.code === 200) {
        ElMessage.success('全量同步完成');
      } else {
        ElMessage.error(response.msg || '同步失败');
      }
    })
    .catch(() => {
      proxy.$modal.closeLoading();
    });
}

onMounted(() => {
  getList();
});
</script>
