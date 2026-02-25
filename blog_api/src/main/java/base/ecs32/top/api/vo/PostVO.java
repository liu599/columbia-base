package base.ecs32.top.api.vo;

import lombok.Data;

@Data
public class PostVO {

    private Integer pid;

    private String poid;

    private String author;

    private String category;

    private String body;

    private String ptitle;

    private String slug;

    private String password;

    private Long createdAt;

    private Long modifiedAt;
}
