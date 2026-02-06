package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.BatchCreateActivationRequest;
import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/activation")
@RequiredArgsConstructor
public class AdminActivationController {

    private final ActivationCodeService activationCodeService;

    @PostMapping("/batch-create")
    @AdminAudit(module = AuditModule.ACTIVATION, action = AuditAction.BATCH_CREATE_CODE)
    public Map<String, Object> batchCreate(@RequestBody BatchCreateActivationRequest request) {
        AuditContext.setTargetId(request.getProductId().toString());
        
        List<String> codes = activationCodeService.batchCreate(request);

        Map<String, Object> data = new HashMap<>();
        data.put("product_id", request.getProductId());
        data.put("codes", codes);
        return data;
    }
}
