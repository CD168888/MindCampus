# SSE流式对话重复插入问题修复方案

> 基于Spring AI Alibaba的AI聊天系统中，流式输出暂停时出现重复插入问题的分析与解决

## 📌 问题背景

在实现AI聊天系统的过程中，我们使用了SSE（Server-Sent Events）技术来实现流式对话输出。然而在测试过程中发现，当用户暂停流式输出时，数据库中会出现**两条相同的AI回复记录**。

### 问题现象

```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "messageId": 77,
            "sessionId": 23,
            "messageType": 0,
            "content": "你好呀，我是你的心理陪伴伙伴...",
            "createBy": "xiaodu"
        },
        {
            "messageId": 78,
            "sessionId": 23,
            "messageType": 0,
            "content": "你好呀，我是你的心理陪伴伙伴...",
            "createBy": "system"
        }
    ]
}
```

可以看到，同一条AI消息被插入了两次（messageId 77 和 78），内容完全一致。

---

## 🔍 问题分析

### 流程回溯

让我们先了解整个流式对话的执行流程：

#### 1️⃣ 前端暂停操作
```javascript
// chat.vue - 中断流式输出
async handleCancelStream() {
    // 关闭EventSource连接，停止接收数据
    this.closeEventSource();
    
    // 调用后端接口停止生成
    if (this.sessionId) {
        await cancelStream(this.sessionId);
    }
}
```

#### 2️⃣ 后端处理暂停
```java
// AiChatStreamServiceImpl.java
public boolean cancelStream(Long sessionId) {
    String chatId = String.valueOf(sessionId);
    AtomicBoolean isStreaming = streamingStates.get(chatId);
    
    if (isStreaming != null) {
        isStreaming.set(false);  // 设置为false，触发停止
        return true;
    }
    return false;
}
```

#### 3️⃣ Reactive流的处理链
```java
return dashScopeChatClient.prompt()
    .user(message)
    .system(SYSTEM_PROMPT)
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
    .stream()
    .content()
    .takeWhile(data -> isStreaming.get())  // 当isStreaming变为false时停止
    .map(content -> {
        aiResponse.append(content);
        return ServerSentEvent.<String>builder()
                .data(content)
                .build();
    })
    .concatWith(Flux.just(ServerSentEvent.<String>builder()
            .data("\u0003")  // 结束标记
            .build()))
    .doOnComplete(() -> handleStreamComplete(...))   // 完成回调
    .doOnCancel(() -> handleStreamCancel(...))       // 取消回调
    .doOnError(error -> handleStreamError(...))      // 错误回调
    .onErrorResume(error -> handleStreamErrorResponse(...));
```

### 问题根源

当 `isStreaming.set(false)` 被调用后，`takeWhile` 操作符会停止接收上游数据，但这会**同时触发两个回调**：

1. **`doOnCancel`** - 因为流被中断
2. **`doOnComplete`** - 因为流正常结束

**原有代码的问题**：

```java
// 完成回调 - 保存消息
private void handleStreamComplete(...) {
    if (aiResponse.length() > 0) {
        chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);  // ✅ 第一次保存
    }
    // ...
}

// 取消回调 - 也保存消息
private void handleStreamCancel(...) {
    if (aiResponse.length() > 0) {
        chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);  // ❌ 第二次保存
    }
    // ...
}
```

这导致了**重复插入**问题！

---

## 💡 解决方案：基于CAS的幂等性保障

### 什么是CAS？

**CAS（Compare-And-Set）** 是一种无锁的原子操作，它包含三个操作数：
- **V（内存位置）**：要更新的变量
- **A（预期值）**：期望变量当前的值
- **B（新值）**：要设置的新值

**操作逻辑**：
```
如果 V == A，则将 V 设置为 B，返回 true
否则，不做任何修改，返回 false
```

这个操作是**原子的**，即在多线程环境下，只有一个线程能成功执行。

### Java中的CAS实现

Java通过 `java.util.concurrent.atomic` 包提供CAS支持：

```java
AtomicBoolean flag = new AtomicBoolean(false);

// CAS操作：如果当前值是false，则设置为true
boolean success = flag.compareAndSet(false, true);

// 第一次调用：success = true，flag变为true
// 第二次调用：success = false，flag保持true
```

### 解决方案实现

#### 步骤1：添加消息保存标识

