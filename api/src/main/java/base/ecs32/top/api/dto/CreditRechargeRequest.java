package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class CreditRechargeRequest {
    private Long targetUserId;
    private Integer amount; // 正数为充值，负数为扣除
    private String description;
}
