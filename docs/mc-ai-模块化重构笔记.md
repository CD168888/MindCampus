# Spring AI 模块化重构：从单体到分层架构实践

> 作者：caidu  
> 日期：2026-04-01  
> 项目：MindCampus 高校学生心理健康智能干预平台  
> 技术栈：Spring Boot 3.5.4 + Spring AI Alibaba + Maven 多模块

---

## 前言

MindCampus 是一个面向高校学生的心理健康智能干预平台，核心 AI 能力基于阿里巴巴 **通义千问（DashScope）** API 实现。在平台迭代过程中，AI 相关的代码全部堆积在 `mc-project` 模块中，随着功能增加，逐渐暴露出以下问题：

- **职责混乱**：`mc-project` 混杂了 Controller 业务逻辑、MyBatis Mapper、AI 配置、Service 层等各层代码
- **耦合严重**：AI 层的 `ChatClient` 配置依赖业务服务，形成了隐式循环依赖风险
- **难以复用**：如果其他项目想复用 AI 能力，必须引入整个 `mc-project`，包含了大量不必要的数据库相关依赖
- **编译耦合**：业务工具（如学生信息查询工具）以 `@Component` 存在，AI 层直接注入，导致编译期依赖方向错误

本次重构的目标，就是将 **AI 底层能力**（ChatClient 配置、对话记忆、提示词模板、提示词工程）与 **业务层**（Controller、Service、Mapper、Entity）彻底分离，形成清晰的单向依赖架构。

---

## 一、重构前的架构问题

### 1.1 模块依赖关系混乱

重构前，`mc-project` 模块的 `com.mc.ai` 包中包含了几乎所有 AI 相关代码：

```
mc-project/src/main/java/com/mc/ai/
├── config/          # AI 配置类（ChatClient、Redis 记忆、RestTemplate）
├── controller/       # AI 对话 Controller
├── domain/          # 领域实体（AiChatSession、AiChatMessage 等）
├── mapper/          # MyBatis Mapper
├── prompt/          # 提示词模板
├── service/        # AI 业务 Service
└── tool/           # 业务工具类（StudentInfoTool 等）
```

问题在于：

- `AiConfig`（AI 配置层）依赖 `StudentInfoTool`（业务工具 `@Component`），而 `StudentInfoTool` 依赖 `IStudentInfoService`（属于 `mc-project` 的业务层）
- 这形成了 **隐式循环依赖**：AI 配置层 → 业务工具 → 业务服务 → 业务层 → ???

虽然编译期可能侥幸通过，但架构上已经完全违反分层原则，工具注册机制只能硬编码或通过反射 hack。

### 1.2 流式对话的内存可见性问题

之前的 SSE 流式对话实现中，AI 完整回复内容保存在 `AiChatServiceImpl` 的局部变量中，通过 Reactor 的 `StringBuilder` 闭包传递到 `doOnComplete()` 回调。虽然功能正确，但这样的设计将业务逻辑（消息持久化）绑定到了 AI 服务层，违反了"AI 服务只负责 AI 交互"的单一职责原则。

---

## 二、重构方案：新增 `mc-ai` 独立模块

### 2.1 模块划分原则

重构遵循以下原则：

| 原则 | 说明 |
|------|------|
| **单一职责** | 每个模块只负责一件事 |
| **接口隔离** | 模块之间通过接口通信，不暴露内部实现 |
| **单向依赖** | `mc-project` → `mc-ai`，绝不能反向 |
| **编译期安全** | 依赖关系在编译阶段就能验证，不靠运行时反射 hack |

### 2.2 重构后的模块结构

```
mc-admin/
├── mc-framework/      框架层（Redis、MyBatis、Security）
├── mc-system/          系统模块（用户、角色、菜单）
├── mc-project/         核心业务模块 ← 依赖 mc-ai
├── mc-ai/              AI 能力模块 ← 纯 AI 能力，不含任何写库逻辑
├── mc-common/          通用工具层
├── mc-quartz/          定时任务
└── mc-generator/       代码生成器
```

