package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.Map;

@Data
public class LessonDetailVO {
    private Long id;
    private Long chapterId;
    private String chapterTitle;
    private String title;
    private String itemType;
    private Boolean isRequired;
    private Map<String, Object> contentPayload; // 内容数据（防盗链URL等）
    private Map<String, Object> securityConfig; // 安全配置（水印、防复制等）
    private String status; // 学习状态
    private Map<String, Object> progressPayload; // 进度数据
}
