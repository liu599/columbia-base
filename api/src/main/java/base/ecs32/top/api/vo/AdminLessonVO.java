package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.Map;

@Data
public class AdminLessonVO {
    private Long id;
    private Long chapterId;
    private String title;
    private String itemType;
    private Boolean isRequired;
    private Integer sortOrder;
    private Map<String, Object> contentPayload;
}
