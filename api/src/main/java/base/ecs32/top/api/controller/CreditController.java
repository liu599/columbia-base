package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.vo.CreditBalanceVO;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.entity.CreditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/balance")
    public CreditBalanceVO getBalance(@RequestAttribute("userId") Long userId) {
        return creditService.getBalance(userId);
    }

    @PostMapping("/logs")
    public PageResponse<CreditLog> getLogs(@RequestAttribute("userId") Long userId, @RequestBody SearchRequest request) {
        return creditService.getLogs(userId, request);
    }
}
