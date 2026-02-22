package base.ecs32.top.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserCourseAccessRequest {
    private String accessStatus; // ACTIVE, EXPIRED, SUSPENDED
    private LocalDateTime validUntil; // 有效期至
}
