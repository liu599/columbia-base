package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.UserStatusRequest;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.entity.User;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import base.ecs32.top.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping("/status")
    @AdminAudit(module = AuditModule.USER, action = AuditAction.BAN_USER)
    public Map<String, Object> updateStatus(@RequestBody UserStatusRequest request) {
        User user = userService.getById(request.getTargetUserId());
        if (user != null) {
            AuditContext.setBeforeValue(user.getStatus());
            AuditContext.setTargetId(user.getId().toString());
            
            user.setStatus(request.getStatus() == 0 ? UserStatus.LOCKED : UserStatus.NORMAL);
            userService.updateById(user);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("target_user_id", request.getTargetUserId());
        data.put("current_status", request.getStatus());
        return data;
    }
}