**`mc-ai` 模块职责**：专注于 AI 底层能力，**完全不含任何数据库写操作**。

```
mc-ai/
├── pom.xml
└── src/main/java/com/mc/ai/
    ├── config/
    │   ├── AiConfig.java                      # 核心 ChatClient + ChatMemory + 工具注册
    │   ├── MentalHealthChatClientConfig.java  # 心理健康评估专用 Client
    │   ├── QuestionGenerationChatClientConfig.java  # 题目生成专用 Client
    │   └── RestTemplateConfig.java            # 全局 HTTP 超时配置
    ├── prompt/
    │   └── AiPrompts.java                     # 提示词模板（系统提示词定义）
    └── service/
        ├── IAiChatService.java                 # AI 服务接口（核心抽象）
        └── impl/
            └── AiChatServiceImpl.java          # AI 服务实现
```

**`mc-project` 模块职责**：HTTP 入口、数据库写操作、会话/消息 CRUD、权限校验。

```
mc-project/src/main/java/com/mc/ai/
├── config/
│   └── ToolConfig.java                        # 业务工具 Bean 注册
├── controller/
│   └── AiChatController.java                  # AI 对话 HTTP 入口
├── domain/
│   ├── AiChatMessage.java                     # 聊天消息实体
│   └── AiChatSession.java                     # 会话实体
├── mapper/
│   ├── AiChatMessageMapper.java               # 消息 Mapper
│   └── AiChatSessionMapper.java               # 会话 Mapper
├── service/
│   ├── IAiChatMessageService.java
│   ├── IAiChatSessionService.java
│   └── impl/
│       ├── AiChatMessageServiceImpl.java
│       └── AiChatSessionServiceImpl.java
└── tool/
    ├── StudentInfoTool.java                   # 学生信息工具
    └── StudentPsychologicalTool.java          # 心理评估工具
```

### 2.3 依赖方向图

```
┌─────────────────────────────────────────────────────────┐
│                      mc-admin                           │
│                  （应用启动入口）                         │
└────────────────────────┬───────────────────────────────┘
                         │
           ┌─────────────┴─────────────┐
           ▼                           ▼
┌─────────────────────┐     ┌─────────────────────────┐
│    mc-project        │     │       mc-ai             │
│  （业务层 + 写库）     │────▶│   （AI 能力层）          │
│                      │     │                         │
│  · Controller        │     │  · ChatClient 配置       │
│  · Service（写库）    │     │  · ChatMemory（Redis）   │
│  · Mapper / Entity   │     │  · 提示词模板             │
│  · 业务工具 Bean      │     │  · AI 服务接口           │
│                      │     │  · RestTemplate 配置     │
└─────────────────────┘     └─────────────────────────┘
           │                           ▲
           │                           │
           └────────── mc-framework ─────┘
```

---

## 三、核心设计：如何优雅地解决循环依赖

这是重构中最关键的设计：**`mc-ai` 如何调用 `mc-project` 中的业务工具，同时又不产生编译期循环依赖？**

### 3.1 旧方案的问题

通常的解决方案有：

| 方案 | 问题 |
|------|------|
| 业务工具移到 `mc-ai` | `mc-ai` 就依赖了 `mc-project` 的服务层，形成循环 |
| 通过反射注册工具 | 运行时 hack，不安全，编译期无法验证 |
| 工具抽象成接口放在 `mc-common` | 需要改包结构，影响较大 |

### 3.2 新方案：运行时动态扫描 + 接口隔离

核心思路：**`mc-ai` 在编译期只依赖 Spring AI 的标准接口（`Supplier`、`Function`），不依赖任何业务类。运行期通过 `ApplicationContext` 动态扫描所有 `Supplier/Function` 类型的 Bean 作为工具。**

#### AiConfig 中的工具注册（`mc-ai` 模块）

