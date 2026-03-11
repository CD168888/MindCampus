<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>风险等级配置</span>
          <el-button v-if="!editing" type="primary" @click="handleEdit">
            编辑
          </el-button>
          <div v-else>
            <el-button type="success" @click="handleSave">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column label="风险等级" prop="riskLevel" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRiskTagType(row.riskLevel)">{{ row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分数区间" width="250" align="center">
          <template #default="{ row, $index }">
            <div class="score-range" v-if="editing">
              <el-input-number
                v-model="row.minScore"
                :min="0"
                :max="100"
                size="small"
                controls-position="right"
                @change="handleScoreChange($index)"
              />
              <span class="separator">-</span>
              <el-input-number
                v-model="row.maxScore"
                :min="0"
                :max="100"
                size="small"
                controls-position="right"
                @change="handleScoreChange($index)"
              />
            </div>
            <span v-else>{{ row.minScore }} - {{ row.maxScore }}</span>
          </template>
        </el-table-column>
        <el-table-column label="通知模板" min-width="300">
          <template #default="{ row }">
            <el-input
              v-if="editing"
              v-model="row.notificationTemplate"
              type="textarea"
              :rows="2"
              placeholder="通知模板"
            />
            <span v-else class="template-text">{{ row.notificationTemplate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="0"
              inactive-value="1"
              :disabled="!editing"
            />
            <span class="status-text">{{ row.status === '0' ? '正常' : '停用' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="tips" v-if="editing">
        <el-alert
          title="分数区间说明：低风险分数必须小于中风险，中风险分数必须小于高风险，且三个区间的总分范围为 0-100"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </el-card>
  </div>
</template>

<script setup name="RiskConfig">
import {getRiskConfig, updateRiskConfig} from '@/api/intervention/riskConfig'

const { proxy } = getCurrentInstance()

const loading = ref(false)
const editing = ref(false)

// 表格数据
const tableData = ref([
  {
    configId: null,
    riskLevel: '低',
    minScore: 0,
    maxScore: 60,
    notificationTemplate: '',
    status: '0'
  },
  {
    configId: null,
    riskLevel: '中',
    minScore: 61,
    maxScore: 80,
    notificationTemplate: '',
    status: '0'
  },
  {
    configId: null,
    riskLevel: '高',
    minScore: 81,
    maxScore: 100,
    notificationTemplate: '',
    status: '0'
  }
])

// 保存编辑前的数据
let originalData = []

/** 获取风险标签类型 */
function getRiskTagType(riskLevel) {
  const types = {
    '低': 'success',
    '中': 'warning',
    '高': 'danger'
  }
  return types[riskLevel] || 'info'
}

/** 分数变化时自动调整 */
function clamp(value, min, max) {
  return value < min ? min : value > max ? max : value
}

function handleScoreChange(index) {
  const [low, medium, high] = tableData.value

  const normalize = item => {
    item.minScore = clamp(item.minScore, 0, 100)
    item.maxScore = clamp(Math.max(item.minScore, item.maxScore), item.minScore, 100)
  }

  normalize(low)
  normalize(medium)
  normalize(high)

  if (index === 0) {
    medium.minScore = low.maxScore + 1
    normalize(medium)
    high.minScore = medium.maxScore + 1
    normalize(high)
  }

  if (index === 1) {
    low.maxScore = medium.minScore - 1
    normalize(low)
    high.minScore = medium.maxScore + 1
    normalize(high)
  }

  if (index === 2) {
    medium.maxScore = high.minScore - 1
    normalize(medium)
    low.maxScore = medium.minScore - 1
    normalize(low)
  }
}

/** 获取配置 */
function getConfig() {
  loading.value = true
  getRiskConfig().then(response => {
    const configs = response.data || []
    if (configs.length > 0) {
      // 按低、中、高排序
      const levelOrder = { '低': 0, '中': 1, '高': 2 }
      configs.sort((a, b) => levelOrder[a.riskLevel] - levelOrder[b.riskLevel])

      tableData.value = configs
    }
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 编辑 */
function handleEdit() {
  originalData = JSON.parse(JSON.stringify(tableData.value))
  editing.value = true
}

/** 取消编辑 */
function handleCancel() {
  tableData.value = originalData
  editing.value = false
}

/** 保存 */
function handleSave() {
  // 验证分数连续性
  const sortedData = [...tableData.value].sort((a, b) => a.minScore - b.minScore)

  for (let i = 0; i < sortedData.length - 1; i++) {
    if (sortedData[i].maxScore >= sortedData[i + 1].minScore) {
      proxy.$modal.msgError('分数区间必须连续，不能有重叠或断层')
      return
    }
  }

  // 验证分数范围覆盖 0-100
  const minScore = Math.min(...tableData.value.map(item => item.minScore))
  const maxScore = Math.max(...tableData.value.map(item => item.maxScore))

  if (minScore !== 0 || maxScore !== 100) {
    proxy.$modal.msgError('分数区间必须覆盖 0-100')
    return
  }

  // 逐个保存配置
  loading.value = true
  const savePromises = tableData.value.map(item => {
    const data = {
      configId: item.configId,
      riskLevel: item.riskLevel,
      minScore: item.minScore,
      maxScore: item.maxScore,
      notificationTemplate: item.notificationTemplate,
      status: item.status
    }
    return updateRiskConfig(data)
  })

  Promise.all(savePromises).then(() => {
    proxy.$modal.msgSuccess('保存成功')
    editing.value = false
    getConfig()
  }).catch(() => {
    loading.value = false
  })
}

onMounted(() => {
  getConfig()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-range {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.score-range .el-input-number {
  width: 90px;
}

.separator {
  color: #909399;
}

.status-text {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}

.template-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tips {
  margin-top: 20px;
}

:deep(.el-table .cell) {
  padding-left: 8px;
  padding-right: 8px;
}
</style>
