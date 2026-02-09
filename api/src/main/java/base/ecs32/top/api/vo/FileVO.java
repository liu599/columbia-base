package base.ecs32.top.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {
    private String fileUuid;
    private String fileName;
    private String signedUrl;    // 动态生成的签名 URL
    private Long fileSize;       // 字节数
    private String contentType;  // 媒体类型
    private Object metadata;     // 业务自定义元数据
    private String bucketName;
    private String ossPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
