package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.UserLoginRequest;
import base.ecs32.top.api.dto.UserRegisterRequest;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.api.util.JwtUtils;
import base.ecs32.top.api.util.PasswordUtils;
import base.ecs32.top.api.vo.UserLoginVO;
import base.ecs32.top.api.vo.UserProfileVO;
import base.ecs32.top.api.vo.UserRegisterVO;
import base.ecs32.top.dao.UserAuthMapper;
import base.ecs32.top.dao.UserMapper;
import base.ecs32.top.entity.User;
import base.ecs32.top.entity.UserAuth;
import base.ecs32.top.enums.UserStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;

    public UserServiceImpl(UserMapper userMapper, UserAuthMapper userAuthMapper) {
        this.userMapper = userMapper;
        this.userAuthMapper = userAuthMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRegisterVO register(UserRegisterRequest request) {
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())) != null) {
            throw new BusinessException(ResultCode.USER_ERROR, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.hashPassword(request.getPassword()));
        user.setStatus(UserStatus.NORMAL);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        UserRegisterVO vo = new UserRegisterVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO login(UserLoginRequest request, String ip) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getAccount())
                .or()
                .eq(User::getPhone, request.getAccount()));

        if (user == null || !PasswordUtils.checkPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_ERROR, "用户名或密码错误");
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new BusinessException(ResultCode.USER_ERROR, "账号已锁定");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        UserAuth auth = new UserAuth();
        auth.setUserId(user.getId());
        auth.setToken(token);
        auth.setLoginIp(ip);
        auth.setExpireTime(LocalDateTime.now().plusDays(7));
        userAuthMapper.insert(auth);

        return UserLoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_ERROR, "用户不存在");
        }
        return UserProfileVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}
