# 系统技术总体架构说明书 (Technical Architecture Specification)

## 1. 项目概述

本系统旨在打造一个高性能、可扩展的**工作文档智能问答与知识管理平台**。系统采用分层演进的架构思想，依托 **Java + Python 混合微服务架构**，完美融合了 Java 在企业级复杂业务（如计费、账户、高并发）的严谨性，以及 Python 在 AI 推理与非结构化数据处理上的敏捷性。

![whiteboard_exported_image (1).png](whiteboard_exported_image_(1).png)

五大核心层级：**展现层、接入层、业务服务层、智能与基础服务层、基建层**。

## 2. 各层级详细设计与技术落地

### 2.1 展现层 (Presentation Layer)

展现层致力于提供全场景的用户覆盖，统一采用现代化的前端工程体系进行构建，以降低多端维护成本。

- **Web 端：** 面向桌面办公场景的主力入口。采用 **React 19 + Next.js** 构建，利用最新的 Server Components 优化首页加载时间（FCP），提升 SEO 效果及复杂交互的流畅度。
- **桌面端：** 面向重度办公用户的沉浸式体验。可基于 Electron 或 Tauri 封装 Web 端核心逻辑，提供本地文件系统的高效访问能力。
- **小程序 / 移动端：** 满足移动端扫码登录, 随时随地添加知识库并轻量问答的场景。
- **开发者入口 (API/SDK)：** 面向企业客户或第三方开发者，提供标准化的 RESTful API 与接入 SDK，支持将文档问答能力嵌入到客户自有的 OA 或 ERP 系统中。

### 2.2 接入层 (Access Layer)

所有来自展现层的外部请求，均需经过接入层的统一管控，确保内网服务的安全与纯净。

- **代理 Gateway (Nginx / APISIX)：** 负责全局流量的路由分发、L4/L7 负载均衡、SSL 证书卸载、跨域处理（CORS）以及防刷限流。
- **鉴权服务：** 统一的“安全门神”。负责验证 Token（如 JWT），进行会话状态校验与基础的访问控制（RBAC/ABAC）。未授权请求将被直接拦截，不再透传至下游。

### 2.3 业务服务层 (Business Logic Layer)

本层是面向用户具体业务流程的聚合层，主要由 **Java (Spring Boot)** 微服务集群构成，负责处理高吞吐和强事务相关的业务。

- **知识资产服务：** 管理用户的工作区、文件夹树形结构、文档权限（公开/私有）及分享逻辑。
- **智能交互服务：** 问答会话的聚合网关。负责接收用户提问，调用下游的 Python AI 推理服务，并将结果流式（SSE / WebSocket）推送到前端。
- **用户入驻与账户服务：** **(核心闭环)** 专门负责**用户注册**、登录（支持多端单点登录）、密码找回、个人资料（Profile）维护以及账户状态的生命周期管理。
- **商业权益与会员中心：** 系统的“收银台”。处理复杂的增值套餐（ProductPlan）售卖逻辑、订单创建、支付状态回调及会员等级计算。
- **异步任务引擎：** 负责接收业务层的异步指令（如：批量导出、长效报告生成），并投递给底层执行。

### 2.4 智能与基础服务层 (AI & Core Services)

这是系统的“大脑”与“账房”，按职责划分为 Python AI 阵营和 Java 核心领域阵营。

**【AI 引擎组 (Python 驱动)】** 采用 FastAPI 构建，结合 LangGraph 等智能体框架：

- **知识资产内核：**
    - *FileMetadata:* 文件深度解析与清洗。
    - *DocumentChunk:* 针对不同文档类型（PDF, Word）的智能分块策略。
    - *WebSnapshot:* 网页链接内容的抓取与快照归档。
- **AI 推理/上下文工程：**
    - *ChatSession & MemoryBuffer:* 基于 Redis 管理多轮对话上下文。
    - *SkillManifest & Prompt:* 提示词工程管理及大模型能力路由（Intent Routing）。

**【核心基础组 (Java 驱动)】** 结合 Spring Boot + MyBatis Plus 等持久层框架：

