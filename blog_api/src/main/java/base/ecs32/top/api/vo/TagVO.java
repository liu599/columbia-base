package base.ecs32.top.api.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {

    private Long id;

    private String name;

    private String slug;

    private String description;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
