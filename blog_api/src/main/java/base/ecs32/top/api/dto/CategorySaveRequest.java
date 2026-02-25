package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategorySaveRequest {

    private Integer cid;

    @NotBlank(message = "分类ID不能为空")
    @Size(max = 50, message = "分类ID长度不能超过50")
    private String id;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50")
    private String cname;

    @NotBlank(message = "分类链接不能为空")
    @Size(max = 40, message = "分类链接长度不能超过40")
    private String clink;

    @Size(max = 32, message = "分类描述长度不能超过32")
    private String cinfo;
}
