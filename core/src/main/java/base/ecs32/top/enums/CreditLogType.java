package base.ecs32.top.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum CreditLogType {
    RECHARGE("RECHARGE", "充值"),
    ACTIVATE("ACTIVATE", "激活奖励"),
    CONSUME("CONSUME", "消费"),
    FREEZE("FREEZE", "冻结"),
    UNFREEZE("UNFREEZE", "解冻");

    @EnumValue
    private final String code;
    private final String description;

    CreditLogType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
