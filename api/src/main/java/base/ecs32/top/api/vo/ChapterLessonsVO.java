package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.List;

@Data
public class ChapterLessonsVO {
    private Long id;
    private String title;
    private Integer sortOrder;
    private String lockStatus;
    private List<LessonSimpleVO> lessons;
}
