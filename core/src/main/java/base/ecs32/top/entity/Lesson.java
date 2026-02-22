package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_lesson")
public class Lesson {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long chapterId;

    private String title;

    private String itemType;

    private Boolean isRequired;

    private Integer sortOrder;

    private String contentPayload;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
