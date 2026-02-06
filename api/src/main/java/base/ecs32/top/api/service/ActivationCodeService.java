package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.BatchCreateActivationRequest;
import base.ecs32.top.api.vo.RedeemVO;

import java.util.List;

public interface ActivationCodeService {
    RedeemVO redeem(Long userId, String code);
    void manualActivate(Long targetUserId, Long productId, String remark);
    List<String> batchCreate(BatchCreateActivationRequest request);
}