```java
@Autowired
private ApplicationContext applicationContext;

@Bean
@Primary
public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
    String[] toolNames = getToolNames();  // 运行时扫描

    return builder.clone()
            .defaultOptions(DashScopeChatOptions.builder()
                    .withModel(modelName)
                    .build())
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .defaultToolNames(toolNames)   // 自动注册所有业务工具
            .build();
}

private String[] getToolNames() {
    // 编译期只依赖标准接口 Supplier / Function，不依赖任何业务类
    Map<String, Supplier> supplierBeans = applicationContext.getBeansOfType(Supplier.class);
    Map<String, Function> functionBeans = applicationContext.getBeansOfType(Function.class);

    Set<String> allToolNames = new HashSet<>(supplierBeans.keySet());
    allToolNames.addAll(functionBeans.keySet());
    return allToolNames.toArray(new String[0]);
}
```

#### ToolConfig 中的工具定义（`mc-project` 模块）

```java
@Configuration
public class ToolConfig {

    @Autowired
    private IStudentInfoService studentInfoService;

    @Bean
    @Description("查询所有学生信息，返回学生列表")
    public Supplier<List<Student>> getAllStudents() {
        return () -> studentInfoService.selectStudentInfoList(new Student());
    }

    @Bean
    @Description("根据学生ID查询学生信息")
    public Function<Long, Student> getStudentById() {
        return studentId -> studentInfoService.selectStudentInfoByStudentId(studentId);
    }
}
```

#### 原理分析

```
编译期：
  mc-ai 只看到：java.util.function.Supplier / Function（Java 标准库）
  mc-project 只看到：mc-ai 的 IAiChatService 接口

运行期（Spring 启动时）：
  1. Spring 先初始化 mc-ai 的 AiConfig → chatClient Bean 等待 ChatMemory
  2. Spring 初始化 mc-project 的 ToolConfig → 注册 Supplier/Function Bean
  3. AiConfig.getToolNames() 被调用时，ToolConfig 的 Bean 已经存在于容器中
  4. getToolNames() 通过 applicationContext.getBeansOfType() 扫描到所有工具
  5. ChatClient 拿到完整的工具名称列表

结果：
  ✅ 编译期依赖关系干净（单向）
  ✅ 运行期工具自动发现
  ✅ 无循环依赖、无反射 hack
```

---

## 四、`IAiChatService` 接口设计

`IAiChatService` 是 `mc-ai` 对外暴露的唯一入口。它使用 **Java record** 定义返回值类型，充分利用 Spring AI 的类型推断能力。

### 4.1 流式对话接口

```java
public interface IAiChatService {

    record StreamChatResult(
            /** SSE 流，供 Controller 层直接返回给前端 */
            Flux<ServerSentEvent<String>> sseFlux,
            /** 完整 AI 回复内容累积器，供调用方在流结束后读取 */
            StringBuilder fullContentHolder
    ) {}

    /**
     * 发送消息并获取流式响应
     *
     * @param userMessage   用户消息内容
     * @param files         附件列表（图片等）
     * @param systemPrompt  系统提示词
     * @param conversationId 会话 ID
     * @return SSE 流 + 完整内容累积器
     */
    StreamChatResult streamChat(String userMessage, List<MultipartFile> files,
                                String systemPrompt, String conversationId);
}
```

**设计思路**：返回 `StreamChatResult` 同时携带两样东西：

- `sseFlux`：直接返回给前端的 SSE 流
- `fullContentHolder`：`StringBuilder` 引用，供调用方在 `doOnComplete()` / `doOnCancel()` 回调中读取 AI 完整回复

这样 **`mc-ai` 不知道"消息需要写库"这件事**，写库逻辑完全由 `mc-project` 的 Controller 在回调中处理。

### 4.2 流式取消与记忆管理

```java
// 取消指定会话的流式输出
void cancelStream(String conversationId);

// 清空指定会话的 AI 对话记忆（Redis）
void clearChatMemory(String conversationId);
```

