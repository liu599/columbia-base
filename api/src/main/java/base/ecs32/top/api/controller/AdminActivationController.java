package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.BatchCreateActivationRequest;
import base.ecs32.top.api.dto.DeactivateProductRequest;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.dto.UserActivationCheckRequest;
import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.api.vo.ActivationCodeListVO;
import base.ecs32.top.entity.ActivationCode;
import base.ecs32.top.entity.Product;
import base.ecs32.top.entity.User;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import base.ecs32.top.enums.UserRole;
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
    private final ProductService productService;
    private final UserService userService;

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

    @PostMapping("/check-user")
    @AdminAudit(module = AuditModule.ACTIVATION, action = AuditAction.QUERY_USER_ACTIVATION)
    public Map<String, Object> checkUserActivation(@RequestBody UserActivationCheckRequest request) {
        AuditContext.setTargetId(request.getTargetUserId().toString());

        Product product = productService.getById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("产品不存在");
        }

        ActivationCode activation = activationCodeService.findUserActivation(
                request.getTargetUserId(), request.getProductId());

        User user = userService.getById(request.getTargetUserId());
        UserRole userRole = UserRole.fromLevel(user != null ? user.getRoleLevel() : null);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", request.getTargetUserId());
        data.put("productId", request.getProductId());
        data.put("productName", product.getName());
        data.put("isActivated", activation != null);
        data.put("activationId", activation != null ? activation.getId() : null);
        data.put("roleLevel", user != null ? user.getRoleLevel() : null);
        data.put("roleDescription", userRole.getDescription());
        data.put("avatarFileId", user != null ? user.getAvatarFileId() : null);
        return data;
    }

    @PostMapping("/deactivate")
    @AdminAudit(module = AuditModule.ACTIVATION, action = AuditAction.DEACTIVATE_PRODUCT)
    public Map<String, Object> deactivateProduct(@RequestBody DeactivateProductRequest request) {
        AuditContext.setTargetId(request.getTargetUserId().toString());

        activationCodeService.deactivateUserProduct(
                request.getTargetUserId(),
                request.getProductId(),
                request.getRemark()
        );

        Product product = productService.getById(request.getProductId());

        Map<String, Object> data = new HashMap<>();
        data.put("user_id", request.getTargetUserId());
        data.put("product_id", request.getProductId());
        data.put("product_name", product != null ? product.getName() : "Unknown");
        return data;
    }

    @PostMapping("/list")
    @AdminAudit(module = AuditModule.ACTIVATION, action = AuditAction.QUERY_ACTIVATION_CODES)
    public ActivationCodeListVO.ActivationCodePageResponse listActivationCodes(@RequestBody SearchRequest request) {
        return activationCodeService.listActivationCodes(request);
    }
}
