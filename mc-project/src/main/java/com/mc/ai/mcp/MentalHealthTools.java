package com.mc.ai.mcp;

import com.mc.community.domain.CommunityPost;
import com.mc.community.service.ICommunityPostService;
import com.mc.evaluation.domain.EvaluationResult;
import com.mc.evaluation.service.IEvaluationResultService;
import com.mc.intervention.domain.InterventionNotification;
import com.mc.intervention.service.IInterventionNotificationService;
import com.mc.intervention.service.IInterventionRiskConfigService;
import com.mc.recommend.domain.RecommendArticle;
import com.mc.recommend.service.IRecommendArticleService;
import com.mc.student.domain.Student;
import com.mc.student.service.IStudentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class MentalHealthTools {

    @Autowired
    private IStudentInfoService studentInfoService;
    @Autowired
    private IEvaluationResultService evaluationResultService;
    @Autowired
    private IInterventionRiskConfigService riskConfigService;
    @Autowired
    private IRecommendArticleService recommendArticleService;
    @Autowired
    private ICommunityPostService communityPostService;
    @Autowired
    private IInterventionNotificationService notificationService;

    private Long getCurrentStudentId(org.springframework.ai.chat.model.ToolContext toolContext) {
        Long userId = (Long) toolContext.getContext().get("userId");
        if (userId == null) {
            throw new IllegalStateException("ToolContext中未获取到用户ID，无法查询个人数据");
        }
        Student student = studentInfoService.selectStudentInfoByUserId(userId);
        if (student == null) {
            throw new IllegalStateException("当前用户未绑定学生信息，无法查询个人数据");
        }
        return student.getStudentId();
    }

    @Tool(name = "get_current_date",
            description = "获取当前的日期和时间。此工具用于解决AI模型知识截止日期不确定的问题，确保在进行需要时效性的操作（如搜索最新资讯）时能使用准确的当前时间。")
    public Map<String, Object> getCurrentDate() {
        Map<String, Object> result = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        result.put("year", calendar.get(Calendar.YEAR));
        result.put("month", calendar.get(Calendar.MONTH) + 1);
        result.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        result.put("hour", calendar.get(Calendar.HOUR_OF_DAY));
        result.put("minute", calendar.get(Calendar.MINUTE));
        result.put("dateString", String.format("%d年%02d月%02d日",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)));
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    private Student getStudentOrNull(Long studentId) {
        return studentInfoService.selectStudentInfoByStudentId(studentId);
    }

    // ==================== 学生个人档案（所有方法添加ToolContext参数） ====================
    @Tool(name = "get_student_mental_health_status",
            description = "查询当前学生的心理健康状态，包括基本信息、最近评估分数、风险等级和未处理干预通知数。仅供查询，不做写操作。")
    public Map<String, Object> getStudentMentalHealthStatus(ToolContext toolContext) {
        Long studentId = getCurrentStudentId(toolContext);
        log.info("[MCP-Tools] 查询学生心理健康状态 - studentId: {}", studentId);
        Map<String, Object> result = new LinkedHashMap<>();

        Student student = getStudentOrNull(studentId);
        if (student == null) {
            result.put("error", "未找到该学生信息");
            return result;
        }

        result.put("studentId", student.getStudentId());
        result.put("studentName", student.getName());
        result.put("grade", student.getGrade());
        result.put("major", student.getMajor());

        EvaluationResult evalQuery = new EvaluationResult();
        evalQuery.setStudentId(studentId);
        List<EvaluationResult> evalList = evaluationResultService.selectEvaluationResultList(evalQuery);

        if (evalList != null && !evalList.isEmpty()) {
            EvaluationResult latestEval = evalList.stream()
                    .filter(e -> "1".equals(e.getCompletionStatus()))
                    .max(Comparator.comparing(EvaluationResult::getCreateTime))
                    .orElse(evalList.get(0));

            result.put("latestAssessmentDate", latestEval.getCreateTime());
            result.put("latestAssessmentScore", latestEval.getTotalScore());
            result.put("latestRiskLevel", latestEval.getRiskLevel());
            result.put("latestQuestionnaireTitle", latestEval.getQuestionnaireTitle());
            result.put("aiAnalysisCompleted", "1".equals(latestEval.getAiStatus()));

            Integer score = latestEval.getTotalScore();
            if (score != null) {
                if (score >= 80) {
                    result.put("mentalHealthLevel", "良好");
                    result.put("suggestion", "继续保持积极心态，建议定期参与心理健康活动。");
                } else if (score >= 60) {
                    result.put("mentalHealthLevel", "轻度风险");
                    result.put("suggestion", "建议关注情绪变化，可预约辅导员进行心理咨询。");
                } else {
                    result.put("mentalHealthLevel", "中高度风险");
                    result.put("suggestion", "建议尽快联系辅导员进行专业心理评估和干预。");
                }
            }
        } else {
            result.put("latestAssessmentDate", null);
            result.put("latestAssessmentScore", null);
            result.put("latestRiskLevel", null);
            result.put("mentalHealthLevel", "未评估");
            result.put("suggestion", "建议完成一次心理健康评估，了解自身状态。");
        }

        List<InterventionNotification> notifications = notificationService.selectNotificationByStudentId(studentId);
        long unprocessedCount = 0;
        if (notifications != null) {
            unprocessedCount = notifications.stream()
                    .filter(n -> !"1".equals(n.getProcessStatus()))
                    .count();
        }
        result.put("unprocessedInterventionCount", unprocessedCount);

        return result;
    }

    @Tool(name = "get_psychological_assessment_reports",
            description = "获取当前学生的心理评估历史报告列表，包括每次评估的分数、风险等级和完成状态。")
    public List<Map<String, Object>> getPsychologicalAssessmentReports(
            @ToolParam(description = "返回记录数量限制，默认10", required = false) Integer limit,
            ToolContext toolContext) {
        Long studentId = getCurrentStudentId(toolContext);
        log.info("[MCP-Tools] 获取心理评估报告 - studentId: {}, limit: {}", studentId, limit);
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        EvaluationResult query = new EvaluationResult();
        query.setStudentId(studentId);
        List<EvaluationResult> list = evaluationResultService.selectEvaluationResultList(query);

        List<Map<String, Object>> reports = new ArrayList<>();
        int count = 0;
        for (EvaluationResult eval : list) {
            if (count >= limit) break;
            if (!"1".equals(eval.getCompletionStatus())) continue;

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("resultId", eval.getResultId());
            report.put("questionnaireTitle", eval.getQuestionnaireTitle());
            report.put("assessmentDate", eval.getCreateTime());
            report.put("totalScore", eval.getTotalScore());
            report.put("riskLevel", eval.getRiskLevel());
            report.put("aiAnalysisCompleted", "1".equals(eval.getAiStatus()));
            report.put("readStatus", "1".equals(eval.getReadStatus()) ? "已读" : "未读");
            reports.add(report);
            count++;
        }
        return reports;
    }

    @Tool(name = "get_student_info",
            description = "查询当前学生本人的基本信息，包括姓名、学号、年级、专业、班级等。")
    public Map<String, Object> getStudentInfo(ToolContext toolContext) {
        Long studentId = getCurrentStudentId(toolContext);
        log.info("[MCP-Tools] 查询学生信息 - studentId: {}", studentId);
        Student student = getStudentOrNull(studentId);
        if (student == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "未找到该学生信息");
            return error;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studentId", student.getStudentId());
        result.put("studentNo", student.getStudentNo());
        result.put("name", student.getName());
        result.put("gender", "0".equals(student.getGender()) ? "男" : "女");
        result.put("grade", student.getGrade());
        result.put("major", student.getMajor());
        result.put("className", student.getClassName());
        result.put("phone", student.getPhone());
        result.put("status", "0".equals(student.getStatus()) ? "正常" : "异常");
        return result;
    }

    // ==================== 内容推荐 ====================
    @Tool(name = "get_recommended_articles",
            description = "获取平台推荐的心理健康知识文章列表，供学生学习心理健康知识。包含文章标题、摘要和阅读量。")
    public List<Map<String, Object>> getRecommendedArticles(
            @ToolParam(description = "返回数量限制，默认5", required = false) Integer limit) {
        log.info("[MCP-Tools] 获取推荐文章 - limit: {}", limit);
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        RecommendArticle query = new RecommendArticle();
        List<RecommendArticle> list = recommendArticleService.selectRecommendArticleList(query);

        List<Map<String, Object>> articles = new ArrayList<>();
        int count = 0;
        for (RecommendArticle article : list) {
            if (count >= limit) break;
            if (!"0".equals(article.getStatus())) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("articleId", article.getArticleId());
            item.put("title", article.getTitle());
            item.put("summary", article.getSummary());
            item.put("readCount", article.getReadCount());
            item.put("createTime", article.getCreateTime());
            articles.add(item);
            count++;
        }
        return articles;
    }

    // ==================== 风险等级 ====================
    @Tool(name = "calculate_risk_level",
            description = "根据评估总分（0-100）计算心理风险等级，返回风险等级和干预建议。")
    public Map<String, Object> calculateRiskLevel(
            @ToolParam(description = "评估总分，范围 0-100", required = true) Integer score) {
        log.info("[MCP-Tools] 计算风险等级 - score: {}", score);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("inputScore", score);

        String riskLevel = riskConfigService.judgeRiskLevel(score);
        result.put("riskLevel", riskLevel);

        String riskDesc;
        String suggestion;
        if ("高".equals(riskLevel) || "高风险".equals(riskLevel)) {
            riskDesc = "高风险";
            suggestion = "评估分数 " + score + " 分，处于高风险状态，建议立即联系辅导员进行专业干预。";
        } else if ("中".equals(riskLevel) || "中风险".equals(riskLevel)) {
            riskDesc = "中风险";
            suggestion = "评估分数 " + score + " 分，处于中风险状态，建议密切关注情绪变化，必要时预约咨询。";
        } else {
            riskDesc = "低风险";
            suggestion = "评估分数 " + score + " 分，当前状态良好，保持积极心态即可。";
        }

        result.put("riskDescription", riskDesc);
        result.put("suggestion", suggestion);
        return result;
    }

    // ==================== 社区帖子 ====================
    @Tool(name = "get_community_posts",
            description = "获取心理健康社区帖子列表，包括帖子标题、内容摘要、浏览数、点赞数和评论数。")
    public List<Map<String, Object>> getCommunityPosts(
            @ToolParam(description = "返回数量限制，默认10", required = false) Integer limit) {
        log.info("[MCP-Tools] 获取社区帖子 - limit: {}", limit);
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        CommunityPost query = new CommunityPost();
        List<CommunityPost> posts = communityPostService.selectCommunityPostList(query);

        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        for (CommunityPost post : posts) {
            if (count >= limit) break;
            if (!"0".equals(post.getStatus())) continue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("postId", post.getPostId());
            item.put("title", post.getTitle());
            item.put("content", post.getContent());
            item.put("authorName", post.getUserName());
            item.put("viewCount", post.getViewCount());
            item.put("commentCount", post.getCommentCount());
            item.put("likeCount", post.getLikeCount());
            item.put("createTime", post.getCreateTime());
            result.add(item);
            count++;
        }
        return result;
    }
}