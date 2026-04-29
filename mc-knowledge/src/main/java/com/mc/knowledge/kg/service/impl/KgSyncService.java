package com.mc.knowledge.kg.service.impl;

import com.mc.knowledge.domain.entity.KnowledgeEvaluationResult;
import com.mc.knowledge.domain.entity.StudentInfo;
import com.mc.knowledge.kg.graph.Neo4jClient;
import com.mc.knowledge.mapper.EvaluationResultMapper;
import com.mc.knowledge.mapper.StudentInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Neo4j 知识图谱同步服务
 * 负责从 MySQL 定时同步数据到 Neo4j 图数据库
 */
@Service
@Slf4j
public class KgSyncService {

    @Resource
    private Neo4jClient neo4jClient;

    @Resource
    private StudentInfoMapper knowledgeStudentInfoMapper;

    @Resource
    private EvaluationResultMapper knowledgeEvaluationResultMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 每日凌晨2点同步所有学生数据到 Neo4j
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncAllStudentsDaily() {
        log.info("[KgSync] 开始每日同步学生数据到 Neo4j...");
        try {
            syncAllStudents();
            log.info("[KgSync] 每日同步完成");
        } catch (Exception e) {
            log.error("[KgSync] 每日同步失败", e);
        }
    }

    /**
     * 每小时同步评估结果
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncKnowledgeEvaluationResultsHourly() {
        log.info("[KgSync] 开始每小时同步评估结果到 Neo4j...");
        try {
            syncAllEvaluations();
            log.info("[KgSync] 每小时同步完成");
        } catch (Exception e) {
            log.error("[KgSync] 每小时同步失败", e);
        }
    }

    /**
     * 同步所有学生信息到 Neo4j
     */
    public void syncAllStudents() {
        List<StudentInfo> students = knowledgeStudentInfoMapper.selectAllStudents();
        int successCount = 0;
        int failCount = 0;

        for (StudentInfo student : students) {
            try {
                if (student.getUserId() == null) {
                    continue;
                }
                neo4jClient.upsertStudent(
                    student.getUserId(),
                    student.getStudentId(),
                    student.getStudentNo(),
                    student.getName(),
                    student.getGrade(),
                    student.getMajor(),
                    student.getClassName()
                );
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("[KgSync] 同步学生失败 - userId: {}, name: {}", student.getUserId(), student.getName(), e);
            }
        }

        log.info("[KgSync] 学生同步完成 - 成功: {}, 失败: {}", successCount, failCount);
    }

    /**
     * 同步所有评估结果到 Neo4j
     */
    public void syncAllEvaluations() {
        List<KnowledgeEvaluationResult> results = knowledgeEvaluationResultMapper.selectAllResults();
        int successCount = 0;
        int failCount = 0;

        for (KnowledgeEvaluationResult result : results) {
            try {
                if (result.getStudentId() == null) {
                    continue;
                }
                // 获取学生对应的 userId
                Long userId = getUserIdByStudentId(result.getStudentId());
                if (userId == null) {
                    continue;
                }
                neo4jClient.addAssessment(
                    userId,
                    result.getResultId(),
                    result.getQuestionnaireTitle(),
                    result.getTotalScore(),
                    result.getRiskLevel()
                );
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.error("[KgSync] 同步评估结果失败 - resultId: {}", result.getResultId(), e);
            }
        }

        log.info("[KgSync] 评估结果同步完成 - 成功: {}, 失败: {}", successCount, failCount);
    }

    /**
     * 根据 studentId 获取 userId
     */
    private Long getUserIdByStudentId(Long studentId) {
        StudentInfo student = knowledgeStudentInfoMapper.selectById(studentId);
        return student != null ? student.getUserId() : null;
    }

    /**
     * 手动触发全量同步（管理员接口调用）
     */
    public Map<String, Object> manualFullSync() {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // 先同步学生
            List<StudentInfo> students = knowledgeStudentInfoMapper.selectAllStudents();
            int studentSuccess = 0;
            int studentFail = 0;
            for (StudentInfo student : students) {
                try {
                    if (student.getUserId() != null) {
                        neo4jClient.upsertStudent(
                            student.getUserId(),
                            student.getStudentId(),
                            student.getStudentNo(),
                            student.getName(),
                            student.getGrade(),
                            student.getMajor(),
                            student.getClassName()
                        );
                        studentSuccess++;
                    }
                } catch (Exception e) {
                    studentFail++;
                }
            }

            // 再同步评估结果
            List<KnowledgeEvaluationResult> results = knowledgeEvaluationResultMapper.selectAllResults();
            int evalSuccess = 0;
            int evalFail = 0;
            for (KnowledgeEvaluationResult evalResult : results) {
                try {
                    if (evalResult.getStudentId() != null) {
                        Long userId = getUserIdByStudentId(evalResult.getStudentId());
                        if (userId != null) {
                            neo4jClient.addAssessment(
                                userId,
                                evalResult.getResultId(),
                                evalResult.getQuestionnaireTitle(),
                                evalResult.getTotalScore(),
                                evalResult.getRiskLevel()
                            );
                            evalSuccess++;
                        }
                    }
                } catch (Exception e) {
                    evalFail++;
                }
            }

            // 同步对话会话
            Map<String, Object> chatSync = syncAllChatSessions();

            // 同步情绪状态
            Map<String, Object> emotionSync = syncAllEmotionStates();

            long cost = System.currentTimeMillis() - startTime;
            result.put("success", true);
            result.put("studentSync", Map.of("success", studentSuccess, "fail", studentFail));
            result.put("evaluationSync", Map.of("success", evalSuccess, "fail", evalFail));
            result.put("chatSync", chatSync);
            result.put("emotionSync", emotionSync);
            result.put("costMs", cost);
            log.info("[KgSync] 手动全量同步完成 - {}", result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("[KgSync] 手动全量同步失败", e);
        }

        return result;
    }

