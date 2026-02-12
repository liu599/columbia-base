# 智能工作文件助手项目

## 产品需求, 架构及详细设计

### 需求及目标

日常工作的上下文是极度碎片化的：决策依据散落在Slack的零碎对话、飞书的文档、Salesforce的仪表盘以及储存在老员工脑海中的隐性知识里。目前，人类依然是这些碎片化系统之间的“胶水”，通过不断的复制粘贴和标签页切换来维持运转。在解决“上下文碎片化”这一工程难题之前，AI Agent（智能体）将始终难以突破狭窄的用例，无法从辅助工具进化为真正的自动驾驶系统。
本项目将作为人类的外挂智能大脑, 随时随地解决工作中的问题。

### 竞品分析

- https://detail.design/
    - 不足: 传统搜索
    - 不足: 收集方式单一
    - 不足: 没有智能化, 不能随时随地解决问题
    - 优点: 动态展示一些idea的交互形式

### 产品路线图

- [Phase1 MVP阶段 304ca6726ca88065bb57c500c3ff6be2.md](docs/design/roadmap/Phase1%20MVP%E9%98%B6%E6%AE%B5%20304ca6726ca88065bb57c500c3ff6be2.md)
- [04796d00-346b-4235-a0af-0e91511bc08c_Phase2_复杂场景深入挖掘阶段.pdf](docs/design/roadmap/04796d00-346b-4235-a0af-0e91511bc08c_Phase2_%E5%A4%8D%E6%9D%82%E5%9C%BA%E6%99%AF%E6%B7%B1%E5%85%A5%E6%8C%96%E6%8E%98%E9%98%B6%E6%AE%B5.pdf)
- [Phase3 企业级用户权限、产品矩阵、商业化闭环与观测 304ca6726ca88085b000fe10dda029d8.md](docs/design/roadmap/Phase3%20%E4%BC%81%E4%B8%9A%E7%BA%A7%E7%94%A8%E6%88%B7%E6%9D%83%E9%99%90%E3%80%81%E4%BA%A7%E5%93%81%E7%9F%A9%E9%98%B5%E3%80%81%E5%95%86%E4%B8%9A%E5%8C%96%E9%97%AD%E7%8E%AF%E4%B8%8E%E8%A7%82%E6%B5%8B%20304ca6726ca88085b000fe10dda029d8.md)


### 技术架构

- [系统技术总体架构说明书 (Technical Architecture Specification) 304ca6726ca880058589e7ee3eecd2b2.md](docs/design/%E7%B3%BB%E7%BB%9F%E6%8A%80%E6%9C%AF%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%84%E8%AF%B4%E6%98%8E%E4%B9%A6%20%28Technical%20Architecture%20Specification%29%20304ca6726ca880058589e7ee3eecd2b2.md)
  - [0bf25189-f97f-45f1-90bb-982b00533c4f_异步长耗时任务架构.pdf](docs/design/structure/0bf25189-f97f-45f1-90bb-982b00533c4f_%E5%BC%82%E6%AD%A5%E9%95%BF%E8%80%97%E6%97%B6%E4%BB%BB%E5%8A%A1%E6%9E%B6%E6%9E%84.pdf)
  - [9b2f5144-cbca-4b03-8dd9-33a563cd721c_智能体总体架构.pdf](docs/design/structure/9b2f5144-cbca-4b03-8dd9-33a563cd721c_%E6%99%BA%E8%83%BD%E4%BD%93%E6%80%BB%E4%BD%93%E6%9E%B6%E6%9E%84.pdf)
  - [58a34606-1e8f-4785-8c72-073d332ac11c_AI_智能问答架构实施规范.pdf](docs/design/structure/58a34606-1e8f-4785-8c72-073d332ac11c_AI_%E6%99%BA%E8%83%BD%E9%97%AE%E7%AD%94%E6%9E%B6%E6%9E%84%E5%AE%9E%E6%96%BD%E8%A7%84%E8%8C%83.pdf)



## 本repo部署脚本

- mvn clean install
- nohup java -jar /root/columbia-base/api/target/api-1.0-rc.jar > /dev/null 2>&1 &