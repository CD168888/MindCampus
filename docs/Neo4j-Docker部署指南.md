# Neo4j Docker 部署指南

本文档介绍如何在 Linux 服务器（阿里云 ECS）上通过 Docker 部署 Neo4j 图数据库。

## 环境要求

- Linux 服务器（推荐 Ubuntu 20.04+ / CentOS 7+）
- Docker 和 Docker Compose 已安装
- 至少 2GB 内存
- 阿里云安全组开放端口：**7687**（Bolt）、**7474**（HTTP）

## 一、安装 Docker（如果尚未安装）

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | bash

# 启动并设置开机自启
systemctl start docker
systemctl enable docker

# 安装 Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

## 二、创建部署目录

```bash
mkdir -p ~/neo4j/data
mkdir -p ~/neo4j/logs
mkdir -p ~/neo4j/plugins
```

## 三、编写 docker-compose.yml

```bash
cd ~/neo4j
cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  neo4j:
    image: neo4j:5.23.0
    container_name: neo4j
    restart: always
    ports:
      - "7474:7474"   # HTTP 端口
      - "7687:7687"   # Bolt 端口
    environment:
      NEO4J_AUTH: neo4j/password
      NEO4J_ACCEPT_LICENSE_AGREEMENT: "yes"
      NEO4J_dbms_memory_heap_initial__size: 512m
      NEO4J_dbms_memory_heap_max__size: 2g
      NEO4J_dbms_memory_pagecache_size: 1g
      NEO4J_dbms_security_procedures_unrestricted: "apoc.*,gds.*"
      NEO4J_dbms_security_procedures_allowlist: "apoc.*,gds.*"
      NEO4J_PLUGINS: '["apoc", "graph-data-science"]'
    volumes:
      - ./data:/data
      - ./logs:/logs
      - ./plugins:/plugins
    healthcheck:
      test: ["CMD-SHELL", "wget -O- -q http://localhost:7474 || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 60s
EOF
```

## 四、启动 Neo4j

```bash
# 拉取镜像并启动
docker compose up -d

# 查看容器状态
docker compose ps

# 查看启动日志
docker compose logs -f
```

## 五、验证部署

### 1. 检查容器健康状态

```bash
docker compose ps
```

看到 `neo4j` 容器状态为 `healthy` 即表示启动成功（首次启动约需 30-60 秒）。

### 2. 访问 Neo4j Browser

在浏览器中访问：

```
http://8.221.103.214:7474
```

首次登录：
- 用户名：`neo4j`
- 密码：`password`

> 注意：首次登录后会要求修改密码，请设置一个强密码并妥善保管。

### 3. 测试 Bolt 连接

使用 Cypher Shell 测试：

```bash
docker exec -it neo4j cypher-shell -u neo4j -p password "RETURN 1"
```

### 4. 从外部测试连接

在本地 Windows PowerShell 中：

```powershell
# 测试 HTTP 端口
curl http://8.221.103.214:7474

# 测试 Bolt 端口（需要安装 neo4j 客户端或使用其他工具）
```

## 六、开放阿里云安全组端口

登录阿里云控制台 → ECS → 安全组 → 入方向规则添加：

| 协议 | 端口范围 | 来源 |
|------|---------|------|
| TCP | 7474/7474 | 0.0.0.0/0 |
| TCP | 7687/7687 | 0.0.0.0/0 |

## 七、配置说明

### 内存配置说明

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `NEO4J_dbms_memory_heap_initial__size` | 堆内存初始大小 | 512m |
| `NEO4J_dbms_memory_heap_max__size` | 堆内存最大大小 | 2g |
| `NEO4J_dbms_memory_pagecache_size` | 页缓存大小 | 1g |

> 生产环境建议根据服务器内存大小调整，一般设置为服务器总内存的 50%-70%。

### 插件说明

- **APOC**（Awesome Procedures On Cypher）：最常用的 Neo4j 扩展库
- **GDS**（Graph Data Science）：图算法库，用于图分析

如果不需要这些插件，可以移除 `NEO4J_PLUGINS` 环境变量。

## 八、日常运维

### 常用命令

```bash
# 停止
docker compose down

# 重启
docker compose restart

# 查看日志
docker compose logs -f neo4j

# 进入容器
docker exec -it neo4j bash

# 备份数据
docker exec -it neo4j neo4j-admin dump --database=neo4j --to=/data/backup_$(date +%Y%m%d).dump
```

### 修改密码

首次启动后，建议修改默认密码：

```bash
# 在容器内修改
docker exec -it neo4j cypher-shell -u neo4j -p password \
  "ALTER CURRENT USER SET PASSWORD 'YourNewPassword'"
```

同时更新 `docker-compose.yml` 中的 `NEO4J_AUTH` 环境变量。

### 数据持久化

数据存储在 `./data` 目录，删除容器不会丢失数据。删除所有数据：

```bash
docker compose down
rm -rf ./data ./logs
```

## 九、应用配置

更新 Spring Boot 的 `application.yml`：

```yaml
neo4j:
  uri: bolt://8.221.103.214:7687
  authentication:
    username: neo4j
    password: YourNewPassword  # 替换为你的实际密码
```

## 十、常见问题

### Q1: Neo4j 容器一直处于 restarting 状态

检查日志：
```bash
docker compose logs neo4j
```

可能原因：
- 端口被占用：`7474` 或 `7687` 已被其他程序占用
- 内存不足：服务器内存过小，Docker 无法分配足够内存

### Q2: 外部无法访问 7474 或 7687 端口

1. 检查容器是否正常运行：`docker compose ps`
2. 检查端口是否在监听：`netstat -tlnp | grep -E '7474|7687'`
3. 检查阿里云安全组是否已开放对应端口
4. 检查服务器防火墙：`systemctl status firewalld`，如需开放防火墙端口：
   ```bash
   firewall-cmd --zone=public --add-port=7474/tcp --permanent
   firewall-cmd --zone=public --add-port=7687/tcp --permanent
   firewall-cmd --reload
   ```

### Q3: 忘记 Neo4j 密码

重置密码：

```bash
# 停止容器
docker compose stop neo4j

# 以无认证模式启动
docker run --rm -e NEO4J_AUTH=none \
  -v $(pwd)/data:/data \
  --entrypoint neo4j-admin \
  neo4j:5.25.0 dbms reset-password --require-password
```

### Q4: APOC 插件无法使用

确保 `NEO4J_dbms_security_procedures_unrestricted` 和 `NEO4J_PLUGINS` 配置正确。也可以将 APOC JAR 文件手动放到 `./plugins` 目录并重启容器。
