package base.ecs32.top.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BusinessException.class)
    public ResultVo<Object> handleBusinessException(BusinessException e) {
        return ResultVo.fail(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<Object> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ResultVo.fail(ResultCode.SERVER_ERROR);
    }
}
