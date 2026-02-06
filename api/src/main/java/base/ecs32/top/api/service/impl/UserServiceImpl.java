package base.ecs32.top.api.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAuthMapper userAuthMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserAuthMapper userAuthMapper) {
        this.userMapper = userMapper;
        this.userAuthMapper = userAuthMapper;
    }

    @Override
    @Transactional
    public UserRegisterVO register(UserRegisterRequest request) {
        // Password strength validation
        if (!PasswordUtils.isStrongPassword(request.getPassword())) {
            throw new RuntimeException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one special character.");
        }

        // Check if user already exists
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtils.hashPassword(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(UserStatus.NORMAL);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);

        return new UserRegisterVO(user.getId().toString());
    }

    @Override
    public UserLoginVO login(UserLoginRequest request) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getAccount())
                .or()
                .eq(User::getPhone, request.getAccount());
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !user.getPassword().equals(PasswordUtils.hashPassword(request.getPassword()))) {
            throw new RuntimeException("Invalid account or password");
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new RuntimeException("Account is locked");
        }

        String token = JwtUtils.generateToken(user.getId(), user.getUsername());
        Date expirationDate = JwtUtils.extractExpiration(token);
        LocalDateTime expireTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());

        // Save or update user auth
        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(user.getId());
        userAuth.setToken(token);
        userAuth.setLoginIp(request.getLoginIp());
        userAuth.setExpireTime(expireTime);
        userAuthMapper.insert(userAuth);

        return UserLoginVO.builder()
                .userId(user.getId().toString())
                .token(token)
                .expireTime(expireTime)
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserProfileVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return UserProfileVO.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .phone(maskPhone(user.getPhone()))
                .wechatOpenid(user.getWechatOpenid())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }

    @Override
    public void logout(String token) {
        LambdaQueryWrapper<UserAuth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuth::getToken, token);
        userAuthMapper.delete(queryWrapper);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
