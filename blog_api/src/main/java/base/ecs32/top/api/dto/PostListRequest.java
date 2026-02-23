package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class PostListRequest {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String status;

    private Long categoryId;

    private Long tagId;

    private String keyword;
}