`cancelStream` 的实现利用了 Reactor 的 `takeWhile`：

```java
AtomicBoolean cancelFlag = cancelFlags.computeIfAbsent(conversationId, k -> new AtomicBoolean(false));
cancelFlag.set(false);

Flux<String> contentFlux = clientToUse.prompt()
        .messages(userMsg)
        .system(systemPrompt)
        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
        .stream()
        .content()
        .takeWhile(data -> !cancelFlag.get());  // 取消时条件为 true，流中断
```

---

## 五、`AiChatController` 中的 SSE 流式对话流程

### 5.1 完整流程图

```
前端请求 ──▶ AiChatController.generate()
                    │
                    ├─ 1. SecurityUtils.getUserId()  ─ 验证登录
                    │
                    ├─ 2. validateOrCreateSession()  ─ 验证/创建会话
                    │
                    ├─ 3. chatMessageService.saveMessage()  ─ 保存用户消息【写库】
                    │
                    ├─ 4. aiChatService.streamChat()  ─ 调用 AI【mc-ai 模块】
                    │       │
                    │       └─ 返回 StreamChatResult
                    │               ├─ sseFlux → 直接返回给前端
                    │               └─ fullContentHolder → 在回调中读取
                    │
                    └─ 5. result.sseFlux() 返回
                            │
                            ├─ .doOnComplete()  ─ 流结束时
                            │       ├─ 从 fullContentHolder 读取 AI 回复
                            │       ├─ chatMessageService.saveMessage() 【写库】
                            │       └─ aiChatService.generateSessionTitleAsync() 生成标题
                            │
                            ├─ .doOnCancel()  ─ 用户取消时
                            │       └─ 保存已生成的 AI 回复片段【写库】
                            │
                            └─ .doOnError()  ─ 异常时
                                    └─ 记录错误日志
```

### 5.2 关键代码：流结束回调中读取完整回复

```java
return result.sseFlux()
        .doOnComplete(() -> {
            if (messageSaved.compareAndSet(false, true)) {
                String aiResponse = result.fullContentHolder().toString();
                if (aiResponse.length() > 0) {
                    // 保存 AI 回复到数据库
                    chatMessageService.saveMessage(sessionId, userId, 0, aiResponse, null);
                }
            }
        })
        .doOnCancel(() -> {
            // 同理，用户主动停止时也保存已生成的内容
        })
        .doOnError(error -> {
            log.error("AI对话异常", error);
        });
```

**这里的 `StringBuilder fullContentHolder` 是整个设计的精髓**：它是一个引用穿透到回调中，`streamChat` 方法在流式发送每个 chunk 时会 `append` 到这个 StringBuilder 中。当流结束时（无论正常还是取消），回调中可以拿到完整的 AI 回复内容。

---

## 六、多 ChatClient 架构

`mc-ai` 模块中注册了 4 个不同的 `ChatClient` 实例，每个针对不同场景：

### 6.1 4 个 ChatClient 职责

| Bean 名称 | 用途 | 对话记忆 | 业务工具 | 流式支持 | 超时 |
|-----------|------|---------|---------|---------|------|
| `chatClient`（@Primary） | 通用对话 | ✅ Redis | ✅ 自动注册 | ✅ | 继承 RestTemplate |
| `multiModalChatClient` | 图片附件对话 | ✅ Redis | ✅ 自动注册 | ✅ | 继承 RestTemplate |
| `mentalHealthChatClient` | 心理健康评估 | ❌ | ❌ | ❌ 单次 | 继承 RestTemplate |
| `questionGenerationChatClient` | 题目生成 | ❌ | ❌ | ❌ 单次 | 继承 RestTemplate |

### 6.2 多模态 ChatClient 的关键配置

```java
@Bean
public ChatClient multiModalChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
    return builder.clone()
            .defaultOptions(DashScopeChatOptions.builder()
                    .withModel(multiModalModelName)
                    .withMultiModel(true)  // ⚠️ 灵魂参数：不加这个流式和多模态都会失效
                    .build())
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .defaultToolNames(toolNames)
            .build();
}
```

