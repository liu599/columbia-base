package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbFileUpdateStatusRequest {
    private Long kbId;
    private Long userId;
    private Long fileId;
    private String status; // parsing, success, error, uploaded
    private String parserConfig; // optional: update parser config
}
