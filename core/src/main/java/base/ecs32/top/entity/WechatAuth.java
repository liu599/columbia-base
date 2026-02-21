package base.ecs32.top.entity;

import base.ecs32.top.enums.WechatAuthStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_wechat_auth")
public class WechatAuth {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String scene;

    private String openid;

    private WechatAuthStatus status;

    private LocalDateTime expiredAt;

    private LocalDateTime createTime;
}
