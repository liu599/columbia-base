package base.ecs32.top.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class Category {

    @TableId(value = "cid", type = IdType.AUTO)
    private Integer cid;

    private String id;

    @TableField("cname")
    private String cname;

    @TableField("clink")
    private String clink;

    @TableField("cinfo")
    private String cinfo;
}
