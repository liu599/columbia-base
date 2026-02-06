package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.ProductActivateRequest;
import base.ecs32.top.api.dto.ProductSaveRequest;
import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.entity.Product;
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
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ActivationCodeService activationCodeService;

    @PostMapping("/save")
    @AdminAudit(module = AuditModule.PRODUCT, action = AuditAction.SAVE_PRODUCT)
    public Map<String, Object> saveProduct(@RequestBody ProductSaveRequest request) {
        Product product;
        if (request.getId() != null) {
            product = productService.getById(request.getId());
            if (product != null) {
                AuditContext.setBeforeValue(product);
                AuditContext.setTargetId(product.getId().toString());
            } else {
                product = new Product();
            }
        } else {
            product = new Product();
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBaseCredits(request.getBaseCredits());
        product.setStatus(request.getStatus());

        productService.saveOrUpdate(product);
        AuditContext.setTargetId(product.getId().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("id", product.getId());
        return data;
    }

    @PostMapping("/activate")
    @AdminAudit(module = AuditModule.PRODUCT, action = AuditAction.MANUAL_ACTIVATE)
    public Map<String, Object> manualActivate(@RequestBody ProductActivateRequest request) {
        AuditContext.setTargetId(request.getTargetUserId().toString());
        
        // Manual activation logic: skip code, just give product benefits
        // This is a new feature, implementation depends on ActivationCodeService or a new service method
        activationCodeService.manualActivate(request.getTargetUserId(), request.getProductId(), request.getRemark());

        Product product = productService.getById(request.getProductId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getTargetUserId());
        data.put("product_name", product != null ? product.getName() : "Unknown");
        data.put("added_credits", product != null ? product.getBaseCredits() : 0);
        return data;
    }
}