```java
public class AiChatStreamServiceImpl implements IAiChatStreamService {
    
    // 存储每个会话的流式状态标识
    private final ConcurrentHashMap<String, AtomicBoolean> streamingStates = new ConcurrentHashMap<>();
    
    // 存储每个会话是否已保存AI消息的标识（防止重复保存）
    private final ConcurrentHashMap<String, AtomicBoolean> messageSavedFlags = new ConcurrentHashMap<>();
    
    // ...
}
```

#### 步骤2：初始化标识

```java
public Flux<ServerSentEvent<String>> generateStreamResponse(String message, Long sessionId, Long userId) {
    String chatId = String.valueOf(sessionId);
    
    // 为当前会话创建流式状态标识
    AtomicBoolean isStreaming = streamingStates.computeIfAbsent(chatId, k -> new AtomicBoolean(true));
    isStreaming.set(true);
    
    // 重置消息保存标识（开始新的流式对话）
    AtomicBoolean messageSaved = messageSavedFlags.computeIfAbsent(chatId, k -> new AtomicBoolean(false));
    messageSaved.set(false);  // 初始化为false，表示未保存
    
    StringBuilder aiResponse = new StringBuilder();
    
    // ... 流式处理逻辑
}
```

#### 步骤3：修改完成回调

```java
private void handleStreamComplete(Long sessionId, Long userId, String userMessage, String aiResponse, String chatId) {
    // 使用CAS确保只保存一次AI消息（防止与doOnCancel重复）
    AtomicBoolean messageSaved = messageSavedFlags.get(chatId);
    
    if (messageSaved != null && messageSaved.compareAndSet(false, true)) {
        // 只有第一次进入（messageSaved从false变为true）才会保存
        if (aiResponse.length() > 0) {
            chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);
            log.info("流式对话完成，已保存AI消息 [sessionId: {}, 长度: {}]", sessionId, aiResponse.length());
        }
        
        // 如果是会话的第一次对话，生成会话标题
        List<AiChatMessage> sessionMessages = chatMessageService.getSessionMessages(sessionId);
        if (sessionMessages.size() == 2) {
            generateSessionTitleAsync(sessionId, userMessage, aiResponse);
        }
    } else {
        log.info("流式对话完成，但消息已被保存，跳过 [sessionId: {}]", sessionId);
    }
    
    // 清理资源
    streamingStates.remove(chatId);
    messageSavedFlags.remove(chatId);
}
```

#### 步骤4：修改取消回调

```java
private void handleStreamCancel(Long sessionId, Long userId, String aiResponse, String chatId) {
    // 使用CAS确保只保存一次AI消息（防止与doOnComplete重复）
    AtomicBoolean messageSaved = messageSavedFlags.get(chatId);
    
    if (messageSaved != null && messageSaved.compareAndSet(false, true)) {
        // 即使取消也保存部分回复
        if (aiResponse.length() > 0) {
            chatMessageService.saveMessage(sessionId, userId, 0, aiResponse);
            log.info("流式对话被取消，已保存部分AI消息 [sessionId: {}, 长度: {}]", sessionId, aiResponse.length());
        }
    } else {
        log.info("流式对话被取消，但消息已被保存，跳过 [sessionId: {}]", sessionId);
    }
    
    // 清理资源
    streamingStates.remove(chatId);
    messageSavedFlags.remove(chatId);
}
```

### 执行流程分析

#### 场景1：正常完成流式输出

```
1. 开始流式输出
   ├─ messageSaved = false
   └─ 流式数据传输中...

2. AI生成完成
   ├─ doOnComplete() 被调用
   ├─ compareAndSet(false, true) → 返回 true ✅
   ├─ 保存AI消息到数据库
   └─ messageSaved = true

3. 流结束
   └─ 只保存了一次 ✅
```

#### 场景2：用户暂停流式输出

```
1. 开始流式输出
   ├─ messageSaved = false
   └─ 流式数据传输中...

2. 用户点击"停止生成"
   ├─ isStreaming.set(false)
   ├─ takeWhile() 停止接收数据
   │
   ├─ doOnCancel() 首先被触发
   │  ├─ compareAndSet(false, true) → 返回 true ✅
   │  ├─ 保存部分AI消息
   │  └─ messageSaved = true
   │
   └─ doOnComplete() 随后被触发
      ├─ compareAndSet(true, true) → 返回 false ❌
      └─ 跳过保存，避免重复 ✅

3. 流结束
   └─ 只保存了一次 ✅
```

**核心优势**：无论 `doOnCancel` 和 `doOnComplete` 哪个先执行，CAS操作都能保证**有且仅有一个**能成功保存消息！

