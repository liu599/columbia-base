package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_file")
public class File {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String fileUuid;

    private String fileMd5;

    private Long userId;

    private String bucketName;

    private String ossPath;

    private Long fileSize;

    private String contentType;

    private String metadata;

    private Integer isDeleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