### 6.3 AI 服务中自动选择合适的 Client

```java
@Override
public StreamChatResult streamChat(String userMessage, List<MultipartFile> files,
                                   String systemPrompt, String conversationId) {
    boolean hasFiles = files != null && !files.isEmpty();
    ChatClient clientToUse = hasFiles ? multiModalChatClient : chatClient;

    // ... 构建 UserMessage（含 Media）
    return new StreamChatResult(sseFlux, fullContentHolder);
}
```

---

## 七、Redis 对话记忆

### 7.1 架构

```
User Message ──▶ ChatClient
                        │
                        ├── MessageChatMemoryAdvisor
                        │           │
                        │           └── MessageWindowChatMemory
                        │                       │
                        │                       └── RedisChatMemoryRepository
                        │                                   │
                        │                                   └── JedisPoolConfig
                        │                                           │
                        └───────────────────────────────────────▶ Redis
```

### 7.2 滑动窗口策略

```java
@Bean
public RedisChatMemoryRepository chatMemoryRepository() {
    return RedisChatMemoryRepository.builder()
            .host(redisHost)
            .port(redisPort)
            .timeout(redisTimeout)
            .build();
}

@Bean
public ChatMemory chatMemory(RedisChatMemoryRepository chatMemoryRepository) {
    return MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .build();
}
```

`MessageWindowChatMemory` 使用滑动窗口策略，只保留最近 N 条消息（默认 20 条），避免对话历史无限增长。

---

## 八、提示词模板体系

### 8.1 提示词分类

| 提示词 | 用途 | 特点 |
|--------|------|------|
| `STUDENT_WELL_BEING_PROMPT` | 心理陪伴对话 | 温暖共情风格，无评判 |
| `MENTAL_HEALTH_EVALUATION` | 问卷评估 | 输出标准 JSON 结构 |
| `QUESTION_GENERATION` | 题目生成 | 输出 JSON，含选择题/简答题 |
| `GENERAL_ASSISTANT` | 通用助手 | 简单兜底 |

### 8.2 `MENTAL_HEALTH_EVALUATION` 的 JSON 输出规范

```java
public static final String MENTAL_HEALTH_EVALUATION = """
    ...
    // 输出严格 JSON 格式
    {
        "total_score": 75,
        "risk_level": "中",
        "indicators": {
            "anxiety_score": 70,
            "depression_score": 80,
            ...
        },
        "main_issues": ["学业压力明显", ...],
        "suggestions": ["建议寻求心理咨询", ...],
        "detailed_analysis": "详细分析报告"
    }
    """;
```

Spring AI 的 `chatClient.call().entity(MentalHealthEvaluationResult.class)` 可以直接将 JSON 反序列化为 Java record：

```java
public record MentalHealthEvaluationResult(
        int totalScore,
        String riskLevel,
        MentalHealthIndicator indicators,
        List<String> mainIssues,
        List<String> suggestions,
        String detailedAnalysis
) {}
```

---

## 九、`pom.xml` 关键依赖说明

### 9.1 根 `pom.xml` 中 `mc-ai` 的引入

```xml
<!-- AI 模块 -->
<dependency>
    <groupId>com.mc</groupId>
    <artifactId>mc-ai</artifactId>
    <version>${mc.version}</version>
</dependency>

<!-- Spring AI Alibaba BOM -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-bom</artifactId>
    <version>1.0.0.2</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

### 9.2 `mc-ai/pom.xml` 中的依赖（按功能分组）

```xml
<!-- ① Spring AI Alibaba DashScope 核心（模型调用） -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
</dependency>

<!-- ② Redis 对话记忆（ChatMemory 持久化） -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-memory-redis</artifactId>
</dependency>

<!-- ③ Jedis 连接池（RedisChatMemoryRepository 依赖，勿漏） -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>

