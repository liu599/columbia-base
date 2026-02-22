package base.ecs32.top.api.interceptor;

import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.advice.ResultVo;
import base.ecs32.top.dao.UserMapper;
import base.ecs32.top.entity.User;
import base.ecs32.top.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    /**
     * 管理员最低权限等级
     * 7 - 内部管理用户及以上可访问管理接口
     */
    private static final int MIN_ADMIN_LEVEL = UserRole.INTERNAL_ADMIN.getLevel();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long userId) {
            User user = userMapper.selectById(userId);
            if (user != null && user.getRoleLevel() != null && user.getRoleLevel() >= MIN_ADMIN_LEVEL) {
                // Put user role level in request attribute for later use
                request.setAttribute("roleLevel", user.getRoleLevel());
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        // Reusing USER_ERROR or we could add a specific FORBIDDEN error
        ResultVo<Object> resultVo = ResultVo.fail(ResultCode.PERMISSION_DENIED);
        resultVo.setMessage("无权访问管理接口，需要内部管理用户及以上权限");
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultVo));
        return false;
    }
}
