package base.ecs32.top.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING(0, "待激活"),
    NORMAL(1, "正常"),
    LOCKED(2, "锁定");

    @EnumValue
    private final int code;
    private final String description;

    UserStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
