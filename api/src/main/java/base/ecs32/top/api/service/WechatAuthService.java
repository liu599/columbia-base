package base.ecs32.top.api.service;

import java.util.Map;

public interface WechatAuthService {

    /**
     * 获取微信access_token
     */
    String getAccessToken();

    /**
     * 获取不限制的小程序码
     * @param token 白名单token
     * @return 包含临时URL的Map
     */
    Map<String, String> getWxacode(String token);

    /**
     * 扫码登录
     * @param sceneId 场景ID
     * @param loginCode 微信登录code
     * @return 登录token或临时注册token
     */
    String loginWithWechat(String sceneId, String loginCode);

    /**
     * 强制刷新微信access_token
     * @param token 白名单token
     * @return 刷新后的access_token
     */
    String refreshAccessToken(String token);
}
