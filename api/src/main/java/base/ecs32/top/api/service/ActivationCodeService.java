package base.ecs32.top.api.service;

import base.ecs32.top.api.vo.RedeemVO;

public interface ActivationCodeService {
    RedeemVO redeem(Long userId, String code);
}