    /**
     * 同步所有对话会话统计到 Neo4j
     */
    public Map<String, Object> syncAllChatSessions() {
        Map<String, Object> syncResult = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        // 查询每个学生的对话会话统计
        String sql = """
            SELECT s.user_id as userId,
                   COUNT(DISTINCT c.session_id) as sessionCount,
                   COUNT(m.message_id) as messageCount,
                   MAX(m.create_time) as lastDate,
                   AVG(CHAR_LENGTH(m.content)) as avgMsgLen
            FROM ai_chat_session c
            JOIN ai_chat_message m ON c.session_id = m.session_id AND m.message_type = 1
            JOIN sys_user s ON c.user_id = s.user_id
            WHERE s.user_id IS NOT NULL
            GROUP BY s.user_id
            """;
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : rows) {
                try {
                    Long userId = row.get("userId") instanceof Number ? ((Number) row.get("userId")).longValue() : null;
                    if (userId == null) continue;

                    Integer sessionCount = row.get("sessionCount") instanceof Number ? ((Number) row.get("sessionCount")).intValue() : 0;
                    Integer messageCount = row.get("messageCount") instanceof Number ? ((Number) row.get("messageCount")).intValue() : 0;
                    String lastDate = row.get("lastDate") != null ? row.get("lastDate").toString() : null;

                    // 汇总到最近一个会话
                    Long sessionId = 1L;
                    neo4jClient.upsertInteraction(userId, sessionId, messageCount, null);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.warn("[KgSync] 同步对话会话失败: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[KgSync] 查询对话会话失败", e);
        }

        syncResult.put("success", successCount);
        syncResult.put("fail", failCount);
        log.info("[KgSync] 对话会话同步完成 - 成功: {}, 失败: {}", successCount, failCount);
        return syncResult;
    }

