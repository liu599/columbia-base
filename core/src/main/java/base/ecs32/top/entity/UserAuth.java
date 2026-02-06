package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_auth")
public class UserAuth {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String token;

    private String loginIp;

    private LocalDateTime expireTime;
}
