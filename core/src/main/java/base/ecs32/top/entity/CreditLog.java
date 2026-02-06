package base.ecs32.top.entity;

import base.ecs32.top.enums.CreditLogType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_credit_log")
public class CreditLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private CreditLogType type;

    private Integer amount;

    private String taskId;

    private String description;

    private LocalDateTime createTime;
}
