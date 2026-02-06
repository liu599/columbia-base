package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class UserStatusRequest {
    private Long targetUserId;
    private Integer status; // 0: 禁用, 1: 正常
    private String reason;
}
