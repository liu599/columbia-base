package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.CreditRechargeRequest;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.vo.CreditBalanceVO;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/credit")
@RequiredArgsConstructor
public class AdminCreditController {

    private final CreditService creditService;

    @PostMapping("/recharge")
    @AdminAudit(module = AuditModule.CREDIT, action = AuditAction.MANUAL_RECHARGE)
    public Map<String, Object> recharge(@RequestBody CreditRechargeRequest request) {
        CreditBalanceVO oldBalance = creditService.getBalance(request.getTargetUserId());
        AuditContext.setBeforeValue(oldBalance);
        AuditContext.setTargetId(request.getTargetUserId().toString());

        creditService.manualRecharge(request);

        CreditBalanceVO newBalance = creditService.getBalance(request.getTargetUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("target_user_id", request.getTargetUserId());
        data.put("old_balance", oldBalance.getAvailableCredits());
        data.put("new_balance", newBalance.getAvailableCredits());
        return data;
    }
}