---

## 📚 技术知识深入

### 一、SSE（Server-Sent Events）

#### 什么是SSE？

SSE是HTML5引入的一种服务器推送技术，允许服务端主动向客户端推送数据。

**特点**：
- ✅ 单向通信（服务器 → 客户端）
- ✅ 基于HTTP协议，兼容性好
- ✅ 自动重连机制
- ✅ 简单轻量，适合文本数据流
- ❌ 只支持UTF-8编码
- ❌ 不支持二进制数据

#### SSE vs WebSocket

| 特性 | SSE | WebSocket |
|------|-----|-----------|
| 通信方向 | 单向（服务器→客户端） | 双向 |
| 协议 | HTTP | WebSocket协议 |
| 数据格式 | 文本（UTF-8） | 文本/二进制 |
| 重连 | 自动 | 需手动实现 |
| 浏览器兼容 | IE不支持 | 广泛支持 |
| 适用场景 | 实时推送、进度更新 | 即时通讯、游戏 |

#### SSE数据格式

```
data: 这是第一条消息\n\n

data: 这是第二条消息\n\n

event: customEvent\n
data: 自定义事件消息\n\n

data: 多行消息第一行\n
data: 多行消息第二行\n\n
```

**重要规则**：
- 每条消息以 `\n\n` 结尾
- `data:` 后跟消息内容
- `event:` 可指定自定义事件类型

#### 前端使用SSE

```javascript
const eventSource = new EventSource('/api/ai/chatStream?message=你好&sessionId=123');

// 监听消息
eventSource.onmessage = function(event) {
    console.log('收到消息:', event.data);
};

// 监听自定义事件
eventSource.addEventListener('sessionId', function(event) {
    console.log('收到会话ID:', event.data);
});

// 错误处理
eventSource.onerror = function(error) {
    console.error('SSE错误:', error);
    eventSource.close();
};

// 关闭连接
eventSource.close();
```

#### 后端实现SSE（Spring WebFlux）

```java
@GetMapping(value = "/chatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> generate(@RequestParam String message) {
    return Flux.interval(Duration.ofSeconds(1))
        .map(seq -> ServerSentEvent.<String>builder()
            .data("消息 " + seq)
            .build())
        .take(10);
}
```

### 二、Spring AI Alibaba的Reactive流处理

#### Reactive编程模型

Spring AI Alibaba基于**Project Reactor**实现响应式流处理：

```java
Flux<String> stream = dashScopeChatClient.prompt()
    .user(message)
    .stream()
    .content();
```

**Flux** 是Reactor中的核心类型，表示0到N个元素的异步序列。

#### 关键操作符

##### 1. `takeWhile` - 条件终止

```java
Flux.range(1, 10)
    .takeWhile(n -> n < 5)
    .subscribe(System.out::println);
// 输出: 1, 2, 3, 4
```

在我们的场景中：
```java
.takeWhile(data -> isStreaming.get())
```
当 `isStreaming` 变为 `false` 时，流会停止接收新数据。

##### 2. `map` - 数据转换

```java
.map(content -> {
    aiResponse.append(content);
    return ServerSentEvent.<String>builder()
        .data(content)
        .build();
})
```

将AI生成的文本内容转换为SSE格式的事件。

##### 3. `concatWith` - 拼接流

```java
.concatWith(Flux.just(ServerSentEvent.<String>builder()
    .data("\u0003")  // ETX (End of Text)
    .build()))
```

在流的末尾添加结束标记。

##### 4. 生命周期钩子

```java
.doOnComplete(() -> {})    // 流正常完成时触发
.doOnCancel(() -> {})      // 流被取消时触发
.doOnError(error -> {})    // 流发生错误时触发
```

**重要**：当使用 `takeWhile` 停止流时，**可能同时触发** `doOnCancel` 和 `doOnComplete`！

#### 完整的流处理链

```java
return dashScopeChatClient.prompt()
    .user(message)                                    // 用户输入
    .system(SYSTEM_PROMPT)                            // 系统提示词
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))  // 会话上下文
    .stream()                                         // 启用流式输出
    .content()                                        // 提取内容
    .takeWhile(data -> isStreaming.get())            // 条件终止
    .map(content -> {                                 // 转换为SSE事件
        aiResponse.append(content);
        return ServerSentEvent.<String>builder()
                .data(content)
                .build();
    })
    .concatWith(Flux.just(ServerSentEvent.<String>builder()
            .data("\u0003")                           // 结束标记
            .build()))
    .doOnComplete(() -> handleStreamComplete(...))   // 完成回调
    .doOnCancel(() -> handleStreamCancel(...))       // 取消回调
    .doOnError(error -> handleStreamError(...))      // 错误回调
    .onErrorResume(error -> handleStreamErrorResponse(...));  // 错误恢复
```

