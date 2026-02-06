package base.ecs32.top.api.controller;

import base.ecs32.top.api.aspect.AdminAudit;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.dao.AuditLogMapper;
import base.ecs32.top.entity.AuditLog;
import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AdminAuditController {

    private final AuditLogMapper auditLogMapper;

    @PostMapping("/list")
    @AdminAudit(module = AuditModule.AUDIT, action = AuditAction.QUERY_AUDIT_LOG)
    public PageResponse<AuditLog> list(@RequestBody SearchRequest request) {
        Page<AuditLog> page = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<AuditLog> wrapper = QueryWrapperUtils.buildWrapper(request, Collections.singletonList("remark"));
        
        // Sort by create_time desc by default if not specified
        if (request.getSort() == null) {
            wrapper.orderByDesc("create_time");
        }

        auditLogMapper.selectPage(page, wrapper);

        return PageResponse.of(page.getRecords(), page.getTotal(), (int)page.getCurrent(), (int)page.getSize());
    }
}
