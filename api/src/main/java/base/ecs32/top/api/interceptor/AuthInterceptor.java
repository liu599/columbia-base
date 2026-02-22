package base.ecs32.top.api.interceptor;

import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.advice.ResultVo;
import base.ecs32.top.api.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        System.out.println("AuthInterceptor: URI=" + request.getRequestURI() + ", Method=" + request.getMethod());

        String authHeader = request.getHeader("Authorization");
        System.out.println("AuthInterceptor: Authorization header=" + authHeader);

        String errorMessage = "";

        if (authHeader == null) {
            errorMessage = "缺少 Authorization 请求头";
        } else if (!authHeader.startsWith("Bearer ")) {
            errorMessage = "Authorization 格式错误，应为: Bearer <token>";
        } else {
            String token = authHeader.substring(7);
            System.out.println("AuthInterceptor: Token extracted, length=" + token.length() + ", validating...");

            String validationError = JwtUtils.getValidationError(token);
            if (validationError == null) {
                Long userId = JwtUtils.extractUserId(token);
                System.out.println("AuthInterceptor: Token valid, userId=" + userId);
                request.setAttribute("userId", userId);
                return true;
            } else {
                errorMessage = "Token 无效: " + validationError;
            }
        }

        System.out.println("AuthInterceptor: Auth failed - " + errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ResultVo<Object> resultVo = ResultVo.fail(ResultCode.USER_ERROR, errorMessage);
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultVo));
        return false;
    }
}
