package base.ecs32.top.api.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserActivationVO {
    private Long activationId;
    private Long productId;
    private String productName;
    private String productDescription;
    private String activationCode;
    private LocalDateTime activatedAt;
}
