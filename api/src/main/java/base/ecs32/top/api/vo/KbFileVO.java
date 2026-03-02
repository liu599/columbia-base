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
public class KbFileVO {
    private Long id;
    private Long kbId;
    private Long fileId;
    private String status;
    private String parserConfig;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // File related fields (joined from t_file)
    private String fileUuid;
    private String fileName;
    private Long fileSize;
    private String contentType;
}
