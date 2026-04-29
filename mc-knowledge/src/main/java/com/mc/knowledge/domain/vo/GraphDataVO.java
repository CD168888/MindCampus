package com.mc.knowledge.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 图谱可视化数据VO
 * 用于前端 vis-network 绑定的图谱节点和边数据
 */
@Data
public class GraphDataVO {

    /**
     * 节点列表
     */
    private List<GraphNode> nodes;

    /**
     * 边列表
     */
    private List<GraphEdge> edges;

    @Data
    public static class GraphNode {
        /**
         * 节点ID
         */
        private String id;

        /**
         * 节点标签（显示文本）
         */
        private String label;

        /**
         * 节点类型：student, assessment, emotion, interaction, profile, risk, counselor, notification
         */
        private String type;

        /**
         * 节点数据（详细信息）
         */
        private Object data;

        /**
         * 颜色配置
         */
        private NodeColor color;
    }

    @Data
    public static class GraphEdge {
        /**
         * 边的ID
         */
        private String id;

        /**
         * 起始节点ID
         */
        private String from;

        /**
         * 目标节点ID
         */
        private String to;

        /**
         * 关系类型
         */
        private String type;

        /**
         * 关系标签（显示文本）
         */
        private String label;

        /**
         * 边的箭头方向
         */
        private EdgeArrows arrows;

        /**
         * 边数据（详细信息）
         */
        private Object data;
    }

    @Data
    public static class NodeColor {
        private String background;
        private String border;
        private String highlight;

        public static NodeColor of(String background, String border, String highlight) {
            NodeColor color = new NodeColor();
            color.setBackground(background);
            color.setBorder(border);
            color.setHighlight(highlight);
            return color;
        }
    }

    @Data
    public static class EdgeArrows {
        private String to;

        public static EdgeArrows directed() {
            EdgeArrows arrows = new EdgeArrows();
            arrows.setTo("enabled");
            return arrows;
        }
    }
}
