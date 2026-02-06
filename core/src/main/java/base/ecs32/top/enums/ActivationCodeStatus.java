package base.ecs32.top.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ActivationCodeStatus {
    UNUSED(0, "未使用"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期");

    @EnumValue
    private final int code;
    private final String description;

    ActivationCodeStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
