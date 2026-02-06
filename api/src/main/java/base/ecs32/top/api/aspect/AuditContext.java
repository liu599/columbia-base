package base.ecs32.top.api.aspect;

public class AuditContext {
    private static final ThreadLocal<Object> BEFORE_VALUE_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> TARGET_ID_HOLDER = new ThreadLocal<>();

    public static void setBeforeValue(Object beforeValue) {
        BEFORE_VALUE_HOLDER.set(beforeValue);
    }

    public static Object getBeforeValue() {
        return BEFORE_VALUE_HOLDER.get();
    }

    public static void setTargetId(String targetId) {
        TARGET_ID_HOLDER.set(targetId);
    }

    public static String getTargetId() {
        return TARGET_ID_HOLDER.get();
    }

    public static void clear() {
        BEFORE_VALUE_HOLDER.remove();
        TARGET_ID_HOLDER.remove();
    }
}
