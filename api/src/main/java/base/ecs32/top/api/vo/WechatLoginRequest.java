package base.ecs32.top.api.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WechatLoginRequest {

    @NotBlank(message = "场景ID不能为空")
    private String sceneId;

    @NotBlank(message = "登录code不能为空")
    private String loginCode;
}
