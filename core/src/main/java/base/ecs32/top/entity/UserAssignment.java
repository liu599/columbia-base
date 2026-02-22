package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_assignment")
public class UserAssignment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long lessonId;

    private String status;

    private String submissionPayload;

    private Integer score;

    private String feedback;

    private LocalDateTime submittedAt;

    private LocalDateTime gradedAt;

    private Long graderId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
