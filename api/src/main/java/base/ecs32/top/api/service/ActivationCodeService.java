package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.BatchCreateActivationRequest;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.vo.ActivationCodeListVO;
import base.ecs32.top.api.vo.RedeemVO;
import base.ecs32.top.api.vo.UserActivationsVO;
import base.ecs32.top.entity.ActivationCode;

import java.util.List;

public interface ActivationCodeService {
    RedeemVO redeem(Long userId, String code);
    void manualActivate(Long targetUserId, Long productId, String remark);
    List<String> batchCreate(BatchCreateActivationRequest request);
    UserActivationsVO getUserActivations(Long userId);
    boolean checkUserActivation(Long userId, Long productId);
    void deactivateUserProduct(Long userId, Long productId, String remark);
    ActivationCode findUserActivation(Long userId, Long productId);
    ActivationCodeListVO.ActivationCodePageResponse listActivationCodes(SearchRequest request);
}
