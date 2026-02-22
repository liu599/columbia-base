package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user_lesson_progress")
public class UserLessonProgress {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long lessonId;

    private String status;

    private String progressPayload;

    private LocalDateTime lastAccessedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
