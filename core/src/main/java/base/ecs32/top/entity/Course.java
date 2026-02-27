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

    private Long productId; // 关联的产品 ID

    private Long cover; // 封面图 file_id

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
