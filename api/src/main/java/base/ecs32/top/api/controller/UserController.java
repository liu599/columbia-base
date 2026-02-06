package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.UserLoginRequest;
import base.ecs32.top.api.dto.UserRegisterRequest;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.api.vo.UserLoginVO;
import base.ecs32.top.api.vo.UserProfileVO;
import base.ecs32.top.api.vo.UserRegisterVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserRegisterVO register(@RequestBody UserRegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserLoginVO login(@RequestBody UserLoginRequest request, HttpServletRequest httpServletRequest) {
        return userService.login(request, httpServletRequest.getRemoteAddr());
    }

    @PostMapping("/profile")
    public UserProfileVO profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getProfile(userId);
    }



    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
