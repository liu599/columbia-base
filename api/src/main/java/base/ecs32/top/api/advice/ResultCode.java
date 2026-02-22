package base.ecs32.top.api.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(20000, "成功"),
    USER_ERROR(40001, "参数错误"),
    FILE_SIZE_EXCEEDED(41301, "文件大小超过限制"),
    FILE_NOT_FOUND(40401, "文件不存在"),
    COURSE_NOT_FOUND(40402, "课程不存在"),
    COURSE_NOT_ACTIVATED(40301, "课程未激活或已过期"),
    LESSON_NOT_FOUND(40403, "课时不存在"),
    LESSON_NOT_ACCESSIBLE(40302, "课时不可访问"),
    ASSIGNMENT_NOT_FOUND(40404, "作业不存在"),
    INVALID_ACTIVATION_CODE(40002, "无效的激活码"),
    SERVER_ERROR(50000, "服务器错误");

    private final int code;
    private final String message;
}
