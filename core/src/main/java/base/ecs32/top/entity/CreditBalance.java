package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_credit_balance")
public class CreditBalance {

    private Long userId;

    private Integer availableCredits;

    private Integer frozenCredits;

    private LocalDateTime updateTime;
}
