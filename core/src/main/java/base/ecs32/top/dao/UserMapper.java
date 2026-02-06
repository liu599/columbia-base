package base.ecs32.top.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import base.ecs32.top.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}