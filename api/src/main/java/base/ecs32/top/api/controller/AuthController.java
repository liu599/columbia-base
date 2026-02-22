package base.ecs32.top.api.controller;

import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.advice.ResultVo;
import base.ecs32.top.api.util.JwtUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/check")
    public ResultVo<Map<String, Object>> checkToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();

        if (authHeader == null) {
            result.put("valid", false);
            result.put("error", "缺少 Authorization 请求头");
            return ResultVo.fail(ResultCode.USER_NOT_LOGGED_IN, "缺少 Authorization 请求头");
        }

        if (!authHeader.startsWith("Bearer ")) {
            result.put("valid", false);
            result.put("error", "Authorization 格式错误，应为: Bearer <token>");
            return ResultVo.fail(ResultCode.USER_NOT_LOGGED_IN, "Authorization 格式错误");
        }

        String token = authHeader.substring(7);
        String validationError = JwtUtils.getValidationError(token);

        if (validationError == null) {
            result.put("valid", true);
            result.put("userId", JwtUtils.extractUserId(token));
            result.put("username", JwtUtils.extractUsername(token));
            return ResultVo.success(result);
        } else {
            result.put("valid", false);
            result.put("error", validationError);
            return ResultVo.fail(ResultCode.USER_NOT_LOGGED_IN, validationError);
        }
    }
}
