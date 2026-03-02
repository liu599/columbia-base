package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库-文件关联表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_kb_file")
public class KbFile {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long kbId;

    private Long fileId;

    private String status; // parsing, success, error, uploaded

    private String parserConfig; // JSON格式配置

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
