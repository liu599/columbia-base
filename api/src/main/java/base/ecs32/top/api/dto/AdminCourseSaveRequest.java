package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class AdminCourseSaveRequest {
    private Long id; // 更新时传入
    private String title;
    private String description;
    private String status; // DRAFT, PUBLISHED, OFFLINE
    private Long productId; // 关联的产品ID
}
