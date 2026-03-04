# 技术架构指南

本文档介绍项目采用的技术架构和核心类库,帮助开发者理解并构建类似架构的项目。

## 目录

- [整体架构](#整体架构)
- [核心技术栈](#核心技术栈)
- [项目结构](#项目结构)
- [核心组件](#核心组件)
- [最佳实践](#最佳实践)

## 整体架构

本项目采用**分层架构 + 多模块设计**,基于 Spring Boot 3.x 构建。

### 架构层次

```
┌─────────────────────────────────────┐
│   API Layer (Controller/Advice)    │  ← Web 层,处理 HTTP 请求
├─────────────────────────────────────┤
│   Service Layer (Business Logic)   │  ← 业务逻辑层
├─────────────────────────────────────┤
│   DAO Layer (Mapper/Repository)    │  ← 数据访问层
├─────────────────────────────────────┤
│   Database (MySQL)                 │  ← 数据存储
└─────────────────────────────────────┘
```

### 多模块设计

采用 Maven 多模块结构,实现关注点分离:

```
columbia-base (父工程)
├── core        → 数据访问层(Entity/Mapper)
├── api         → Web API 层(Controller/Service/Config)
├── blog_core   → 博客模块数据层
└── blog_api    → 博客模块 API 层
```

**优势**:
- 模块解耦,便于维护和测试
- 支持独立部署和扩展
- 代码复用性强

## 核心技术栈

### 1. Spring Boot 3.2.4

**作用**: 应用程序框架,提供自动配置和快速开发能力。

**关键特性**:
- 内嵌 Servlet 容器(Tomcat)
- 自动配置减少样板代码
- 生产就绪的 Actuator 监控

**Maven 依赖**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.4</version>
</parent>
```

**常用 Starter**:
```xml
<!-- Web 开发 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- AOP 切面编程 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- 参数验证 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- 健康监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2. Java 21

**作用**: 编程语言,使用最新的 LTS 版本。

**关键特性**:
- Record 类型(不可变数据类)
- Pattern Matching(模式匹配)
- Virtual Threads(虚拟线程,提升并发性能)

**Maven 配置**:
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

### 3. MyBatis-Plus 3.5.5

**作用**: MyBatis 增强工具,简化数据库操作。

**关键特性**:
- 无侵入式设计,只做增强不做改变
- 内置 CRUD 操作,无需编写 XML
- 支持分页插件、代码生成器
- 支持 Lambda 表达式查询

**Maven 依赖**:
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

**实体类示例**:
```java
@Data
@TableName("t_course")
public class Course {
    @TableId(type = IdType.ASSIGN_ID)  // 雪花算法生成 ID
    private Long id;

    private String title;
    private String description;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

**Mapper 接口**:
```java
public interface CourseMapper extends BaseMapper<Course> {
    // 继承 BaseMapper 即可获得 CRUD 方法
    // insert(), selectById(), selectList(), updateById(), deleteById()
}
```

**配置多数据源**:
```java
@Configuration
@MapperScan(
    basePackages = "base.ecs32.top.dao",
    sqlSessionTemplateRef = "baseSqlSessionTemplate"
)
public class MybatisConfig {

    @Bean(name = "baseDataSource")
    @Primary
    public DataSource baseDataSource(
        @Value("${spring.datasource.url}") String url,
        @Value("${spring.datasource.username}") String username,
        @Value("${spring.datasource.password}") String password
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "baseSqlSessionFactory")
    @Primary
    public SqlSessionFactory baseSqlSessionFactory(
        @Qualifier("baseDataSource") DataSource dataSource
    ) throws Exception {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        // 配置日志输出
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setLogImpl(StdOutImpl.class);
        bean.setConfiguration(configuration);

        return bean.getObject();
    }
}
```

### 4. Lombok 1.18.30

**作用**: 自动生成样板代码(Getter/Setter/Constructor 等)。

**关键特性**:
- 减少样板代码,提高可读性
- 编译期生成代码,无运行时开销
- 支持 @Data, @Builder, @Slf4j 等注解

**Maven 配置**:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

**需要在编译插件中配置**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.30</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

**常用注解**:
```java
@Data                    // 生成 Getter/Setter/toString/equals/hashCode
@Builder                 // 构建者模式
@NoArgsConstructor       // 无参构造器
@AllArgsConstructor      // 全参构造器
@Slf4j                  // 日志对象 private static final Logger log
@RequiredArgsConstructor // final 字段构造器(用于依赖注入)
```

### 5. MySQL Connector

**作用**: MySQL 数据库驱动。

**Maven 依赖**:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**配置数据源**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_name?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 6. Redis (Spring Data Redis)

**作用**: 缓存和会话存储。

**Maven 依赖**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**配置 Redis**:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0
```

**使用示例**:
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 存储数据
redisTemplate.opsForValue().set("key", "value", 7200, TimeUnit.SECONDS);

// 读取数据
String value = (String) redisTemplate.opsForValue().get("key");
```

### 7. JWT (JSON Web Token)

**作用**: 用户身份认证和授权。

**Maven 依赖**:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

**生成 Token 示例**:
```java
public String generateToken(Long userId) {
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7天
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
}
```

### 8. Aliyun OSS SDK

**作用**: 对象存储服务,用于文件上传和管理。

**Maven 依赖**:
```xml
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

**配置 OSS**:
```properties
aliyun.oss.endpoint=oss-cn-hangzhou.aliyuncs.com
aliyun.oss.accessKeyId=your_access_key_id
aliyun.oss.accessKeySecret=your_access_key_secret
aliyun.oss.bucketName=your_bucket_name
```

### 9. Jackson

**作用**: JSON 序列化/反序列化,Spring Boot 默认集成。

**自定义配置**:
```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 日期格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 时区
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return mapper;
    }
}
```

## 项目结构

### 标准分层结构

```
api/src/main/java/base/ecs32/top/api/
├── advice/        ← 全局异常处理和响应包装
├── aspect/        ← AOP 切面(审计日志、权限校验)
├── config/        ← 配置类(CORS、MyBatis、WebMvc)
├── controller/    ← 控制器层
├── dto/           ← 数据传输对象
├── interceptor/   ← 拦截器(JWT 认证)
├── service/       ← 服务层接口
│   └── impl/      ← 服务层实现
├── util/          ← 工具类
└── vo/            ← 视图对象

core/src/main/java/base/ecs32/top/
├── entity/        ← 实体类
├── dao/           ← Mapper 接口
└── enums/         ← 枚举类
```

### 职责划分

| 层次 | 职责 | 关键注解 |
|-----|------|---------|
| Controller | 接收 HTTP 请求,参数验证,调用 Service | @RestController, @RequestMapping |
| Service | 业务逻辑处理,事务管理 | @Service, @Transactional |
| Mapper | 数据库操作 | @Mapper |
| Entity | 数据库表映射 | @TableName, @TableId |
| DTO | 接收请求参数 | - |
| VO | 返回响应数据 | - |

## 核心组件

### 1. 统一响应格式

**作用**: 统一 API 响应格式,便于前端处理。

**实现方式**:
```java
// 响应包装类
@Data
public class ResultVo<T> {
    private Integer code;    // 状态码
    private String message;  // 提示信息
    private T data;          // 数据

    public static <T> ResultVo<T> success(T data) {
        ResultVo<T> result = new ResultVo<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
}

// 全局响应处理器
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                   MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResultVo) {
            return body;
        }
        return ResultVo.success(body);
    }
}
```

### 2. 全局异常处理

**作用**: 统一异常处理,避免敏感信息泄露。

**实现方式**:
```java
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResultVo<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResultVo.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultVo.error(500, "系统繁忙,请稍后重试");
    }
}
```

### 3. AOP 审计日志

**作用**: 记录管理员操作日志,用于审计追踪。

**实现方式**:

**自定义注解**:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAudit {
    Module module();
    Action action();
}
```

**切面处理**:
```java
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuditAspect {

    private final AuditLogMapper auditLogMapper;

    @Around("@annotation(adminAudit)")
    public Object around(ProceedingJoinPoint joinPoint, AdminAudit adminAudit) throws Throwable {
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            saveLog(joinPoint, adminAudit, result, throwable);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, AdminAudit adminAudit,
                        Object result, Throwable throwable) {
        // 获取请求信息
        HttpServletRequest request = getCurrentRequest();
        Long adminId = (Long) request.getAttribute("userId");

        // 构建审计日志
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setModule(adminAudit.module().name());
        auditLog.setAction(adminAudit.action().name());
        auditLog.setIpAddress(getIpAddr(request));
        auditLog.setCreateTime(LocalDateTime.now());

        // 保存日志
        auditLogMapper.insert(auditLog);
    }
}
```

**使用示例**:
```java
@PostMapping("/courses")
@AdminAudit(module = Module.COURSE, action = Action.CREATE)
public ResultVo<Long> createCourse(@RequestBody @Valid CourseDTO dto) {
    Long courseId = courseService.createCourse(dto);
    return ResultVo.success(courseId);
}
```

### 4. 拦截器认证

**作用**: JWT Token 验证,保护需要登录的接口。

**实现方式**:

**拦截器**:
```java
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                            Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录");
        }

        token = token.substring(7);
        Long userId = jwtUtil.validateToken(token);

        request.setAttribute("userId", userId);
        return true;
    }
}
```

**注册拦截器**:
```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/user/**")
                .excludePathPatterns("/auth/**");
    }
}
```

### 5. CORS 跨域配置

**作用**: 允许前端跨域访问 API。

**实现方式**:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

## 最佳实践

### 1. 依赖注入

**推荐方式**: 使用**构造器注入** + Lombok

```java
@Service
@RequiredArgsConstructor  // Lombok 生成构造器
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;      // final 字段
    private final ProductService productService;  // 自动注入

    // 无需 @Autowired,代码更简洁
}
```

**优势**:
- 不可变对象,线程安全
- 依赖关系明确
- 易于单元测试(Mock)

### 2. 参数验证

**使用 Bean Validation**:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

**DTO 示例**:
```java
@Data
public class CourseDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    private String title;

    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @Min(value = 0, message = "价格不能为负数")
    private BigDecimal price;
}
```

**Controller 使用**:
```java
@PostMapping
public ResultVo<Long> createCourse(@RequestBody @Valid CourseDTO dto) {
    // @Valid 触发验证,失败会抛出 MethodArgumentNotValidException
    Long courseId = courseService.createCourse(dto);
    return ResultVo.success(courseId);
}
```

### 3. 异常处理

**自定义业务异常**:
```java
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

**全局异常处理**:
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResultVo<?> handleValidationException(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));
    return ResultVo.error(400, message);
}
```

### 4. 日志规范

**使用 Slf4j**:
```java
@Service
@Slf4j  // Lombok 自动生成 log 对象
public class OrderServiceImpl implements OrderService {

    public void createOrder(OrderDTO dto) {
        log.info("创建订单: userId={}, productId={}", dto.getUserId(), dto.getProductId());

        try {
            // 业务逻辑
        } catch (Exception e) {
            log.error("创建订单失败: userId={}", dto.getUserId(), e);
            throw new BusinessException(500, "创建订单失败");
        }
    }
}
```

**日志级别**:
- ERROR: 错误信息,需要立即处理
- WARN: 警告信息,可能存在问题
- INFO: 关键流程信息(订单创建、支付成功)
- DEBUG: 调试信息(仅开发环境)

### 5. 数据库设计

**主键策略**: 使用雪花算法(分布式唯一 ID)

```java
@TableId(type = IdType.ASSIGN_ID)
private Long id;
```

**时间字段**: 自动填充

```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;
```

**逻辑删除**: 软删除,数据可恢复

```java
@TableLogic
private Integer deleted;  // 0-未删除, 1-已删除
```

### 6. 事务管理

**声明式事务**:
```java
@Service
public class OrderServiceImpl implements OrderService {

    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderDTO dto) {
        // 创建订单
        Order order = new Order();
        orderMapper.insert(order);

        // 扣减库存
        productService.reduceStock(dto.getProductId(), dto.getQuantity());

        // 扣减余额
        userService.reduceBalance(dto.getUserId(), dto.getTotalAmount());
    }
}
```

**注意事项**:
- 只在 public 方法上使用 @Transactional
- 避免在事务中调用远程服务
- 事务方法不能被同类方法调用(绕过代理)

## 快速开始

### 1. 环境准备

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 2. 创建项目

**pom.xml 配置**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.4</version>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.5</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### 3. 创建启动类

```java
@SpringBootApplication
@MapperScan("your.package.dao")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 配置文件

**application.properties**:
```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=password

spring.data.redis.host=localhost
spring.data.redis.port=6379

logging.level.your.package=DEBUG
```

### 5. 分层开发

**Entity**:
```java
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createTime;
}
```

**Mapper**:
```java
public interface UserMapper extends BaseMapper<User> {
}
```

**Service**:
```java
public interface UserService {
    User getById(Long id);
    Long create(User user);
}

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Long create(User user) {
        userMapper.insert(user);
        return user.getId();
    }
}
```

**Controller**:
```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResultVo<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResultVo.success(user);
    }

    @PostMapping
    public ResultVo<Long> create(@RequestBody @Valid UserDTO dto) {
        Long userId = userService.create(dto);
        return ResultVo.success(userId);
    }
}
```

## 参考资料

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [Lombok 官方文档](https://projectlombok.org/)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [JWT.io](https://jwt.io/)

## 总结

本架构采用 **Spring Boot + MyBatis-Plus + MySQL + Redis** 的经典技术栈,通过分层设计和多模块结构,实现了:

- ✅ 关注点分离,代码职责清晰
- ✅ 统一响应格式和异常处理
- ✅ 声明式审计日志(AOP)
- ✅ JWT 认证和权限控制
- ✅ 文件上传(阿里云 OSS)
- ✅ 缓存支持(Redis)

适合中大型项目的快速开发和迭代。
