package base.ecs32.top.api.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TagVO {

    private Integer id;

    private String tagId;

    private String tagLink;

    private String tagName;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private LocalDate deletedAt;
}
