package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_course")
public class Course {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private String description;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
