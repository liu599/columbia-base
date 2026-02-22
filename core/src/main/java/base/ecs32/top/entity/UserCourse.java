package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_course")
public class UserCourse {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long courseId;

    private String accessStatus;

    private Integer progressPercent;

    private LocalDateTime activatedAt;

    private LocalDateTime validUntil;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
