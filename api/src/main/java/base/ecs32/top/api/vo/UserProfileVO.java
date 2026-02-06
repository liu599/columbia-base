package base.ecs32.top.api.vo;

import base.ecs32.top.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileVO {
    private String id;
    private String username;
    private String phone;
    private String wechatOpenid;
    private UserStatus status;
    private LocalDateTime createTime;
}
