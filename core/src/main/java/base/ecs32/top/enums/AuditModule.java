package base.ecs32.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditModule {
    USER("用户模块"),
    PRODUCT("产品模块"),
    CREDIT("积分模块"),
    ACTIVATION("激活模块"),
    AUDIT("审计模块");

    private final String description;
}
