package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbFileRemoveRequest {
    private Long kbId;
    private Long userId;
    private Long fileId;
}
