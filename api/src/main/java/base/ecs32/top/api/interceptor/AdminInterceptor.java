package base.ecs32.top.api.interceptor;

import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.advice.ResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long userId) {
            // Hardcoded admin range 1-10 as per requirement
            if (userId >= 1 && userId <= 10) {
                return true;
            }
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        // Reusing USER_ERROR or we could add a specific FORBIDDEN error
        ResultVo<Object> resultVo = ResultVo.fail(ResultCode.USER_ERROR);
        resultVo.setMessage("无权访问管理接口");
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultVo));
        return false;
    }
}
