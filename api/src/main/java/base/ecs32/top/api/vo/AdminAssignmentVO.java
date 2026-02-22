package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class AdminAssignmentVO {
    private Long id;
    private Long userId;
    private String username;
    private Long lessonId;
    private String lessonTitle;
    private Long courseId;
    private String courseTitle;
    private String status;
    private Integer score;
    private String submittedAt;
}
