package base.ecs32.top.api.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreditBalanceVO {
    private Integer availableCredits;
    private Integer frozenCredits;
    private LocalDateTime updateTime;
}
