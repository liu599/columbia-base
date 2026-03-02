package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbDeleteRequest {
    private Long kbId;
    private Long userId;
}
