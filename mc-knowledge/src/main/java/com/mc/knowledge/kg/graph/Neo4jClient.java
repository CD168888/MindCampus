package com.mc.knowledge.kg.graph;

import com.mc.knowledge.domain.vo.GraphDataVO;
import com.mc.knowledge.domain.vo.StudentProfileNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Neo4j 图数据库客户端封装
 *
 * @author MindCampus
 */
@Component
@Slf4j
public class Neo4jClient {

    @Resource
    private Driver neo4jDriver;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 500;

    private static final Set<Class<? extends Exception>> RETRYABLE_EXCEPTIONS = Set.of(
            org.neo4j.driver.exceptions.ServiceUnavailableException.class,
            org.neo4j.driver.exceptions.SessionExpiredException.class,
            java.net.SocketException.class,
            java.io.IOException.class,
            io.netty.channel.ChannelException.class
    );

    /**
     * 带重试的 Neo4j 会话执行
     */
    private void executeWithRetry(String cypher, org.neo4j.driver.Value parameters) {
        Exception lastException = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (Session session = neo4jDriver.session()) {
                session.run(cypher, parameters);
                return;
            } catch (Exception e) {
                lastException = e;
                if (!isRetryable(e) || attempt == MAX_RETRIES) {
                    throw e;
                }
                log.warn("[Neo4j] 连接异常，第 {} 次重试（{}ms 后）: {}", attempt, RETRY_DELAY_MS, e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试被中断", ie);
                }
            }
        }
        throw new RuntimeException("Neo4j 操作失败，已重试 " + MAX_RETRIES + " 次", lastException);
    }

    private boolean isRetryable(Exception e) {
        if (RETRYABLE_EXCEPTIONS.contains(e.getClass())) return true;
        Throwable cause = e.getCause();
        while (cause != null) {
            if (RETRYABLE_EXCEPTIONS.contains(cause.getClass())) return true;
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 创建或更新学生节点
     */
    public void upsertStudent(Long userId, Long studentId, String studentNo, String name, String grade, String major, String className) {
        String cypher = """
            MERGE (s:Student {userId: $userId})
            SET s.studentId = coalesce($studentId, s.studentId),
                s.studentNo = $studentNo,
                s.name = $name,
                s.grade = $grade,
                s.major = $major,
                s.className = $className,
                s.updatedAt = datetime()
            """;
        try {
            executeWithRetry(cypher,
                org.neo4j.driver.Values.parameters(
                    "userId", userId,
                    "studentId", studentId,
                    "studentNo", studentNo != null ? studentNo : "",
                    "name", name,
                    "grade", grade != null ? grade : "",
                    "major", major != null ? major : "",
                    "className", className != null ? className : ""
                ));
            log.info("[Neo4j] 创建/更新学生节点完成 - userId: {}, studentNo: {}", userId, studentNo);
        } catch (Exception e) {
            log.error("[Neo4j] 创建学生节点失败 - userId: {}", userId, e);
            throw new RuntimeException("Neo4j 图谱操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加评估记录节点（使用MERGE避免重复）
     */
    public void addAssessment(Long userId, Long resultId, String questionnaireTitle, Integer totalScore, String riskLevel) {
        String cypher = """
            MATCH (s:Student {userId: $userId})
            MERGE (a:Assessment {resultId: $resultId})
            SET a.questionnaireTitle = $questionnaireTitle,
                a.totalScore = $totalScore,
                a.riskLevel = $riskLevel,
                a.date = datetime()
            MERGE (s)-[:TOOK_ASSESSMENT]->(a)
            """;
        try {
            executeWithRetry(cypher,
                org.neo4j.driver.Values.parameters(
                    "userId", userId,
                    "resultId", resultId,
                    "questionnaireTitle", questionnaireTitle != null ? questionnaireTitle : "",
                    "totalScore", totalScore != null ? totalScore : 0,
                    "riskLevel", riskLevel != null ? riskLevel : "未知"
                ));
            log.info("[Neo4j] 添加/更新评估记录 - userId: {}, resultId: {}", userId, resultId);
        } catch (Exception e) {
            log.error("[Neo4j] 添加评估记录失败", e);
            throw new RuntimeException("Neo4j 图谱操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加情绪状态节点
     */
    public void addEmotionState(Long userId, String emotion, Integer intensity) {
        String cypher = """
            MATCH (s:Student {userId: $userId})
            CREATE (s)-[:EXPRESSED_EMOTION]->(e:EmotionState {
                emotion: $emotion,
                intensity: $intensity,
                date: datetime()
            })
            """;
        try {
            executeWithRetry(cypher,
                org.neo4j.driver.Values.parameters(
                    "userId", userId,
                    "emotion", emotion != null ? emotion : "未知",
                    "intensity", intensity != null ? intensity : 5
                ));
            log.info("[Neo4j] 添加情绪状态 - userId: {}, emotion: {}", userId, emotion);
        } catch (Exception e) {
            log.error("[Neo4j] 添加情绪状态失败", e);
            throw new RuntimeException("Neo4j 图谱操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 添加/更新对话交互统计
     */
    public void upsertInteraction(Long userId, Long sessionId, Integer messageCount, Double avgScore) {
        String cypher = """
            MATCH (s:Student {userId: $userId})
            MERGE (s)-[r:INTERACTED_IN]->(i:Interaction {sessionId: $sessionId})
            SET r.messageCount = $messageCount,
                r.avgScore = $avgScore,
                r.lastDate = datetime()
            """;
        try {
            executeWithRetry(cypher,
                org.neo4j.driver.Values.parameters(
                    "userId", userId,
                    "sessionId", sessionId,
                    "messageCount", messageCount != null ? messageCount : 0,
                    "avgScore", avgScore != null ? avgScore : 5.0
                ));
            log.info("[Neo4j] 更新对话交互 - userId: {}, sessionId: {}", userId, sessionId);
        } catch (Exception e) {
            log.error("[Neo4j] 更新对话交互失败", e);
            throw new RuntimeException("Neo4j 图谱操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建或更新性格画像节点
     */
    public void upsertProfile(Long userId, String personality, String communicationStyle,
                              List<String> interests, List<String> concerns) {
        String cypher = """
            MATCH (s:Student {userId: $userId})
            MERGE (s)-[:HAS_PROFILE]->(p:Profile)
            SET p.personality = $personality,
                p.communicationStyle = $communicationStyle,
                p.interests = $interests,
                p.concerns = $concerns,
                p.updatedAt = datetime()
            """;
        try {
            executeWithRetry(cypher,
                org.neo4j.driver.Values.parameters(
                    "userId", userId,
                    "personality", personality != null ? personality : "",
                    "communicationStyle", communicationStyle != null ? communicationStyle : "",
                    "interests", interests != null ? interests : Collections.emptyList(),
                    "concerns", concerns != null ? concerns : Collections.emptyList()
                ));
            log.info("[Neo4j] 更新性格画像 - userId: {}", userId);
        } catch (Exception e) {
            log.error("[Neo4j] 更新性格画像失败", e);
            throw new RuntimeException("Neo4j 图谱操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询学生完整画像
     */
    public StudentProfileNode queryProfile(Long userId) {
        String cypher = """
            MATCH (s:Student {userId: $userId})
            OPTIONAL MATCH (s)-[:HAS_PROFILE]->(p:Profile)
            OPTIONAL MATCH (s)-[ra:TOOK_ASSESSMENT]->(a:Assessment)
            OPTIONAL MATCH (s)-[:EXPRESSED_EMOTION]->(e:EmotionState)
            OPTIONAL MATCH (s)-[ri:HAS_RISK]->(risk:Risk)
            OPTIONAL MATCH (s)-[:ASSIGNED_TO]->(c:Counselor)
            OPTIONAL MATCH (s)-[:RECEIVED]->(n:Notification)
            WITH s, p, a, e, ri, risk, c, n,
                 [(s)-[in:INTERACTED_IN]->(i:Interaction) | {sessionId: i.sessionId, msgCount: in.messageCount, avgScore: in.avgScore, lastDate: in.lastDate}] as interactions
            RETURN s, p,
                   collect(DISTINCT {resultId: a.resultId, title: a.questionnaireTitle, score: a.totalScore, risk: a.riskLevel, date: a.date}) as assessments,
                   collect(DISTINCT {emotion: e.emotion, intensity: e.intensity, date: e.date}) as emotions,
                   collect(DISTINCT {level: risk.level, score: ri.score, triggers: ri.triggerEvents}) as risks,
                   collect(DISTINCT {notificationId: n.notificationId, type: n.type, status: n.status}) as notifications,
                   interactions
            """;
        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("userId", userId));
            if (!result.hasNext()) {
                return null;
            }
            Record record = result.next();
            StudentProfileNode profile = parseProfileRecord(record, userId);
            if (profile == null) {
                return null;
            }
            log.debug("[Neo4j] 查询画像 - userId: {}, assessmentHistory.size: {}, latestScore: {}, riskLevel: {}",
                userId,
                profile.getAssessmentHistory() != null ? profile.getAssessmentHistory().size() : 0,
                profile.getLatestScore(),
                profile.getRiskLevel());
            return profile;
        } catch (Exception e) {
            log.error("[Neo4j] 查询学生画像失败 - userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 查询学生情绪历史（最近 N 条）
     */
    public String queryEmotionHistory(Long userId, int limit) {
        String cypher = """
            MATCH (s:Student {userId: $userId})-[r:EXPRESSED_EMOTION]->(e:EmotionState)
            RETURN e.emotion as emotion, e.intensity as intensity, e.date as date
            ORDER BY e.date DESC
            LIMIT $limit
            """;
        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("userId", userId, "limit", limit));
            StringBuilder sb = new StringBuilder();
            int idx = 1;
            while (result.hasNext()) {
                Record r = result.next();
                String emotion = r.get("emotion").asString();
                int intensity = r.get("intensity").asInt(5);
                String date = r.get("date").asObject() != null
                    ? r.get("date").asObject().toString().replace("java.time.LocalDateTime", "").trim()
                    : "未知";
                sb.append(String.format("%d. 情绪: %s, 强度: %d/10", idx++, emotion, intensity));
                if (result.hasNext()) sb.append("; ");
            }
            return sb.length() > 0 ? sb.toString() : "暂无情绪记录";
        } catch (Exception e) {
            log.error("[Neo4j] 查询情绪历史失败 - userId: {}", userId, e);
            return "暂无情绪记录";
        }
    }

    /**
     * 查询学生风险趋势（最近 N 条评估）
     */
    public String queryRiskTrend(Long userId) {
        String cypher = """
            MATCH (s:Student {userId: $userId})-[:TOOK_ASSESSMENT]->(a:Assessment)
            RETURN a.totalScore as score, a.riskLevel as riskLevel, a.date as date, a.questionnaireTitle as title
            ORDER BY a.date DESC
            LIMIT 10
            """;
        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("userId", userId));
            StringBuilder sb = new StringBuilder();
            while (result.hasNext()) {
                Record r = result.next();
                Integer score = r.get("score").asInt(0);
                String risk = r.get("riskLevel").asString("未知");
                String title = r.get("title").asString();
                sb.append(String.format("[%s] 分数: %d, 风险: %s; ",
                    title != null ? title : "评估", score, risk));
            }
            return sb.length() > 0 ? sb.toString() : "暂无风险评估记录";
        } catch (Exception e) {
            log.error("[Neo4j] 查询风险趋势失败 - userId: {}", userId, e);
            return "暂无风险评估记录";
        }
    }

    /**
     * 检查学生节点是否存在
     */
    public boolean studentExists(Long userId) {
        String cypher = "MATCH (s:Student {userId: $userId}) RETURN s LIMIT 1";
        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("userId", userId));
            return result.hasNext();
        } catch (Exception e) {
            log.error("[Neo4j] 检查学生节点失败 - userId: {}", userId, e);
            return false;
        }
    }

    private StudentProfileNode parseProfileRecord(Record record, Long userId) {
        StudentProfileNode.StudentProfileNodeBuilder builder = StudentProfileNode.builder().userId(userId);

        Value sVal = record.get("s");
        if (!sVal.isNull()) {
            Map<String, Object> s = sVal.asMap();
            builder.name(getString(s, "name"))
                   .studentNo(getString(s, "studentNo"))
                   .grade(getString(s, "grade"))
                   .major(getString(s, "major"))
                   .className(getString(s, "className"));
        }

        Value pVal = record.get("p");
        if (!pVal.isNull()) {
            Map<String, Object> p = pVal.asMap();
            builder.personality(getString(p, "personality"))
                   .communicationStyle(getString(p, "communicationStyle"))
                   .interests(toStringList(p.get("interests")))
                   .concerns(toStringList(p.get("concerns")));
        }

        Value assessmentsVal = record.get("assessments");
        List<Object> assessments = assessmentsVal.isNull() ? Collections.emptyList() : assessmentsVal.asList();
        log.info("[Neo4j] 原始 assessments 数据 - isNull: {}, size: {}", assessmentsVal.isNull(), assessments.size());
        if (assessments != null && !assessments.isEmpty()) {
            List<StudentProfileNode.AssessmentRecord> assessmentRecords = new ArrayList<>();
            for (Object obj : assessments) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> a = (Map<String, Object>) obj;
                    Object resultId = a.get("resultId");
                    String dateStr = a.get("date") != null ? a.get("date").toString() : null;
                    assessmentRecords.add(StudentProfileNode.AssessmentRecord.builder()
                        .resultId(resultId instanceof Number ? ((Number) resultId).longValue() : null)
                        .questionnaireTitle(getString(a, "title"))
                        .totalScore(getInt(a, "score"))
                        .riskLevel(getString(a, "risk"))
                        .assessmentDate(dateStr)
                        .build());
                }
            }
            builder.assessmentHistory(assessmentRecords);

            // 最近一次评估（按 resultId 降序取最新的）
            StudentProfileNode.AssessmentRecord latest = assessmentRecords.stream()
                .filter(r -> r.getResultId() != null)
                .max((r1, r2) -> Long.compare(r1.getResultId(), r2.getResultId()))
                .orElse(assessmentRecords.isEmpty() ? null : assessmentRecords.get(0));
            log.info("[Neo4j] 评估记录解析 - 共 {} 条, latest: resultId={}, score={}, risk={}, title={}",
                assessmentRecords.size(),
                latest != null ? latest.getResultId() : null,
                latest != null ? latest.getTotalScore() : null,
                latest != null ? latest.getRiskLevel() : null,
                latest != null ? latest.getQuestionnaireTitle() : null);
            if (latest != null) {
                builder.latestScore(latest.getTotalScore())
                       .riskLevel(latest.getRiskLevel())
                       .latestAssessmentDate(latest.getAssessmentDate());
            }
        } else {
            log.debug("[Neo4j] 评估记录为空 - userId: {}", userId);
        }

        List<Object> emotions = record.get("emotions").asList();
        if (emotions != null && !emotions.isEmpty()) {
            for (Object obj : emotions) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> e = (Map<String, Object>) obj;
                    builder.latestEmotion(getString(e, "emotion"))
                           .emotionIntensity(getInt(e, "intensity"));
                    break;
                }
            }
        }

        List<Object> interactions = record.get("interactions").asList();
        if (interactions != null && !interactions.isEmpty()) {
            List<StudentProfileNode.InteractionSummary> summaries = new ArrayList<>();
            for (Object obj : interactions) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> i = (Map<String, Object>) obj;
                    summaries.add(StudentProfileNode.InteractionSummary.builder()
                        .totalSessions(summaries.size() + 1)
                        .totalMessages(getInt(i, "msgCount"))
                        .avgScore(getDouble(i, "avgScore"))
                        .lastInteractionDate(i.get("lastDate") != null ? i.get("lastDate").toString() : null)
                        .build());
                }
            }
            if (!summaries.isEmpty()) {
                builder.interactionSummary(summaries.get(0));
            }
        }

        List<Object> notifications = record.get("notifications").asList();
        if (notifications != null) {
            builder.unprocessedNotifications(
                notifications.stream()
                    .filter(n -> n instanceof Map)
                    .filter(n -> !"1".equals(((Map<?, ?>) n).get("status")))
                    .count()
            );
        }

        return builder.build();
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return null;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        return null;
    }

    private List<String> toStringList(Object val) {
        if (val == null) return Collections.emptyList();
        if (val instanceof List) {
            List<?> list = (List<?>) val;
            List<String> result = new ArrayList<>();
            for (Object item : list) {
                if (item != null) result.add(item.toString());
            }
            return result;
        }
        return Collections.emptyList();
    }

    // ==================== 图谱可视化数据导出 ====================

    /**
     * 导出指定学生的图谱可视化数据
     */
    public GraphDataVO exportGraphData(Long userId) {
        GraphDataVO graphData = new GraphDataVO();
        List<GraphDataVO.GraphNode> nodes = new ArrayList<>();
        List<GraphDataVO.GraphEdge> edges = new ArrayList<>();

        String cypher = """
            MATCH (s:Student {userId: $userId})
            OPTIONAL MATCH path = (s)-[r1]-(n)
            RETURN s, nodes(path) as relatedNodes, relationships(path) as rels
            """;

        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("userId", userId));

            Set<String> nodeIds = new HashSet<>();
            while (result.hasNext()) {
                Record record = result.next();

                // 处理学生节点
                Node studentNode = record.get("s").asNode();
                String studentId = "student_" + userId;
                if (!nodeIds.contains(studentId)) {
                    nodes.add(createNode(studentId, studentNode.get("name").asString("学生"),
                        "student", studentNode.asMap()));
                    nodeIds.add(studentId);
                }

                // 处理关联节点和边
                List<Object> relatedNodes = record.get("relatedNodes").asList();
                List<Object> rels = record.get("rels").asList();

                if (rels != null) {
                    for (Object relObj : rels) {
                        if (relObj instanceof Relationship) {
                            Relationship rel = (Relationship) relObj;
                            String targetNodeId = null;
                            Object endNode = null;

                            // 获取关系的目标节点
                            for (Object nodeObj : relatedNodes) {
                                if (nodeObj instanceof Node) {
                                    Node n = (Node) nodeObj;
                                    Map<String, Object> props = n.asMap();
                                    // 检查节点是否是关系的目标
                                    String relType = rel.type();
                                    if (relType.equals("TOOK_ASSESSMENT") || relType.equals("EXPRESSED_EMOTION") ||
                                        relType.equals("INTERACTED_IN") || relType.equals("HAS_PROFILE") ||
                                        relType.equals("HAS_RISK") || relType.equals("ASSIGNED_TO") ||
                                        relType.equals("RECEIVED")) {
                                        if (props.containsKey("resultId")) {
                                            targetNodeId = "assessment_" + n.get("resultId").asLong();
                                        } else if (props.containsKey("emotion")) {
                                            Object dateVal = props.get("date");
                                            String dateStr = dateVal != null ? dateVal.toString() : String.valueOf(props.hashCode());
                                            targetNodeId = "emotion_" + dateStr.hashCode();
                                        } else if (props.containsKey("sessionId")) {
                                            targetNodeId = "interaction_" + n.get("sessionId").asLong();
                                        } else if (props.containsKey("personality")) {
                                            targetNodeId = "profile_" + userId;
                                        } else if (props.containsKey("level")) {
                                            targetNodeId = "risk_" + userId;
                                        }
                                    }
                                }
                            }

                            // 添加关联节点
                            if (targetNodeId != null && !nodeIds.contains(targetNodeId)) {
                                for (Object nodeObj : relatedNodes) {
                                    if (nodeObj instanceof Node) {
                                        Node n = (Node) nodeObj;
                                        Map<String, Object> props = n.asMap();
                                        // 评估节点
                                        if (props.containsKey("resultId") && targetNodeId.equals("assessment_" + n.get("resultId").asLong())) {
                                            nodes.add(createNode(targetNodeId, n.get("questionnaireTitle").asString("评估"),
                                                "assessment", props));
                                            nodeIds.add(targetNodeId);
                                            break;
                                        }
                                        // 情绪节点
                                        if (props.containsKey("emotion") && targetNodeId.startsWith("emotion_")) {
                                            String emotionName = n.get("emotion").asString("情绪");
                                            nodes.add(createNode(targetNodeId, emotionName, "emotion", props));
                                            nodeIds.add(targetNodeId);
                                            break;
                                        }
                                        // 对话节点
                                        if (props.containsKey("sessionId") && targetNodeId.equals("interaction_" + n.get("sessionId").asLong())) {
                                            String label = "对话";
                                            Integer msgCount = getInt(props, "messageCount");
                                            if (msgCount != null) {
                                                label = "对话(" + msgCount + "条)";
                                            }
                                            nodes.add(createNode(targetNodeId, label, "interaction", props));
                                            nodeIds.add(targetNodeId);
                                            break;
                                        }
                                        // 性格特点节点
                                        if (props.containsKey("personality") && targetNodeId.equals("profile_" + userId)) {
                                            nodes.add(createNode(targetNodeId, "性格特点", "profile", props));
                                            nodeIds.add(targetNodeId);
                                            break;
                                        }
                                        // 风险等级节点
                                        if (props.containsKey("level") && targetNodeId.equals("risk_" + userId)) {
                                            String levelName = n.get("level").asString("风险");
                                            nodes.add(createNode(targetNodeId, "风险等级:" + levelName, "risk", props));
                                            nodeIds.add(targetNodeId);
                                            break;
                                        }
                                    }
                                }
                            }

                            // 添加边（使用 System.identityHashCode 作为唯一ID）
                            String edgeId = "edge_" + System.identityHashCode(rel);
                            edges.add(createEdge(edgeId, studentId, targetNodeId != null ? targetNodeId : "", rel.type(), rel.asMap()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[Neo4j] 导出图谱可视化数据失败 - userId: {}", userId, e);
        }

        graphData.setNodes(nodes);
        graphData.setEdges(edges);
        return graphData;
    }

    /**
     * 导出图谱的全量子图数据（用于全局视图）
     */
    public GraphDataVO exportFullGraphData(int limit) {
        GraphDataVO graphData = new GraphDataVO();
        List<GraphDataVO.GraphNode> nodes = new ArrayList<>();
        List<GraphDataVO.GraphEdge> edges = new ArrayList<>();

        String cypher = """
            MATCH (s:Student)
            OPTIONAL MATCH (s)-[r1]-(n)
            WITH s, collect(DISTINCT n) as relatedNodes, collect(DISTINCT r1) as rels
            WHERE s IS NOT NULL
            RETURN s, relatedNodes, rels
            LIMIT $limit
            """;

        try (Session session = neo4jDriver.session()) {
            var result = session.run(cypher,
                org.neo4j.driver.Values.parameters("limit", limit));

            Set<String> nodeIds = new HashSet<>();
            while (result.hasNext()) {
                Record record = result.next();

                Node studentNode = record.get("s").asNode();
                Long userId = studentNode.get("userId").asLong();
                String studentId = "student_" + userId;

                if (!nodeIds.contains(studentId)) {
                    nodes.add(createNode(studentId, studentNode.get("name").asString("学生"),
                        "student", studentNode.asMap()));
                    nodeIds.add(studentId);
                }

                List<Object> relatedNodes = record.get("relatedNodes").asList();
                List<Object> rels = record.get("rels").asList();

                for (int i = 0; i < rels.size(); i++) {
                    Object relObj = rels.get(i);
                    if (relObj instanceof Relationship) {
                        Relationship rel = (Relationship) relObj;
                        String targetId = generateNodeId(rel, relatedNodes);

                        if (targetId != null && !nodeIds.contains(targetId)) {
                            for (Object nodeObj : relatedNodes) {
                                if (nodeObj instanceof Node) {
                                    Node n = (Node) nodeObj;
                                    String nid = getNodeIdFromNode(n);
                                    if (nid != null && nid.equals(targetId)) {
                                        nodes.add(createNode(targetId, getNodeLabel(n), getNodeType(rel.type()), n.asMap()));
                                        nodeIds.add(targetId);
                                        break;
                                    }
                                }
                            }
                        }

                        // 添加边（使用 System.identityHashCode 作为唯一ID）
                        String edgeId = "edge_" + System.identityHashCode(rel);
                        edges.add(createEdge(edgeId, studentId, targetId != null ? targetId : "", rel.type(), rel.asMap()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("[Neo4j] 导出全量图谱数据失败", e);
        }

        graphData.setNodes(nodes);
        graphData.setEdges(edges);
        return graphData;
    }

    private String generateNodeId(Relationship rel, List<Object> relatedNodes) {
        String relType = rel.type();
        for (Object nodeObj : relatedNodes) {
            if (nodeObj instanceof Node) {
                Node n = (Node) nodeObj;
                Map<String, Object> props = n.asMap();
                if (relType.equals("TOOK_ASSESSMENT") && props.containsKey("resultId")) {
                    return "assessment_" + n.get("resultId").asLong();
                } else if (relType.equals("EXPRESSED_EMOTION") && props.containsKey("emotion")) {
                    return "emotion_" + System.identityHashCode(n);
                } else if (relType.equals("INTERACTED_IN") && props.containsKey("sessionId")) {
                    return "interaction_" + n.get("sessionId").asLong();
                } else if (relType.equals("HAS_PROFILE")) {
                    return "profile_" + n.id();
                } else if (relType.equals("HAS_RISK")) {
                    return "risk_" + n.id();
                }
            }
        }
        return null;
    }

    private String getNodeIdFromNode(Node n) {
        Map<String, Object> props = n.asMap();
        if (props.containsKey("resultId")) return "assessment_" + n.get("resultId").asLong();
        if (props.containsKey("sessionId")) return "interaction_" + n.get("sessionId").asLong();
        if (props.containsKey("emotion")) return "emotion_" + System.identityHashCode(n);
        if (props.containsKey("personality")) return "profile_" + n.id();
        if (props.containsKey("level")) return "risk_" + n.id();
        return null;
    }

    private String getNodeType(String relType) {
        switch (relType) {
            case "TOOK_ASSESSMENT": return "assessment";
            case "EXPRESSED_EMOTION": return "emotion";
            case "INTERACTED_IN": return "interaction";
            case "HAS_PROFILE": return "profile";
            case "HAS_RISK": return "risk";
            case "ASSIGNED_TO": return "counselor";
            case "RECEIVED": return "notification";
            default: return "unknown";
        }
    }

    private String getNodeLabel(Node n) {
        Map<String, Object> props = n.asMap();
        if (props.containsKey("questionnaireTitle")) return truncate(n.get("questionnaireTitle").asString(), 20);
        if (props.containsKey("name")) return n.get("name").asString();
        if (props.containsKey("emotion")) return n.get("emotion").asString();
        if (props.containsKey("sessionId")) return "会话#" + n.get("sessionId").asLong();
        if (props.containsKey("level")) return n.get("level").asString() + "风险";
        if (props.containsKey("type")) return n.get("type").asString();
        return "节点";
    }

    private String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() > len ? s.substring(0, len) + "..." : s;
    }

    private GraphDataVO.GraphNode createNode(String id, String label, String type, Map<String, Object> data) {
        GraphDataVO.GraphNode node = new GraphDataVO.GraphNode();
        node.setId(id);
        node.setLabel(label);
        node.setType(type);
        node.setData(data);

        // 设置默认颜色
        switch (type) {
            case "student":
                node.setColor(GraphDataVO.NodeColor.of("#4A90E2", "#2E5A8B", "#6BA3E8"));
                break;
            case "assessment":
                node.setColor(GraphDataVO.NodeColor.of("#50C878", "#3A8F5A", "#6FD496"));
                break;
            case "emotion":
                node.setColor(GraphDataVO.NodeColor.of("#FFB347", "#D4922F", "#FFC56E"));
                break;
            case "interaction":
                node.setColor(GraphDataVO.NodeColor.of("#9B59B6", "#76448A", "#AF7AC5"));
                break;
            case "profile":
                node.setColor(GraphDataVO.NodeColor.of("#E74C3C", "#B03A2E", "#EC7063"));
                break;
            case "risk":
                node.setColor(GraphDataVO.NodeColor.of("#E74C3C", "#C0392B", "#F1948A"));
                break;
            case "counselor":
                node.setColor(GraphDataVO.NodeColor.of("#1ABC9C", "#148F77", "#48C9B0"));
                break;
            case "notification":
                node.setColor(GraphDataVO.NodeColor.of("#95A5A6", "#7F8C8D", "#AAB7B8"));
                break;
            default:
                node.setColor(GraphDataVO.NodeColor.of("#BDC3C7", "#95A5A6", "#D5DBDB"));
        }
        return node;
    }

    private GraphDataVO.GraphEdge createEdge(String id, String from, String to, String type, Map<String, Object> data) {
        GraphDataVO.GraphEdge edge = new GraphDataVO.GraphEdge();
        edge.setId(id);
        edge.setFrom(from);
        edge.setTo(to);
        edge.setType(type);
        edge.setLabel(getRelationLabel(type));
        edge.setArrows(GraphDataVO.EdgeArrows.directed());
        edge.setData(data);
        return edge;
    }

    private String getRelationLabel(String type) {
        switch (type) {
            case "TOOK_ASSESSMENT": return "参与评估";
            case "EXPRESSED_EMOTION": return "表达情绪";
            case "INTERACTED_IN": return "对话交互";
            case "HAS_PROFILE": return "性格画像";
            case "HAS_RISK": return "风险标签";
            case "ASSIGNED_TO": return "分配给";
            case "RECEIVED": return "接收通知";
            default: return type;
        }
    }
}
