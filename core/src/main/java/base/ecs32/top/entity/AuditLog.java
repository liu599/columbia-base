package base.ecs32.top.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_audit_log")
public class AuditLog {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long adminId;

    private String module;

    private String action;

    private String targetId;

    private String beforeValue;

    private String afterValue;

    private String remark;

    private String ipAddress;

    private LocalDateTime createTime;
}
