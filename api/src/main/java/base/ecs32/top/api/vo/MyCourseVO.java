package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class MyCourseVO {
    private Long id;
    private String title;
    private String description;
    private Integer progressPercent;
    private String accessStatus;
    private String activatedAt;
    private String validUntil;
}