### 三、CAS（Compare-And-Set）原子操作

#### CAS的底层实现

CAS是一种CPU级别的原子指令，在x86架构中对应 `CMPXCHG` 指令：

```assembly
CMPXCHG [内存地址], 新值, 预期值
```

**执行过程**：
1. 比较内存地址的值与预期值
2. 如果相等，则更新为新值
3. 如果不等，则不做任何操作
4. 返回操作是否成功

**原子性保证**：CPU通过**总线锁定**或**缓存锁定**机制，确保整个CAS操作的原子性。

#### Java中的CAS

Java通过 `Unsafe` 类提供CAS支持，但通常我们使用更高层的API：

```java
// AtomicBoolean
AtomicBoolean flag = new AtomicBoolean(false);
boolean success = flag.compareAndSet(false, true);

// AtomicInteger
AtomicInteger counter = new AtomicInteger(0);
int oldValue = counter.getAndIncrement();  // 内部使用CAS

// AtomicReference
AtomicReference<String> ref = new AtomicReference<>("old");
ref.compareAndSet("old", "new");
```

#### CAS的优势与局限

**优势**：
- ✅ 无锁（Lock-Free），避免线程阻塞
- ✅ 性能高，适合低竞争场景
- ✅ 避免死锁

**局限**：
- ❌ ABA问题（可用版本号解决）
- ❌ 高竞争下自旋开销大
- ❌ 只能保证单个变量的原子性

#### CAS在本方案中的应用

```java
// 初始状态
AtomicBoolean messageSaved = new AtomicBoolean(false);

// 线程A（doOnCancel）
if (messageSaved.compareAndSet(false, true)) {
    // 成功！保存消息
    saveMessage(...);
}

// 线程B（doOnComplete）
if (messageSaved.compareAndSet(false, true)) {
    // 失败！因为messageSaved已经是true
    // 跳过保存
}
```

**关键点**：
- `compareAndSet(false, true)` 确保只有一个线程能成功
- 失败的线程会跳过保存逻辑，避免重复

---

## ✅ 验证与总结

### 验证步骤

1. **正常对话测试**
   - 发送消息，等待AI完全回复
   - 检查数据库，应只有1条AI记录 ✅

2. **暂停测试**
   - 发送消息，立即点击"停止生成"
   - 检查数据库，应只有1条AI记录（部分内容） ✅

3. **并发测试**
   - 快速发送多条消息
   - 随机暂停部分对话
   - 检查数据库，每条对话只有1条AI记录 ✅

### 核心要点总结

1. **问题本质**：Reactive流的生命周期钩子可能被多次触发
2. **解决思路**：使用CAS操作实现幂等性保障
3. **技术栈**：
   - SSE实现实时流式输出
   - Spring AI Alibaba提供AI能力
   - Project Reactor处理响应式流
   - CAS原子操作保证并发安全

### 最佳实践

1. **幂等性设计**：对于可能重复执行的操作，务必实现幂等性
2. **原子操作**：使用CAS等原子操作替代锁，提升性能
3. **资源清理**：在finally或回调中及时清理资源（如Map中的标识）
4. **日志记录**：详细记录关键操作，便于问题排查

---

## 🎯 总结

本次修复通过引入**CAS原子操作**，成功解决了SSE流式对话中的重复插入问题。这个方案不仅修复了Bug，更展示了以下技术要点：

- 📡 SSE技术在实时通信中的应用
- 🔄 Reactive编程在Spring AI中的实践
- 🔒 CAS无锁并发控制机制
- 💡 幂等性设计在分布式系统中的重要性

希望这篇文章能帮助你深入理解SSE、Reactive流处理和CAS机制，在实际项目中避免类似问题！

---

**参考资料**：
- [Server-Sent Events - MDN](https://developer.mozilla.org/zh-CN/docs/Web/API/Server-sent_events)
- [Project Reactor Reference Guide](https://projectreactor.io/docs/core/release/reference/)
- [Spring AI Alibaba Documentation](https://spring.io/projects/spring-ai)
- [Java Concurrency in Practice](https://jcip.net/)

---

*作者：AI开发团队*  
*日期：2025-12-03*  
*版本：v1.0*
