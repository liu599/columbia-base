package base.ecs32.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditAction {
    BAN_USER("禁用用户"),
    UNBAN_USER("解冻用户"),
    UPDATE_PRODUCT("更新产品"),
    SAVE_PRODUCT("保存产品"),
    MANUAL_ACTIVATE("手动激活产品"),
    MANUAL_RECHARGE("人工充值"),
    BATCH_CREATE_CODE("批量建码"),
    QUERY_AUDIT_LOG("查询审计日志");

    private final String description;
}
