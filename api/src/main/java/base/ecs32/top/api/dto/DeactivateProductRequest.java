package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class DeactivateProductRequest {
    private Long targetUserId;
    private Long productId;
    private String remark;
}
