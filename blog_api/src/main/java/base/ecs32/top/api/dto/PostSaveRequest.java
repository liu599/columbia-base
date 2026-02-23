package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostSaveRequest {

    private Long id;

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 255, message = "文章标题长度不能超过255")
    private String title;

    @NotBlank(message = "文章别名不能为空")
    @Size(max = 255, message = "文章别名长度不能超过255")
    private String slug;

    @Size(max = 500, message = "文章摘要长度不能超过500")
    private String excerpt;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String contentHtml;

    private String excerptText;

    private String contentText;

    private String coverImage;

    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    private Long categoryId;

    private String status;

    private Boolean isFeatured;

    private List<Long> tagIds;
}
