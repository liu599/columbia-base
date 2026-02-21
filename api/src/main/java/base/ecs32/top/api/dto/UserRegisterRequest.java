package base.ecs32.top.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String verifyCode;
    private String tempToken;
}
