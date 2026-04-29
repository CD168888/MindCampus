<template>
  <div class="knowledge-graph-container">
    <div ref="graphContainer" class="graph-canvas"></div>
    
    <!-- 工具栏 -->
    <div class="graph-toolbar">
      <el-button-group>
        <el-button icon="ZoomIn" @click="zoomIn" size="small" title="放大" />
        <el-button icon="ZoomOut" @click="zoomOut" size="small" title="缩小" />
        <el-button icon="Refresh" @click="fitView" size="small" title="适应画布" />
      </el-button-group>
      <el-button icon="FullScreen" @click="toggleFullscreen" size="small" title="全屏" />
      <el-button icon="Download" @click="exportImage" size="small" title="导出图片" />
    </div>

    <!-- 图例 -->
    <div class="graph-legend">
      <div class="legend-title">图例</div>
      <div class="legend-item" v-for="item in legendItems" :key="item.type">
        <span class="legend-color" :style="{ background: item.color }"></span>
        <span class="legend-label">{{ item.label }}</span>
      </div>
    </div>

    <!-- 节点详情面板 -->
    <div v-if="selectedNode" class="node-detail-panel">
      <div class="panel-header">
        <span>节点详情</span>
        <el-button icon="Close" link @click="selectedNode = null" />
      </div>
      <div class="panel-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="类型">
            <el-tag :type="getNodeTagType(selectedNode.type)">{{ getNodeTypeLabel(selectedNode.type) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="名称">{{ selectedNode.label }}</el-descriptions-item>
          <template v-if="selectedNode.data">
            <el-descriptions-item v-for="(value, key) in formatNodeData(selectedNode.data)" 
              :key="key" :label="key">{{ value }}</el-descriptions-item>
          </template>
        </el-descriptions>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载图谱数据...</span>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && nodes.length === 0" class="empty-state">
      <el-empty description="暂无图谱数据">
        <el-button type="primary" @click="$emit('sync')">同步数据</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as vis from 'vis-network/standalone';

const props = defineProps({
  nodes: {
    type: Array,
    default: () => []
  },
  edges: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['nodeClick', 'sync']);

const graphContainer = ref(null);
const selectedNode = ref(null);
let network = null;

const legendItems = [
  { type: 'student', label: '学生', color: '#4A90E2' },
  { type: 'assessment', label: '评估', color: '#50C878' },
  { type: 'emotion', label: '情绪', color: '#FFB347' },
  { type: 'interaction', label: '对话', color: '#9B59B6' },
  { type: 'profile', label: '画像', color: '#E74C3C' },
  { type: 'risk', label: '风险', color: '#E74C3C' },
  { type: 'counselor', label: '咨询师', color: '#1ABC9C' },
  { type: 'notification', label: '通知', color: '#95A5A6' },
];

function initGraph() {
  if (!graphContainer.value) return;

  // 转换节点数据
  const visNodes = new vis.DataSet(props.nodes.map(node => ({
    id: node.id,
    label: node.label,
    color: {
      background: node.color?.background || '#97C2FC',
      border: node.color?.border || '#2B7CE9',
      highlight: {
        background: node.color?.highlight || '#FFFF00',
        border: node.color?.border || '#2B7CE9'
      }
    },
    font: { color: '#343740', size: 14 },
    shape: getNodeShape(node.type),
    size: getNodeSize(node.type),
    data: node.data
  })));

  // 转换边数据
  const visEdges = new vis.DataSet(props.edges.map(edge => ({
    id: edge.id,
    from: edge.from,
    to: edge.to,
    label: edge.label,
    arrows: edge.arrows?.to ? 'to' : undefined,
    color: { color: '#848484', highlight: '#848484' },
    font: { align: 'middle', size: 12, color: '#666' },
    smooth: { type: 'continuous' }
  })));

  // 配置
  const options = {
    nodes: {
      borderWidth: 2,
      shadow: true
    },
    edges: {
      width: 2,
      shadow: true
    },
    physics: {
      enabled: true,
      barnesHut: {
        gravitationalConstant: -2000,
        centralGravity: 0.5,
        springLength: 150,
        springConstant: 0.04
      }
    },
    interaction: {
      hover: true,
      tooltipDelay: 200,
      hideEdgesOnDrag: true
    },
    layout: {
      improvedLayout: true
    }
  };

  // 创建网络
  network = new vis.Network(graphContainer.value, { nodes: visNodes, edges: visEdges }, options);

  // 事件监听
  network.on('click', (params) => {
    if (params.nodes.length > 0) {
      const nodeId = params.nodes[0];
      const node = props.nodes.find(n => n.id === nodeId);
      if (node) {
        selectedNode.value = node;
        emit('nodeClick', node);
      }
    }
  });

  network.on('doubleClick', (params) => {
    if (params.nodes.length > 0) {
      network.focus(params.nodes[0], {
        scale: 1.5,
        animation: true
      });
    }
  });
}

function getNodeShape(type) {
  const shapes = {
    student: 'ellipse',
    assessment: 'box',
    emotion: 'diamond',
    interaction: 'triangle',
    profile: 'star',
    risk: 'hexagon',
    counselor: 'text',
    notification: 'dot'
  };
  return shapes[type] || 'dot';
}

function getNodeSize(type) {
  const sizes = {
    student: 40,
    assessment: 25,
    emotion: 20,
    interaction: 18,
    profile: 28,
    risk: 25,
    counselor: 30,
    notification: 15
  };
  return sizes[type] || 20;
}

function getNodeTagType(type) {
  const types = {
    student: '',
    assessment: 'success',
    emotion: 'warning',
    interaction: 'primary',
    profile: 'danger',
    risk: 'danger',
    counselor: 'info',
    notification: 'info'
  };
  return types[type] || '';
}

function getNodeTypeLabel(type) {
  const labels = {
    student: '学生',
    assessment: '评估',
    emotion: '情绪',
    interaction: '对话',
    profile: '画像',
    risk: '风险',
    counselor: '咨询师',
    notification: '通知'
  };
  return labels[type] || type;
}

function formatNodeData(data) {
  if (!data) return {};
  const formatted = {};
  Object.keys(data).forEach(key => {
    if (key === 'userId' || key === 'studentId' || key === 'resultId' || key === 'sessionId') {
      formatted['ID'] = data[key];
    } else if (key === 'totalScore') {
      formatted['分数'] = data[key];
    } else if (key === 'riskLevel') {
      formatted['风险等级'] = data[key];
    } else if (key === 'intensity') {
      formatted['强度'] = data[key] + '/10';
    } else if (key === 'emotion') {
      formatted['情绪'] = data[key];
    } else if (key === 'name') {
      formatted['姓名'] = data[key];
    } else if (key === 'grade') {
      formatted['年级'] = data[key];
    } else if (key === 'major') {
      formatted['专业'] = data[key];
    } else if (key === 'personality') {
      formatted['性格特点'] = data[key] || '暂无';
    } else if (key === 'communicationStyle') {
      formatted['沟通风格'] = data[key] || '暂无';
    } else if (key === 'interests') {
      const interests = Array.isArray(data[key]) ? data[key].join('、') : (data[key] || '暂无');
      formatted['兴趣爱好'] = interests;
    } else if (key === 'concerns') {
      const concerns = Array.isArray(data[key]) ? data[key].join('、') : (data[key] || '暂无');
      formatted['关注事项'] = concerns;
    } else if (key === 'level') {
      formatted['风险等级'] = data[key];
    } else if (key === 'messageCount') {
      formatted['消息数'] = data[key];
    } else if (key === 'date') {
      formatted['日期'] = data[key];
    }
  });
  return formatted;
}

function zoomIn() {
  if (network) {
    const scale = network.getScale();
    network.moveTo({ scale: scale * 1.2 });
  }
}

function zoomOut() {
  if (network) {
    const scale = network.getScale();
    network.moveTo({ scale: scale / 1.2 });
  }
}

function fitView() {
  if (network) {
    network.fit({ animation: true });
  }
}

function toggleFullscreen() {
  const el = graphContainer.value;
  if (!document.fullscreenElement) {
    el.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
}

function exportImage() {
  if (network) {
    const canvas = graphContainer.value.querySelector('canvas');
    if (canvas) {
      const link = document.createElement('a');
      link.download = 'knowledge-graph.png';
      link.href = canvas.toDataURL('image/png');
      link.click();
    }
  }
}

// 监听数据变化
watch(() => [props.nodes, props.edges], () => {
  nextTick(() => {
    if (network) {
      network.destroy();
    }
    initGraph();
  });
}, { deep: true });

onMounted(() => {
  nextTick(() => {
    initGraph();
  });
});

onUnmounted(() => {
  if (network) {
    network.destroy();
    network = null;
  }
});
</script>

<style scoped>
.knowledge-graph-container {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 500px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
  border-radius: 8px;
  overflow: hidden;
}

.graph-canvas {
  width: 100%;
  height: 100%;
}

.graph-toolbar {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
  z-index: 10;
}

.graph-legend {
  position: absolute;
  top: 16px;
  right: 16px;
  background: white;
  padding: 12px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.legend-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
  font-size: 13px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-label {
  color: #606266;
}

.node-detail-panel {
  position: absolute;
  bottom: 16px;
  right: 16px;
  width: 320px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-weight: 600;
  color: #303133;
}

.panel-body {
  padding: 16px;
  max-height: 400px;
  overflow-y: auto;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
  z-index: 20;
}

.loading-overlay .el-icon {
  font-size: 32px;
  color: #409EFF;
  margin-bottom: 8px;
}

.empty-state {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
}
</style>
