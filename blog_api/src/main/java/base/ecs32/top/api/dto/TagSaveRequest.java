package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagSaveRequest {

    private Integer id;

    @NotBlank(message = "标签外部ID不能为空")
    @Size(max = 50, message = "标签外部ID长度不能超过50")
    private String tagId;

    @NotBlank(message = "标签链接不能为空")
    @Size(max = 120, message = "标签链接长度不能超过120")
    private String tagLink;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 120, message = "标签名称长度不能超过120")
    private String tagName;
}
