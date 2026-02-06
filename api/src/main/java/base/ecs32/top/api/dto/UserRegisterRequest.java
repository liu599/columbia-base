package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private String phone;
    private String verifyCode;
}
