# Milvus 向量数据库部署指南（MindCampus 专用·低内存优化版）

## 你的环境

| 项目 | 值 |
|------|-----|
| 机器内存 | ~7.4 GB（**关键约束**） |
| Milvus 版本 | v2.4.8 |
| 向量维度 | 1536（text-embedding-v3） |
| 集合名 | `knowledge_base_chunks` |
| 连接地址 | `https://localhost:19530` |
| Java SDK | MilvusClientV2（v2 API） |

> 官方推荐 Milvus 至少 8GB 内存，你的机器略低于推荐值，因此**必须使用轻量模式**，并在 docker-compose 中限制内存上限。

---

## 一、安装 Docker（如未安装）

```bash
# Windows: 下载 Docker Desktop https://www.docker.com/products/docker-desktop/
# 启动后确保分配给 Docker 的内存不超过 3GB（Docker Desktop 设置 -> Resources）

# CentOS/RHEL 安装
sudo yum install -y yum-utils device-mapper-persistent-data lvm2
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl start docker
sudo systemctl enable docker
docker --version
```

---

## 二、部署 Milvus（轻量版·单文件）

> 使用 Milvus **内置 etcd + 本地存储**，无需额外启动 MinIO 等组件，内存占用最小。

### 1. 创建目录

```bash
mkdir -p ~/milvus
cd ~/milvus
```

### 2. 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  milvus:
    image: milvusdb/milvus:v2.4.8
    container_name: milvus
    restart: always
    ports:
      - "19530:19530"      # Milvus 服务端口（应用连接用）
      - "9091:9091"        # 健康检查端口
    environment:
      ETCD_USE_EMBED: true          # 使用内置 etcd（无需单独部署）
      COMMON_STORAGETYPE: local     # 使用本地磁盘存储（非 MinIO）
    volumes:
      - ./milvus_data:/var/lib/milvus
    # 内存限制：你的机器只有7.4GB，Milvus最多用3.5GB，留给系统和其他进程
    mem_limit: 3500m
    # 交换空间限制
    memswap_limit: 3500m
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9091/healthz"]
      interval: 30s
      timeout: 20s
      retries: 5
      start_period: 120s
```

### 3. 启动

```bash
docker compose up -d
```

### 4. 查看状态

```bash
# 查看容器是否正常运行
docker compose ps

# 查看日志（首次启动需要 1-3 分钟初始化索引，耐心等待）
docker compose logs -f milvus
```

### 5. 验证启动成功

```bash
# 检查健康状态
curl http://localhost:9091/healthz
# 返回 {"status":"OK"} 即表示启动成功

# 如果返回空或报错，继续等一会再看日志
docker compose logs milvus --tail 100
```

---

## 三、Spring Boot 配置

你项目中的配置已经就绪，无需修改。如果需要本地调试：

```yaml
# application.yml 中已有，无需修改
spring:
  milvus:
    uri: ${MILVUS_URI:https://localhost:19530}
```

> 注意：这里 uri 是 `https://`，但本地 Docker 启动的 Milvus 是 `http://`。本地调试时可以改为 `http://localhost:19530`，或者保持 `https://` 让 Milvus 前加一层代理。

---

## 四、验证连接（Python 测试）

```bash
pip install pymilvus

python << 'PYEOF'
from pymilvus import connections, utility

connections.connect(
    alias="default",
    host="localhost",
    port="19530"
)

print("Milvus 连接成功！")
print("集合列表:", utility.list_collections())
connections.disconnect("default")
PYEOF
```

---

## 五、常见问题

### Q1: 启动后 Milvus 一直是 unhealthy？

**原因**：首次启动 Milvus 会自动创建系统索引，耗时约 1-3 分钟。另外 mem_limit 设置过低也会导致启动失败。

**排查步骤**：

```bash
# 查看详细日志
docker compose logs -f milvus

# 检查是否被 OOM kill
dmesg | grep -i oom | tail -5
```

**解决**：
- 耐心等待 3 分钟
- 如果日志显示 OOM，增加 docker-compose.yml 中的 `mem_limit` 到 `4g` 或更高（如果内存允许）
- 如果机器内存确实不够，可以考虑使用 [Zilliz Cloud](https://www.zilliz.com/)（Milvus 的云服务，无需本地部署）

### Q2: 内存不够但想用 Milvus？

可以考虑以下方案：

| 方案 | 内存占用 | 说明 |
|------|---------|------|
| 当前方案（轻量版） | ~2-3 GB | 适合 7-8 GB 机器 |
| Zilliz Cloud（免费版） | 0 GB | Milvus 云服务，API 兼容，有免费额度 |
| 升级内存到 16 GB | - | 一劳永逸 |

### Q3: 端口被占用？

```bash
# Windows
netstat -ano | findstr 19530

# Linux
sudo netstat -tlnp | grep 19530
```

### Q4: 想切换到 Zilliz Cloud（无需本地部署）？

1. 注册 [Zilliz Cloud](https://www.zilliz.com/) 并创建免费集群
2. 获取集群 URI 和 Token
3. 修改 `application.yml`：

```yaml
spring:
  milvus:
    uri: https://xxx.zillizcloud.com:19530
    token: your_api_token
```

---

## 六、运维命令

```bash
# 停止
docker compose down

# 重启
docker compose restart milvus

# 查看资源占用
docker stats

# 更新 Milvus 版本（修改镜像版本后重新 up）
# 1. 修改 docker-compose.yml 中的 image 版本
# 2. 执行：
docker compose down
docker compose pull
docker compose up -d

# 清理所有数据（慎用！会删除知识库向量数据）
docker compose down -v
rm -rf ./milvus_data
```

---

## 七、你项目中的 Milvus 配置汇总

```java
// MilvusVectorClient.java 中的配置
private static final String COLLECTION_NAME = "knowledge_base_chunks";
private static final int VECTOR_DIMENSION = 1536;
private static final String VECTOR_FIELD = "embedding";

// application.yml 中的配置
spring:
  milvus:
    uri: ${MILVUS_URI:https://localhost:19530}
  knowledge:
    embedding:
      model: text-embedding-v3
      dimension: 1536
      batch-size: 25
```

部署完成后，刷新 Spring Boot 应用，Milvus 会自动创建 `knowledge_base_chunks` 集合。
