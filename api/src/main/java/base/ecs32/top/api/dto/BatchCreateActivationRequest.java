package base.ecs32.top.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BatchCreateActivationRequest {
    private Long productId;
    private Integer count;
    private String codePrefix;
    private LocalDateTime expireTime;
}
