package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.dto.UserLoginRequest;
import base.ecs32.top.api.dto.UserRegisterRequest;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.api.vo.UserListVO;
import base.ecs32.top.api.vo.UserLoginVO;
import base.ecs32.top.api.vo.UserProfileVO;
import base.ecs32.top.api.vo.UserRegisterVO;
import base.ecs32.top.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    UserRegisterVO register(UserRegisterRequest request);
    UserLoginVO login(UserLoginRequest request, String ip);
    UserProfileVO getProfile(Long userId);
    PageResponse<UserListVO> listUsers(SearchRequest request);
    void logout(String token);
}
