package base.ecs32.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 * 权限等级:
 * 1 - 游客
 * 3 - 正式用户
 * 5 - 付费用户
 * 7 - 内部管理用户
 * 10 - 系统管理员
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    GUEST(1, "游客"),
    REGULAR_USER(3, "正式用户"),
    PAID_USER(5, "付费用户"),
    INTERNAL_ADMIN(7, "内部管理用户"),
    SYSTEM_ADMIN(10, "系统管理员");

    private final Integer level;
    private final String description;

    /**
     * 根据等级获取角色
     */
    public static UserRole fromLevel(Integer level) {
        if (level == null) {
            return GUEST;
        }
        for (UserRole role : values()) {
            if (role.level.equals(level)) {
                return role;
            }
        }
        return GUEST;
    }

    /**
     * 判断角色等级是否大于等于指定等级
     */
    public boolean hasPermission(Integer requiredLevel) {
        return this.level != null && this.level >= requiredLevel;
    }
}
