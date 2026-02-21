package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.config.WechatConfig;
import base.ecs32.top.api.service.WechatAuthService;
import base.ecs32.top.api.util.JwtUtils;
import base.ecs32.top.api.util.OssUtils;
import base.ecs32.top.dao.WechatAuthMapper;
import base.ecs32.top.dao.UserMapper;
import base.ecs32.top.entity.User;
import base.ecs32.top.entity.WechatAuth;
import base.ecs32.top.enums.WechatAuthStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WechatAuthServiceImpl extends ServiceImpl<WechatAuthMapper, WechatAuth>
        implements WechatAuthService {

    private final WechatConfig wechatConfig;
    private final StringRedisTemplate stringRedisTemplate;
    private final RestTemplate restTemplate;
    private final OssUtils ossUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Random RANDOM = new Random();
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int SCENE_ID_LENGTH = 6;
    private static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    private static final String WECHAT_WXACODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";
    private static final String WECHAT_JSCODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WechatAuthMapper wechatAuthMapper;
    private final UserMapper userMapper;

    public WechatAuthServiceImpl(WechatConfig wechatConfig, StringRedisTemplate stringRedisTemplate,
                                  RestTemplate restTemplate, OssUtils ossUtils, WechatAuthMapper wechatAuthMapper,
                                  UserMapper userMapper) {
        this.wechatConfig = wechatConfig;
        this.stringRedisTemplate = stringRedisTemplate;
        this.restTemplate = restTemplate;
        this.ossUtils = ossUtils;
        this.wechatAuthMapper = wechatAuthMapper;
        this.userMapper = userMapper;
    }

    @Override
    public String getAccessToken() {
        String cachedToken = stringRedisTemplate.opsForValue().get(wechatConfig.getAccessTokenKey());
        if (cachedToken != null) {
            return cachedToken;
        }

        String url = String.format("%s?grant_type=client_credential&appid=%s&secret=%s",
                WECHAT_ACCESS_TOKEN_URL,
                wechatConfig.getAppid(),
                wechatConfig.getSecret());

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "获取微信access_token失败");
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.path("access_token").asText();
            Integer expiresIn = jsonNode.path("expires_in").asInt(wechatConfig.getAccessTokenExpireTime());

            if (accessToken == null || accessToken.isEmpty()) {
                String errCode = jsonNode.path("errcode").asText();
                String errMsg = jsonNode.path("errmsg").asText();
                throw new BusinessException(ResultCode.SERVER_ERROR,
                        "获取微信access_token失败: " + errCode + " - " + errMsg);
            }

            stringRedisTemplate.opsForValue().set(wechatConfig.getAccessTokenKey(),
                    accessToken, expiresIn, TimeUnit.SECONDS);
            return accessToken;

        } catch (Exception e) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "解析微信access_token响应失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> getWxacode(String token) {
        if (wechatConfig.getQrcodeWhitelist() == null ||
                !wechatConfig.getQrcodeWhitelist().contains(token)) {
            throw new BusinessException(ResultCode.USER_ERROR, "无权限访问");
        }

        String accessToken = getAccessToken();
        String sceneId = generateSceneId();

        WechatAuth wechatAuth = new WechatAuth();
        wechatAuth.setScene(sceneId);
        wechatAuth.setOpenid(null);
        wechatAuth.setStatus(WechatAuthStatus.PENDING);
        wechatAuth.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        wechatAuth.setCreateTime(LocalDateTime.now());
        wechatAuthMapper.insert(wechatAuth);

        try {
            String url = String.format("%s?access_token=%s", WECHAT_WXACODE_URL, accessToken);

            WxacodeRequest request = new WxacodeRequest();
            request.setScene(sceneId);

            String bodyJson = objectMapper.writeValueAsString(request);
            log.info("Wechat wxacode request URL: {}", url);
            log.info("Wechat wxacode request Body: {}", bodyJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            log.info("Wechat wxacode response status: {}", response.getStatusCode());
            log.info("Wechat wxacode response headers: {}", response.getHeaders());
            log.info("Wechat wxacode response content-type: {}", response.getHeaders().getContentType());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(ResultCode.SERVER_ERROR, "获取小程序码失败");
            }

            // 检查返回的是 JSON 错误还是图片字节
            String responseBody = response.getBody();
            MediaType contentType = response.getHeaders().getContentType();

            if (contentType != null && contentType.getType().equals("application")) {
                // 返回的是 JSON 错误信息
                log.info("Wechat wxacode response body (JSON): {}", responseBody);
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String errCode = jsonNode.path("errcode").asText();
                    String errMsg = jsonNode.path("errmsg").asText();
                    throw new BusinessException(ResultCode.SERVER_ERROR,
                            "获取小程序码失败: " + errCode + " - " + errMsg);
                } catch (Exception e) {
                    throw new BusinessException(ResultCode.SERVER_ERROR, "获取小程序码失败: " + responseBody);
                }
            }

            // 返回的是图片字节 - 重新请求以获取字节
            ResponseEntity<byte[]> byteResponse = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
            byte[] body = byteResponse.getBody();
            if (body == null || body.length == 0) {
                throw new BusinessException(ResultCode.SERVER_ERROR, "获取小程序码失败，返回为空");
            }
            log.info("Wechat wxacode response body length: {}", body.length);

            // 上传图片到阿里云 OSS
            String objectName = "wechat-qrcode/" + sceneId + ".jpg";
            ossUtils.uploadFile(objectName, new ByteArrayInputStream(body), "image/jpeg");

            // 生成临时 URL（30分钟有效期）
            String tempUrl = ossUtils.generateSignedUrl(ossUtils.getBucketName(), objectName, 1800);

            Map<String, String> result = new HashMap<>();
            result.put("url", tempUrl);
            result.put("sceneId", sceneId);
            return result;

        } catch (Exception e) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "获取小程序码失败: " + e.getMessage());
        }
    }

    @Override
    public String refreshAccessToken(String token) {
        if (wechatConfig.getQrcodeWhitelist() == null ||
                !wechatConfig.getQrcodeWhitelist().contains(token)) {
            throw new BusinessException(ResultCode.USER_ERROR, "无权限访问");
        }

        // 先删除 Redis 中的缓存的 token
        stringRedisTemplate.delete(wechatConfig.getAccessTokenKey());

        // 重新获取新的 token
        return getAccessToken();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String loginWithWechat(String sceneId, String loginCode) {
        WechatAuth wechatAuth = wechatAuthMapper.selectOne(
                new LambdaQueryWrapper<WechatAuth>().eq(WechatAuth::getScene, sceneId));

        if (wechatAuth == null) {
            throw new BusinessException(ResultCode.USER_ERROR, "二维码不存在");
        }

        if (wechatAuth.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.USER_ERROR, "二维码已过期");
        }

        try {
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    WECHAT_JSCODE2SESSION_URL,
                    wechatConfig.getAppid(),
                    wechatConfig.getSecret(),
                    loginCode);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(ResultCode.SERVER_ERROR, "微信登录失败");
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String openid = jsonNode.path("openid").asText();
            String errCode = jsonNode.path("errcode").asText();

            if (openid == null || openid.isEmpty() || !errCode.isEmpty()) {
                String errMsg = jsonNode.path("errmsg").asText();
                throw new BusinessException(ResultCode.USER_ERROR,
                        "登录失败: " + errCode + " - " + errMsg);
            }

            wechatAuth.setOpenid(openid);
            wechatAuth.setStatus(WechatAuthStatus.SUCCESS);
            wechatAuthMapper.updateById(wechatAuth);

            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getWechatOpenid, openid));

            if (user != null) {
                return JwtUtils.generateToken(user.getId(), user.getUsername());
            } else {
                return generateTempToken(openid);
            }

        } catch (Exception e) {
            throw new BusinessException(ResultCode.SERVER_ERROR, "解析微信登录响应失败");
        }
    }

    private String generateSceneId() {
        String sceneId;
        do {
            StringBuilder sb = new StringBuilder(SCENE_ID_LENGTH);
            for (int i = 0; i < SCENE_ID_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }
            sceneId = sb.toString();
        } while (wechatAuthMapper.selectOne(
                new LambdaQueryWrapper<WechatAuth>().eq(WechatAuth::getScene, sceneId)) != null);
        return sceneId;
    }

    private String generateTempToken(String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("temp", true);
        claims.put("wechatOpenid", openid);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject("temp_register")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000)) // 10分钟
                .signWith(JwtUtils.key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static class WxacodeRequest {
        private String scene;
        private String page = "pages/home/home";
        private Boolean check_path = false;
        private String env_version = "release";

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public Boolean getCheck_path() {
            return check_path;
        }

        public void setCheck_path(Boolean check_path) {
            this.check_path = check_path;
        }

        public String getEnv_version() {
            return env_version;
        }

        public void setEnv_version(String env_version) {
            this.env_version = env_version;
        }
    }
}
