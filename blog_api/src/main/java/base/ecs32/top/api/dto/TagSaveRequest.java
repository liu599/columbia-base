package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagSaveRequest {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50")
    private String name;

    @NotBlank(message = "标签别名不能为空")
    @Size(max = 50, message = "标签别名长度不能超过50")
    private String slug;

    @Size(max = 255, message = "标签描述长度不能超过255")
    private String description;

    private String status;
}
