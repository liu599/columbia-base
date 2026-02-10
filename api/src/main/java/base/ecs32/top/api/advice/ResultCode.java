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
    SERVER_ERROR(50000, "服务器错误");

    private final int code;
    private final String message;
}
