package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class ProductCourseVO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String coverUrl; // 封面图临时链接
}
