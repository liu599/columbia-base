package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class KbFileAddRequest {
    private Long kbId;
    private Long userId;
    private Long fileId;
    private String parserConfig; // JSON格式的解析器配置
}
