package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class UserActivationCheckRequest {
    private Long targetUserId;
    private Long productId;
}
