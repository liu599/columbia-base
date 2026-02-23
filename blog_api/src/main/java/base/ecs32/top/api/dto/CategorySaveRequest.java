package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategorySaveRequest {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100")
    private String name;

    @NotBlank(message = "分类别名不能为空")
    @Size(max = 100, message = "分类别名长度不能超过100")
    private String slug;

    @Size(max = 255, message = "分类描述长度不能超过255")
    private String description;

    private Integer sortOrder;

    private String status;
}
