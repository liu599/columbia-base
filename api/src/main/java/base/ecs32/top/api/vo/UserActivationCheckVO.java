package base.ecs32.top.api.vo;

import base.ecs32.top.enums.UserRole;
import lombok.Data;

@Data
public class UserActivationCheckVO {
    private Long userId;
    private Long productId;
    private Boolean isActivated;
    private Long activationId;
    private String productName;
    private Integer roleLevel;
    private String roleDescription;
    private String avatarFileId;
}
