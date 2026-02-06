# API 项目

这是一个基于 Java 21 和 Spring Boot 的 API 项目。

## 主要服务

该项目包括以下四个核心服务:

*   **产品服务**: 管理产品信息。
*   **计费服务**: 处理用户计费与额度。
*   **用户服务**: 负责用户注册、登录和信息管理。
*   **后台管理**: 提供对产品、用户、计费等的管理功能。

## 技术栈

*   Java 21
*   Spring Boot
*   MySQL
*   MyBatis
*   ... (其他依赖请查看 `pom.xml`)

## 如何启动项目

1.  **配置环境**: 确保您已安装 Java 21 和 Maven。
2.  **数据库**:
    *   在您的 MySQL 数据库中，创建一个新的数据库。
    *   导入项目根目录下的 `schema.sql` 文件来创建所需的表结构。
3.  **修改配置**:
    *   打开 `api/src/main/resources/application.yml` 文件。
    *   修改 `spring.datasource` 下的数据库连接信息 (url, username, password) 以匹配您的数据库设置。
    *   (可选) 修改 `jwt.secret` 和 `jwt.expiration` 来配置 JWT。
4.  **运行项目**:
    *   在项目根目录下，执行以下 Maven 命令来构建和启动项目:
    ```bash
    mvn spring-boot:run -pl api
    ```
    *   或者，您可以直接运行 `api` 模块下的 `ApiApplication.java` 文件。

项目成功启动后，API 服务将在 `8080` 端口上可用。

## 如何修改接口

项目的接口代码主要位于 `api/src/main/java/base/ecs32/top/api/controller/` 目录下。

例如，如果您想修改用户相关的接口，您可以编辑 `UserController.java` 文件。

所有的业务逻辑都封装在 `service` 层，对应的文件位于 `api/src/main/java/base/ecs32/top/api/service/` 目录下。
