package base.ecs32.top.api.aspect;

import base.ecs32.top.enums.AuditAction;
import base.ecs32.top.enums.AuditModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminAudit {
    AuditModule module();
    AuditAction action();
    String remark() default "";
}
