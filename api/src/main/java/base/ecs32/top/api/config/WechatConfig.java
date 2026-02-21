package base.ecs32.top.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("wechat.mini-program")
public class WechatConfig {

    private String appid;

    private String secret;

    private String accessTokenKey;

    private Integer accessTokenExpireTime;

    private List<String> qrcodeWhitelist;
}
