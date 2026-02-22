package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class LessonSimpleVO {
    private Long id;
    private String title;
    private String itemType;
    private Boolean isRequired;
    private String status;
}
