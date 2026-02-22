package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.List;

@Data
public class AdminUserCourseVO {
    private Long userId;
    private String username;
    private List<CourseProgressVO> courses;
}

@Data
class CourseProgressVO {
    private Long courseId;
    private String courseTitle;
    private String accessStatus;
    private Integer progressPercent;
    private String activatedAt;
    private String validUntil;
}
