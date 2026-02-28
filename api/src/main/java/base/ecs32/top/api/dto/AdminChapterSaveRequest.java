package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class AdminChapterSaveRequest {
    private Long id; // 更新时传入
    private Long courseId;
    private String title;
    private Integer sortOrder;
    private String lockStatus; // LOCK or UNLOCK
}
