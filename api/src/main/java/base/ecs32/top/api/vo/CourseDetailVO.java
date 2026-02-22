package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.List;

@Data
public class CourseDetailVO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer progressPercent;
    private String accessStatus;
    private List<ChapterLessonsVO> chapters;
}

@Data
class ChapterLessonsVO {
    private Long id;
    private String title;
    private Integer sortOrder;
    private List<LessonSimpleVO> lessons;
}

@Data
class LessonSimpleVO {
    private Long id;
    private String title;
    private String itemType;
    private Boolean isRequired;
    private String status; // 学习状态
}
