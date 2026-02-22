package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class AdminAssignmentGradeRequest {
    private Integer score;
    private String feedback;
    private Boolean rejected; // true: 打回重做, false: 正常批改
}
