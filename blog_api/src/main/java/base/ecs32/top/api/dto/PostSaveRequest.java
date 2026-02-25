package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostSaveRequest {

    private Integer pid;

    @NotBlank(message = "文章外部ID不能为空")
    @Size(max = 50, message = "文章外部ID长度不能超过50")
    private String poid;

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者长度不能超过50")
    private String author;

    @NotBlank(message = "分类不能为空")
    @Size(max = 50, message = "分类长度不能超过50")
    private String category;

    @NotBlank(message = "文章内容不能为空")
    private String body;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 500, message = "文章标题长度不能超过500")
    private String ptitle;

    @NotBlank(message = "文章别名不能为空")
    @Size(max = 500, message = "文章别名长度不能超过500")
    private String slug;

    @Size(max = 32, message = "密码长度不能超过32")
    private String password;
}
