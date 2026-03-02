package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbCreateRequest {
    private Long userId;
    private String name;
    private String description;
}
