package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class LessonProgressUpdateRequest {
    private String status; // UNLOCKED, IN_PROGRESS, COMPLETED
    private Map<String, Object> progressPayload; // 进度数据，如播放时间等
}
