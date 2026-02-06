package base.ecs32.top.api.interceptor;

import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.advice.ResultVo;
import base.ecs32.top.api.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (JwtUtils.validateToken(token)) {
                Long userId = JwtUtils.extractUserId(token);
                request.setAttribute("userId", userId);
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ResultVo<Object> resultVo = ResultVo.fail(ResultCode.USER_ERROR);
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultVo));
        return false;
    }
}
