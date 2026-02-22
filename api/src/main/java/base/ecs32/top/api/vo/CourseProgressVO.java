package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class CourseProgressVO {
    private Long courseId;
    private String courseTitle;
    private String accessStatus;
    private Integer progressPercent;
    private String activatedAt;
    private String validUntil;
}
