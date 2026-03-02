package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbUpdateRequest {
    private Long kbId;
    private Long userId;
    private String name;
    private String description;
}
