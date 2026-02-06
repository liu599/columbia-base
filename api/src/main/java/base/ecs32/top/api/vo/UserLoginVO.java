package base.ecs32.top.api.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserLoginVO {
    private String userId;
    private String token;
    private LocalDateTime expireTime;
    private String username;
}
