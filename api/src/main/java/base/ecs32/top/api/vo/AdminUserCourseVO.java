package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.List;

@Data
public class AdminUserCourseVO {
    private Long userId;
    private String username;
    private List<CourseProgressVO> courses;
}