<!-- ④ Apache HttpClient5（RestTemplate 底层） -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.4.4</version>
</dependency>

<!-- ⑤ mc-framework（引入 Spring Context/Web 基础） -->
<dependency>
    <groupId>com.mc</groupId>
    <artifactId>mc-framework</artifactId>
</dependency>
```

> ⚠️ **踩坑记录**：`spring-ai-alibaba-starter-memory-redis` 依赖 `RedisChatMemoryRepository`，而该类底层使用 Jedis 连接池。初次迁移时漏加 Jedis 依赖导致 `ClassNotFoundException: redis.clients.jedis.JedisPoolConfig` 启动报错。

---

## 十、重构过程中的常见问题

### Q1：IDEA Maven 面板显示模块"已忽略"？

**原因**：`.idea/misc.xml` 中把 `mc-ai/pom.xml` 加入到 `ignoredFiles` 列表中。

**解决**：删除 `.idea/misc.xml` 中的对应行：

```xml
<option name="ignoredFiles">
  <set>
    <option value="$PROJECT_DIR$/business-app/pom.xml" />
    <!-- 删除这一行 -->
    <!-- <option value="$PROJECT_DIR$/mc-ai/pom.xml" /> -->
  </set>
</option>
```

### Q2：启动报 `ClassNotFoundException: redis.clients.jedis.JedisPoolConfig`？

**原因**：`mc-ai/pom.xml` 漏加了 Jedis 依赖。

**解决**：在 `mc-ai/pom.xml` 的 `<dependencies>` 中添加：

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```

### Q3：提示词中 JSON 格式不规范导致反序列化失败？

**原因**：AI 返回的 JSON 字段名与 Java record 不匹配（如 snake_case vs camelCase）。

**解决**：
1. 使用 `@JsonProperty("field_name")` 注解映射
2. 或在提示词中明确指定 JSON 字段名为 Java 约定的 camelCase
3. 开启 Spring AI 的 lenient 模式处理尾部逗号等问题

### Q4：流式对话中 `fullContentHolder` 在多线程环境下是否安全？

**安全**。`fullContentHolder` 是 `StringBuilder`，在 Reactor 的单个订阅线程中被 `append`（`map` 操作符内），回调 `doOnComplete` / `doOnCancel` 也在同一线程执行，不存在并发写入问题。

---

## 十一、重构后的效果总结

| 维度 | 重构前 | 重构后 |
|------|--------|--------|
| **模块职责** | `mc-project` 混杂 AI 能力和业务逻辑 | `mc-ai` 专注 AI 能力，`mc-project` 专注业务 |
| **依赖方向** | 混乱，隐式循环依赖风险 | 单向：`mc-project` → `mc-ai` |
| **编译安全** | 部分依赖靠运行时反射 | 完全依赖编译期验证 |
| **复用性** | 复用 AI 能力必须引入整个 `mc-project` | 其他项目只需引入 `mc-ai` |
| **代码复用** | ChatClient 配置与业务层耦合 | ChatClient 可独立测试 |
| **工具注册** | 硬编码或反射 | 运行时自动扫描，零配置 |
| **提示词管理** | 散落在各 Service 类中 | 集中在 `AiPrompts.java`，统一管理 |
| **流式对话** | 写库逻辑耦合在 Service | 写库在 Controller 回调中，完全解耦 |

---

## 十二、后续优化方向

- [ ] 将 `mc-ai` 打包为独立 Maven 构件，发布到私有仓库，供其他项目直接引用
- [ ] 为 `mc-ai` 编写单元测试，直接 mock `ChatMemory` 和 `ChatClient`
- [ ] 补充 AI 对话的 Token 统计和计费功能（`mc-ai` 层可扩展）
- [ ] 引入 Spring AI 的 `PromptTemplate` 替代字符串拼接，提升提示词复用性
- [ ] 流式对话增加限流保护，防止高频调用
