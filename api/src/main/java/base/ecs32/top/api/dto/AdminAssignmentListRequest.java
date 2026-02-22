package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class AdminAssignmentListRequest {
    private Integer current = 1;
    private Integer pageSize = 20;
    private Long courseId;
    private Long lessonId;
    private String status; // PENDING, SUBMITTED, GRADED, REJECTED
}
