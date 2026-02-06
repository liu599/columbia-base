package base.ecs32.top.entity;

import base.ecs32.top.enums.ActivationCodeStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_activation_code")
public class ActivationCode {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String code;

    private Long productId;

    private ActivationCodeStatus status;

    private Long userId;

    private LocalDateTime usedTime;
}
