package base.ecs32.top.api.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String account;
    private String password;
    private String loginIp;

}
