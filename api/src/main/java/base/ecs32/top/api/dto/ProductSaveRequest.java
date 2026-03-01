package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class ProductSaveRequest {
    private Long id;
    private String name;
    private String description;
    private Integer baseCredits;
    private Long cover; // 封面图 file_id
    private Integer status; // 1: 启用, 0: 下架/隐藏
}
