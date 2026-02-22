package base.ecs32.top.api.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一错误码定义
 *
 * 错误码格式: XYZZZ
 * X: 错误类型 (4-客户端错误, 5-服务器错误)
 * Y: 业务模块分类
 * ZZZ: 具体错误编号
 *
 * 客户端错误(4xxxx):
 * - 41000-41999: 用户相关错误
 * - 42000-42999: 文件相关错误
 * - 43000-43999: 课程相关错误
 * - 44000-44999: 激活码/产品相关错误
 * - 45000-45999: 微信认证相关错误
 * - 46000-46999: 积分/信用相关错误
 * - 49000-49999: 通用客户端错误
 *
 * 服务器错误(5xxxx):
 * - 51000-51999: 数据库相关错误
 * - 52000-52999: 外部服务错误(微信API、OSS等)
 * - 53000-53999: 系统资源错误
 * - 55000-55999: 业务逻辑错误
 * - 59000-59999: 未分类服务器错误
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ==================== 成功响应 ====================
    SUCCESS(20000, "成功"),

    // ==================== 客户端错误 (4xxxx) ====================

    // ----- 用户相关错误 (41000-41999) -----
    USER_NOT_FOUND(41001, "用户不存在"),
    USER_ALREADY_EXISTS(41002, "用户已存在"),
    USERNAME_ALREADY_EXISTS(41003, "用户名已存在"),
    PHONE_ALREADY_EXISTS(41004, "手机号已存在"),
    USERNAME_OR_PASSWORD_ERROR(41005, "用户名或密码错误"),
    ACCOUNT_LOCKED(41006, "账号已锁定"),
    PASSWORD_NOT_VALID(41007, "密码强度不合格：必须包含1个大写字母、1个小写字母、1个特殊字符，且长度大于8位"),
    PHONE_FORMAT_NOT_VALID(41008, "手机号格式不正确"),
    PERMISSION_DENIED(41009, "无权限访问此接口"),
    USER_NOT_LOGGED_IN(41010, "用户未登录"),

    // ----- 文件相关错误 (42000-42999) -----
    FILE_NOT_FOUND(42001, "文件不存在"),
    FILE_IDENTIFIER_EMPTY(42002, "文件标识不能为空"),
    FILE_UUID_EMPTY(42003, "文件 UUID 不能为空"),
    FILE_SIZE_EXCEEDED(42004, "文件大小超过限制"),
    FILE_UPLOAD_FAILED(42005, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(42006, "不支持的文件类型"),

    // ----- 课程相关错误 (43000-43999) -----
    COURSE_NOT_FOUND(43001, "课程不存在"),
    COURSE_NOT_PUBLISHED(43002, "课程未发布"),
    COURSE_NOT_ACTIVATED(43003, "课程未激活或已过期"),
    COURSE_ALREADY_ACTIVATED(43004, "课程已激活"),
    COURSE_ACTIVATION_RECORD_NOT_FOUND(43005, "课程激活记录不存在"),
    LESSON_NOT_FOUND(43006, "课时不存在"),
    LESSON_NOT_ACCESSIBLE(43007, "课时不可访问，请先完成上一课时"),
    LESSON_TYPE_INVALID(43008, "无效的课时类型"),
    LESSON_IS_ASSIGNMENT_TYPE(43009, "作业类型课时请使用提交作业接口"),
    LESSON_NOT_ASSIGNMENT_TYPE(43010, "该课时不是作业类型"),
    ASSIGNMENT_NOT_FOUND(43011, "作业不存在"),
    CHAPTER_NOT_FOUND(43012, "章节不存在"),
    LESSON_CONTENT_EMPTY(43013, "内容数据不能为空"),
    LESSON_CONTENT_MISSING_REQUIRED_FIELD(43014, "课时内容缺少必需字段"),

    // ----- 激活码/产品相关错误 (44000-44999) -----
    ACTIVATION_CODE_INVALID(44001, "无效的激活码"),
    ACTIVATION_CODE_NOT_FOUND(44002, "激活码不存在"),
    ACTIVATION_CODE_ALREADY_USED(44003, "激活码已失效"),
    PRODUCT_NOT_FOUND(44004, "产品不存在"),
    PRODUCT_NOT_ASSOCIATED(44005, "关联产品不存在"),
    USER_ALREADY_ACTIVATED_PRODUCT(44006, "该产品您已激活，无法重复激活"),
    USER_NOT_ACTIVATED_PRODUCT(44007, "用户未激活该产品"),

    // ----- 微信认证相关错误 (45000-45999) -----
    WECHAT_QRCODE_NOT_FOUND(45001, "二维码不存在"),
    WECHAT_QRCODE_EXPIRED(45002, "二维码已过期"),
    WECHAT_QRCODE_NO_PERMISSION(45003, "无权限访问二维码"),
    WECHAT_LOGIN_FAILED(45004, "微信登录失败"),
    WECHAT_LOGIN_CODE_INVALID(45005, "微信登录码无效"),
    WECHAT_ACCESS_TOKEN_FAILED(45006, "获取微信access_token失败"),
    WECHAT_WXACODE_FAILED(45007, "获取小程序码失败"),
    WECHAT_PARSE_RESPONSE_FAILED(45008, "解析微信响应失败"),

    // ----- 积分/信用相关错误 (46000-46999) -----
    CREDIT_INSUFFICIENT(46001, "积分不足"),
    CREDIT_BALANCE_NOT_FOUND(46002, "积分余额不存在"),

    // ----- 通用客户端错误 (49000-49999) -----
    INVALID_PARAMETER(49001, "参数错误"),
    MISSING_REQUIRED_PARAMETER(49002, "缺少必需参数 {}"),
    MISSING_REQUEST_PART(49003, "缺少必需的文件参数 {}"),
    INVALID_REQUEST_BODY(49004, "请求体格式错误或为空"),
    METHOD_NOT_SUPPORTED(49005, "不支持的请求方法 {}"),
    VALIDATION_ERROR(49006, "数据验证失败"),
    TEMP_TOKEN_INVALID(49007, "临时令牌无效或已过期"),

    // ==================== 服务器错误 (5xxxx) ====================

    // ----- 数据库相关错误 (51000-51999) -----
    DATABASE_ERROR(51001, "数据库操作失败"),
    DATABASE_CONNECTION_ERROR(51002, "数据库连接失败"),
    DATA_INTEGRITY_ERROR(51003, "数据完整性错误"),
    DATA_NOT_FOUND(51004, "数据不存在"),

    // ----- 外部服务错误 (52000-52999) -----
    WECHAT_API_ERROR(52001, "微信API调用失败"),
    OSS_UPLOAD_ERROR(52002, "文件上传到OSS失败"),
    OSS_GENERATE_URL_ERROR(52003, "生成OSS签名URL失败"),
    EXTERNAL_SERVICE_TIMEOUT(52004, "外部服务调用超时"),
    EXTERNAL_SERVICE_UNAVAILABLE(52005, "外部服务不可用"),

    // ----- 系统资源错误 (53000-53999) -----
    SYSTEM_ERROR(53001, "系统错误"),
    INSUFFICIENT_DISK_SPACE(53002, "磁盘空间不足"),
    MEMORY_ERROR(53003, "内存不足"),
    THREAD_POOL_ERROR(53004, "线程池错误"),

    // ----- 业务逻辑错误 (55000-55999) -----
    BUSINESS_LOGIC_ERROR(55001, "业务逻辑错误"),
    CONCURRENT_MODIFICATION_ERROR(55002, "数据并发修改冲突"),
    OPERATION_NOT_ALLOWED(55003, "当前状态不允许此操作"),

    // ----- 未分类服务器错误 (59000-59999) -----
    SERVER_ERROR(59001, "服务器内部错误"),
    UNKNOWN_ERROR(59002, "未知错误");

    private final int code;
    private final String message;
}
