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
