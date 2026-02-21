package base.ecs32.top.enums;

public enum WechatAuthStatus {

    /**
     * 等待扫码
     */
    PENDING("pending"),

    /**
     * 已过期
     */
    EXPIRE("expire"),

    /**
     * 扫码成功
     */
    SUCCESS("success")

    ;

    private final String code;

    WechatAuthStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static WechatAuthStatus fromCode(String code) {
        for (WechatAuthStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown WechatAuthStatus code: " + code);
    }
}
