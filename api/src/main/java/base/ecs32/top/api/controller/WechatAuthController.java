package base.ecs32.top.api.controller;

import base.ecs32.top.api.service.WechatAuthService;
import base.ecs32.top.api.vo.WechatLoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wechat")
public class WechatAuthController {

    private final WechatAuthService wechatAuthService;

    @Autowired
    public WechatAuthController(WechatAuthService wechatAuthService) {
        this.wechatAuthService = wechatAuthService;
    }

    @GetMapping("/qrcode")
    public Map<String, String> getWxacode(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        return wechatAuthService.getWxacode(actualToken);
    }

    @PostMapping("/login")
    public String loginWithWechat(@RequestBody WechatLoginRequest request) {
        return wechatAuthService.loginWithWechat(request.getSceneId(), request.getLoginCode());
    }
}
