package base.ecs32.top.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private Long id;
    private String name;
    private String description;
    private Integer baseCredits;
    private Integer status;
    private Long cover;           // 封面图 file_id
    private String coverUrl;      // 封面图临时链接
}
