package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AdminLessonSaveRequest {
    private Long id; // 更新时传入
    private Long chapterId;
    private String title;
    private String itemType; // VIDEO, DOCUMENT, PODCAST, ASSIGNMENT, INTERACTIVE
    private Boolean isRequired;
    private Integer sortOrder;
    private Map<String, Object> contentPayload; // 内容数据 JSON
}
