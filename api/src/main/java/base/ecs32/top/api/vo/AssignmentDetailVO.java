package base.ecs32.top.api.vo;

import lombok.Data;
import java.util.Map;

@Data
public class AssignmentDetailVO {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private String status;
    private Map<String, Object> submissionPayload;
    private Integer score;
    private String feedback;
    private String submittedAt;
    private String gradedAt;
}
