package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.dto.UserLoginRequest;
import base.ecs32.top.api.dto.UserRegisterRequest;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.api.util.JwtUtils;
import base.ecs32.top.api.util.OssUtils;
import base.ecs32.top.api.util.PasswordUtils;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.api.vo.UserListVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import base.ecs32.top.api.vo.UserLoginVO;
import base.ecs32.top.api.vo.UserProfileVO;
import base.ecs32.top.api.vo.UserRegisterVO;
import base.ecs32.top.dao.FileMapper;
import base.ecs32.top.dao.UserAuthMapper;
import base.ecs32.top.dao.UserMapper;
import base.ecs32.top.entity.File;
import base.ecs32.top.entity.User;
import base.ecs32.top.entity.UserAuth;
import base.ecs32.top.enums.UserRole;
import base.ecs32.top.enums.UserStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  private final UserMapper userMapper;
  private final UserAuthMapper userAuthMapper;
  private final FileMapper fileMapper;
  private final OssUtils ossUtils;

  public UserServiceImpl(UserMapper userMapper, UserAuthMapper userAuthMapper, FileMapper fileMapper, OssUtils ossUtils) {
    this.userMapper = userMapper;
    this.userAuthMapper = userAuthMapper;
    this.fileMapper = fileMapper;
    this.ossUtils = ossUtils;
  }

  private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
  private static final long DEFAULT_EXPIRATION = 3600; // 1 hour

  /**
   * 默认注册用户角色等级：正式用户 (3)
   */
  private static final Integer DEFAULT_ROLE_LEVEL = UserRole.REGULAR_USER.getLevel();

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserRegisterVO register(UserRegisterRequest request) {
    // Validate phone number format
    if (!PHONE_PATTERN.matcher(request.getPhone()).matches()) {
      throw new BusinessException(ResultCode.PHONE_FORMAT_NOT_VALID, "手机号格式不正确");
    }

    // Extract wechatOpenid from temporary token if present
    String wechatOpenid = extractWechatOpenidFromToken(request.getTempToken());

    // Validate password strength
    if (!PasswordUtils.isStrongPassword(request.getPassword())) {
      throw new BusinessException(ResultCode.PASSWORD_NOT_VALID, "密码强度不合格：必须包含 1 个大写字母、1 个小写字母、1 个特殊字符，且长度大于 8 位");
    }

    // Check if username already exists
    if (userMapper.selectOne(new LambdaQueryWrapper<User().eq(User::getUsername, request.getUsername())) != null) {
      throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS, "用户名已存在");
    }

    // Check if phone number already exists
    if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, request.getPhone())) != null) {
      throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS, "手机号已存在");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(PasswordUtils.hashPassword(request.getPassword()));
    user.setPhone(request.getPhone());
    user.setWechatOpenid(wechatOpenid);
    user.setStatus(UserStatus.NORMAL);
    user.setRoleLevel(DEFAULT_ROLE_LEVEL);
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
      throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR, "用户名或密码错误");
    }

    if (user.getStatus() == UserStatus.LOCKED) {
      throw new BusinessException(ResultCode.ACCOUNT_LOCKED, "账号已锁定");
    }

    String token = JwtUtils.generateToken(user.getId(), user.getUsername());
    UserAuth auth = new UserAuth();
    auth.setUserId(user.getId());
    auth.setToken(token);
    auth.setLoginIp(ip);
    auth.setExpireTime(LocalDateTime.now().plusDays(7));
    userAuthMapper.insert(auth);

    UserRole userRole = UserRole.fromLevel(user.getRoleLevel());
    return UserLoginVO.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .token(token)
        .roleLevel(user.getRoleLevel())
        .roleDescription(userRole.getDescription())
        .avatarFileId(user.getAvatarFileId())
        .build();
  }

  @Override
  public UserProfileVO getProfile(Long userId) {
    User user = userMapper.selectById(userId);
    if (user == null) {
      throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
    }

    String avatarSignedUrl = null;
    if (user.getAvatarFileId() != null) {
      File file = fileMapper.selectOne(new LambdaQueryWrapper<File>()
          .eq(File::getFileUuid, user.getAvatarFileId())
          .eq(File::getIsDeleted, 0));
      if (file != null) {
        avatarSignedUrl = ossUtils.generateSignedUrl(file.getBucketName(), file.getOssPath(), DEFAULT_EXPIRATION);
      }
    }

    UserRole userRole = UserRole.fromLevel(user.getRoleLevel());
    return UserProfileVO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .phone(user.getPhone())
        .wechatOpenid(user.getWechatOpenid())
        .status(user.getStatus())
        .roleLevel(user.getRoleLevel())
        .roleDescription(userRole.getDescription())
        .avatarFileId(user.getAvatarFileId())
        .avatarSignedUrl(avatarSignedUrl)
        .createTime(user.getCreateTime())
        .build();
  }

  @Override
  public PageResponse<UserListVO> listUsers(SearchRequest request) {
    Page<User> page = new Page<>(request.getCurrent(), request.getPageSize());
    QueryWrapper<User> wrapper = QueryWrapperUtils.buildWrapper(request, Arrays.asList("username", "phone"));

    userMapper.selectPage(page, wrapper);

    List<UserListVO> voList = page.getRecords().stream()
        .map(user -> UserListVO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .phone(user.getPhone())
            .wechatOpenid(user.getWechatOpenid())
            .status(user.getStatus())
            .roleLevel(user.getRoleLevel())
            .avatarFileId(user.getAvatarFileId())
            .createTime(user.getCreateTime())
            .build())
        .collect(Collectors.toList());

    return PageResponse.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void logout(String token) {
    UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
        .eq(UserAuth::getToken, token));

    if (userAuth != null) {
      userAuth.setExpireTime(LocalDateTime.of(1970, 1, 1, 0, 0, 0));
      userAuth.setToken(UUID.randomUUID().toString());
      userAuthMapper.updateById(userAuth);
    }
  }

  private String extractWechatOpenidFromToken(String tempToken) {
    if (tempToken == null || tempToken.isEmpty()) {
      return null;
    }

    try {
      io.jsonwebtoken.Claims claims = JwtUtils.extractAllClaims(tempToken);
      Boolean isTemp = claims.get("temp", Boolean.class);
      if (isTemp != null && isTemp) {
        return claims.get("wechatOpenid", String.class);
      }
    } catch (Exception e) {
      // Ignore token parsing errors
    }
    return null;
  }
}
