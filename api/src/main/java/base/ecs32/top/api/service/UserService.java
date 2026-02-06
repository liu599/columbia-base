package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.UserLoginRequest;
import base.ecs32.top.api.dto.UserRegisterRequest;
import base.ecs32.top.api.vo.UserLoginVO;
import base.ecs32.top.api.vo.UserProfileVO;
import base.ecs32.top.api.vo.UserRegisterVO;

public interface UserService {
    UserRegisterVO register(UserRegisterRequest request);
    UserLoginVO login(UserLoginRequest request);
    UserProfileVO getProfile(Long userId);
    void logout(String token);
}
