package base.ecs32.top.api.aspect;

import base.ecs32.top.dao.AuditLogMapper;
import base.ecs32.top.entity.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuditAspect {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(adminAudit)")
    public Object around(ProceedingJoinPoint joinPoint, AdminAudit adminAudit) throws Throwable {
        Object result = null;
        Throwable throwable = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            saveLog(joinPoint, adminAudit, result, throwable);
            AuditContext.clear();
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, AdminAudit adminAudit, Object result, Throwable throwable) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;
            HttpServletRequest request = attributes.getRequest();

            Long adminId = (Long) request.getAttribute("userId");
            if (adminId == null) return;

            AuditLog auditLog = new AuditLog();
            auditLog.setAdminId(adminId);
            auditLog.setModule(adminAudit.module().name());
            auditLog.setAction(adminAudit.action().name());
            auditLog.setIpAddress(getIpAddr(request));
            auditLog.setCreateTime(LocalDateTime.now());

            // Target ID from Context (set by Service)
            auditLog.setTargetId(AuditContext.getTargetId());

            // Before Value from Context (set by Service)
            Object beforeObj = AuditContext.getBeforeValue();
            if (beforeObj != null) {
                auditLog.setBeforeValue(objectMapper.writeValueAsString(beforeObj));
            }

            // After Value from Method Arguments
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                auditLog.setAfterValue(objectMapper.writeValueAsString(args[0]));
            }

            // Remark
            String remark = adminAudit.remark();
            if (throwable != null) {
                remark = (remark.isEmpty() ? "" : remark + " | ") + "Error: " + throwable.getMessage();
            }
            auditLog.setRemark(remark);

            auditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
