package base.ecs32.top.api.controller;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.aspect.AuditContext;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.dto.UserStatusRequest;
import base.ecs32.top.api.service.UserService;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.api.vo.UserListVO;
import base.ecs32.top.entity.User;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import base.ecs32.top.enums.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
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

    private static final int MIN_MANAGE_LEVEL = 5;

    @PostMapping("/status")
    @AdminAudit(module = AuditModule.USER, action = AuditAction.BAN_USER)
    public Map<String, Object> updateStatus(@RequestBody UserStatusRequest request, HttpServletRequest httpRequest) {
        // 获取当前操作用户的角色等级
        Integer currentUserRoleLevel = (Integer) httpRequest.getAttribute("roleLevel");
        if (currentUserRoleLevel == null) {
            throw new BusinessException(ResultCode.USER_ERROR, "无法获取用户权限信息");
        }

        // 验证当前用户等级 > 5
        if (currentUserRoleLevel <= MIN_MANAGE_LEVEL) {
            throw new BusinessException(ResultCode.USER_ERROR, "无权操作，需要等级大于5的用户权限");
        }

        User user = userService.getById(request.getTargetUserId());
        if (user != null) {
            // 验证目标用户的等级不能高于当前操作用户
            if (user.getRoleLevel() != null && user.getRoleLevel() >= currentUserRoleLevel) {
                throw new BusinessException(ResultCode.USER_ERROR, "无权修改等级高于或等于自己的用户信息");
            }

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

    @PostMapping("/list")
    public PageResponse<UserListVO> listUsers(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        // 获取当前操作用户的角色等级
        Integer currentUserRoleLevel = (Integer) httpRequest.getAttribute("roleLevel");
        if (currentUserRoleLevel == null) {
            throw new BusinessException(ResultCode.USER_ERROR, "无法获取用户权限信息");
        }

        // 验证当前用户等级 > 5
        if (currentUserRoleLevel <= MIN_MANAGE_LEVEL) {
            throw new BusinessException(ResultCode.USER_ERROR, "无权操作，需要等级大于5的用户权限");
        }

        return userService.listUsers(request);
    }
}