- **身份与账号：** 细粒度的 *UserAccount* 和 *AuthCredential* 凭证加密存储。
- **商业权益与计费监控：**
    - *UserSubscription:* 记录用户当前生效的订阅状态。
    - *QuotaLedger (额度账本):* 每次 AI 问答后，进行极其精确的 Token 消耗记录与扣费，确保账务不超卖、不漏扣。
- **异步任务与基础服务：** 包含 *Notification* (站内信/邮件触达) 和 *UsageMetric* (系统用量统计)。

### 2.5 基建层 (Infrastructure Layer)

为上层应用提供坚实的数据存储、中间件及监控支撑。

- **管理中台：** 提供给内部运营人员使用的可观测中台（链路追踪 Tracing、日志 Logging、指标 Metrics）和商业中台（财务对账、账单管理）。
- **基础设施中间件：**
    - **MySQL：** 作为核心的关系型数据库，保障用户注册数据、订单流水、套餐配置的 ACID 强一致性。
    - **ES-Serverless：** 承载 RAG 架构中的检索重任。支持混合检索（Hybrid Search），即将传统的 **TF-IDF 关键词倒排索引**与现代的 **Vector 向量检索**相结合，最大化提升 AI 问答的召回准确率。
    - **Redis：** 支撑高并发的分布式锁（如防重复提问、防重复支付）、限流计数器及热点配置缓存。
    - **MQ / Kafka：** 用于系统间的解耦与削峰。例如：用户上传大文件后，通过 MQ 异步通知 Python 服务进行切片和向量化；订单支付成功后，通过 MQ 异步派发额度。
    - **OSS：** 海量非结构化数据的安身之所，存储原始文件、图片及各类快照。
    - **AI-Model：** 抽象的模型调用池，可对接外部大模型 API 以及内部部署的私有化模型。

## 3. 研发协作与规范建议

为了让 Java 与 Python 团队以及前端团队高效配合，建议遵循以下规范：

1. **API 契约先行：** 各层之间的调用统一通过 Swagger/OpenAPI 进行接口定义。业务服务与基础服务之间可以通过轻量级的 RESTful JSON 交互，若对延迟要求极高，可引入 gRPC。
2. **事务边界划分：** Java 负责所有资金、额度、注册信息的分布式事务控制；Python 则作为纯粹的无状态计算节点，失败可随时重试，不处理业务状态回滚。
3. **流式通信标准：** 针对“智能交互服务”到展现层的问答链路，必须统一采用 Server-Sent Events (SSE) 协议，以确保各端打字机效果的体验一致性。

| 维度 | 选型 | 理由 |
| --- | --- | --- |
| 后端架构 | Java (Spring Boot) + Python (FastAPI/FastStream) | Java 保证业务系统的稳定性、事务性及生态成熟度；Python 适配 LLM 生态（LangChain, LlamaIndex），方便进行算法迭代。 |
| 前端框架 | React 19 | 利用最新的 Server Components 及 Actions 特性，提升 Web 端性能。 |
| 多端覆盖 | Next.js (Web) / Taro (小程序) / Electron (桌面端) | 统一 React 技术栈，最大化组件复用率。 |
| 搜索技术 | Hybrid Search (ES Serverless) | 结合传统的 BM25 关键词检索 与 Vector Embedding 向量检索，确保问答召回率。 |
| 模型调用 | Gemini / 私有化模型 | 提供多模型接入能力，通过 API 网关进行统一调用与限流。 |
| 数据一致性 | MQ + 幂等设计 | 针对充值与权益下发，采用异步消息确保数据最终一致性。 |

[注册登录与产品订阅流程](https://www.notion.so/304ca6726ca880408b42d318314e354b?pvs=21)

[个人知识库收集/建立流程](https://www.notion.so/304ca6726ca880dbad80c6ae0643d47e?pvs=21)

[智能体总体架构](https://www.notion.so/304ca6726ca8808dba8df05dbc1ae80c?pvs=21)

[AI 智能问答架构实施规范](https://www.notion.so/AI-304ca6726ca880cf98bbec94f16a213a?pvs=21)

[异步长耗时任务架构](https://www.notion.so/304ca6726ca8802b9f4cf9cc71945472?pvs=21)