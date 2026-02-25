package base.ecs32.top.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("post")
public class Post {

    @TableId(value = "pid", type = IdType.AUTO)
    private Integer pid;

    private String poid;

    private String author;

    private String category;

    private String body;

    private String ptitle;

    private String slug;

    private String password;

    @TableField("createdAt")
    private Long createdAt;

    @TableField("modifiedAt")
    private Long modifiedAt;
}
