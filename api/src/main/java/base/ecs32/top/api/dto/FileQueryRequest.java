package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class FileQueryRequest {
    private Long userId;       // 当前操作用户
    private String fileUuid;    // 常用标识
    private String ossPath;     // 备选标识
    private String newMetadata; // 仅在接口 3 更新时使用
}
