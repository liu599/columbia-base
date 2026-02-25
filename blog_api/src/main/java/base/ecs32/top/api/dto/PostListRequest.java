package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class PostListRequest {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String category;

    private String keyword;
}
