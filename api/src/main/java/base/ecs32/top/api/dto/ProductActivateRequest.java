package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class ProductActivateRequest {
    private Long targetUserId;
    private Long productId;
    private String remark;
}
