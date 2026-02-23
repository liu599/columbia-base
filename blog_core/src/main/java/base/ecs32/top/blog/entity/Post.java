package base.ecs32.top.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_blog_post")
public class Post {

    @TableId(type = IdType.ASSIGN_ID)
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

    private Long categoryId;

    private String status;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isFeatured;

    private LocalDateTime publishedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
