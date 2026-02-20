package base.ecs32.top.api.vo;

import base.ecs32.top.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserListVO {
    private Long id;
    private String username;
    private String phone;
    private String wechatOpenid;
    private UserStatus status;
    private Integer roleLevel;
    private String avatarFileId;
    private LocalDateTime createTime;
}
