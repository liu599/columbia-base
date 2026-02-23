package base.ecs32.top.api.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {

    private Long id;

    private String title;

    private String slug;

    private String excerpt;

    private String content;

    private String contentHtml;

    private String excerptText;

    private String contentText;

    private String coverImage;

    private Long authorId;

    private String authorName;

    private Long categoryId;

    private String categoryName;

    private List<TagVO> tags;

    private String status;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isFeatured;

    private LocalDateTime publishedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
