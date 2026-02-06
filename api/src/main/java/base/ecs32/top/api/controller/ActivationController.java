package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.RedeemRequest;
import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.api.vo.RedeemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activation")
@RequiredArgsConstructor
public class ActivationController {

    private final ActivationCodeService activationCodeService;

    @PostMapping("/redeem")
    public RedeemVO redeem(@RequestAttribute("userId") Long userId, @RequestBody RedeemRequest request) {
        return activationCodeService.redeem(userId, request.getCode());
    }
}