    /**
     * 同步所有学生的情绪状态到 Neo4j（从对话消息中提取）
     */
    public Map<String, Object> syncAllEmotionStates() {
        Map<String, Object> syncResult = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        // 获取所有有对话的学生及其最近的对话内容
        String sql = """
            SELECT c.user_id as userId,
                   m.content,
                   m.kg_context
            FROM ai_chat_message m
            JOIN ai_chat_session c ON m.session_id = c.session_id
            WHERE c.user_id IS NOT NULL
            ORDER BY c.user_id, m.create_time DESC
            """;
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            // 按 userId 去重，只取最新的
            Map<Long, Map<String, String>> latestData = new LinkedHashMap<>();
            for (Map<String, Object> row : rows) {
                Long userId = row.get("userId") instanceof Number ? ((Number) row.get("userId")).longValue() : null;
                if (userId == null || latestData.containsKey(userId)) continue;
                latestData.put(userId, Map.of(
                    "content", row.get("content") != null ? (String) row.get("content") : "",
                    "kgContext", row.get("kg_context") != null ? (String) row.get("kg_context") : ""
                ));
            }

            for (Map.Entry<Long, Map<String, String>> entry : latestData.entrySet()) {
                try {
                    Long userId = entry.getKey();
                    Map<String, String> data = entry.getValue();
                    String kgContext = data.get("kgContext");
                    String content = data.get("content");
                    // 优先从 kg_context 提取，其次从 AI 回复内容提取
                    String emotion = extractEmotionFromContext(kgContext);
                    if (emotion == null) {
                        emotion = extractEmotionFromAIResponse(content);
                    }
                    if (emotion != null) {
                        neo4jClient.addEmotionState(userId, emotion, 5);
                        successCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
        } catch (Exception e) {
            log.error("[KgSync] 查询情绪状态失败", e);
        }

        syncResult.put("success", successCount);
        syncResult.put("fail", failCount);
        log.info("[KgSync] 情绪状态同步完成 - 成功: {}, 失败: {}", successCount, failCount);
        return syncResult;
    }

    private String extractEmotionFromContext(String context) {
        if (context == null || context.isEmpty()) return null;
        String lower = context.toLowerCase();
        if (lower.contains("积极") || lower.contains("开心") || lower.contains("高兴") || lower.contains("乐观") || lower.contains("positive")) return "积极";
        if (lower.contains("焦虑") || lower.contains("紧张") || lower.contains("不安") || lower.contains("anxious")) return "焦虑";
        if (lower.contains("抑郁") || lower.contains("低落") || lower.contains("悲伤") || lower.contains("难过") || lower.contains("低落")) return "抑郁";
        if (lower.contains("愤怒") || lower.contains("生气") || lower.contains("烦躁") || lower.contains("angry")) return "愤怒";
        if (lower.contains("平静") || lower.contains("稳定") || lower.contains("放松") || lower.contains("calm")) return "平静";
        if (lower.contains("一般") || lower.contains("neutral")) return "一般";
        return null;
    }

    private String extractEmotionFromAIResponse(String content) {
        // 从 AI 的回复内容中提取情绪相关关键词
        if (content == null || content.isEmpty()) return null;
        String lower = content.toLowerCase();
        if (lower.contains("理解你的感受") || lower.contains("感受到你的") || lower.contains("我理解")) return "共情";
        if (lower.contains("焦虑") || lower.contains("担心") || lower.contains("紧张")) return "焦虑";
        if (lower.contains("抑郁") || lower.contains("低落") || lower.contains("悲伤")) return "抑郁";
        if (lower.contains("积极") || lower.contains("开心") || lower.contains("乐观")) return "积极";
        if (lower.contains("平静") || lower.contains("放松") || lower.contains("舒缓")) return "平静";
        if (lower.contains("愤怒") || lower.contains("生气") || lower.contains("烦躁")) return "愤怒";
        return null;
    }

    /**
     * 同步指定学生的数据
     */
    public void syncStudent(Long studentId) {
        StudentInfo student = knowledgeStudentInfoMapper.selectById(studentId);
        if (student != null && student.getUserId() != null) {
            // 同步学生基本信息
            neo4jClient.upsertStudent(
                student.getUserId(),
                student.getStudentId(),
                student.getStudentNo(),
                student.getName(),
                student.getGrade(),
                student.getMajor(),
                student.getClassName()
            );

            // 同步该学生的评估结果
            List<KnowledgeEvaluationResult> results = knowledgeEvaluationResultMapper.selectRecentByStudentId(studentId, 10);
            for (KnowledgeEvaluationResult evalResult : results) {
                neo4jClient.addAssessment(
                    student.getUserId(),
                    evalResult.getResultId(),
                    evalResult.getQuestionnaireTitle(),
                    evalResult.getTotalScore(),
                    evalResult.getRiskLevel()
                );
            }

            // 同步该学生的对话会话
            syncChatSessionsByUserId(student.getUserId());

            // 同步该学生的情绪状态
            syncEmotionStateByUserId(student.getUserId());

            log.info("[KgSync] 同步学生数据完成 - studentId: {}, userId: {}", studentId, student.getUserId());
        }
    }

    private void syncChatSessionsByUserId(Long userId) {
        String sql = """
            SELECT COUNT(DISTINCT c.session_id) as sessionCount,
                   COUNT(m.message_id) as messageCount,
                   MAX(m.create_time) as lastDate
            FROM ai_chat_session c
            JOIN ai_chat_message m ON c.session_id = m.session_id AND m.message_type = 1
            WHERE c.user_id = ?
            """;
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                Integer messageCount = row.get("messageCount") instanceof Number ? ((Number) row.get("messageCount")).intValue() : 0;
                if (messageCount > 0) {
                    neo4jClient.upsertInteraction(userId, 1L, messageCount, null);
                    log.info("[KgSync] 同步对话会话 - userId: {}, messageCount: {}", userId, messageCount);
                }
            }
        } catch (Exception e) {
            log.warn("[KgSync] 同步对话会话失败 - userId: {}", userId, e);
        }
    }

    private void syncEmotionStateByUserId(Long userId) {
        String sql = """
            SELECT m.content, m.kg_context
            FROM ai_chat_message m
            JOIN ai_chat_session c ON m.session_id = c.session_id
            WHERE c.user_id = ?
            ORDER BY m.create_time DESC
            LIMIT 1
            """;
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                String kgContext = row.get("kg_context") != null ? (String) row.get("kg_context") : "";
                String content = row.get("content") != null ? (String) row.get("content") : "";
                String emotion = extractEmotionFromContext(kgContext);
                if (emotion == null) {
                    emotion = extractEmotionFromAIResponse(content);
                }
                if (emotion != null) {
                    neo4jClient.addEmotionState(userId, emotion, 5);
                    log.info("[KgSync] 同步情绪状态 - userId: {}, emotion: {}", userId, emotion);
                }
            }
        } catch (Exception e) {
            log.warn("[KgSync] 同步情绪状态失败 - userId: {}", userId, e);
        }
    }
}
