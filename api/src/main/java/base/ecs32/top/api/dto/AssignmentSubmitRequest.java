package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AssignmentSubmitRequest {
    private Map<String, Object> submissionPayload; // 作业提交内容 JSON
}
