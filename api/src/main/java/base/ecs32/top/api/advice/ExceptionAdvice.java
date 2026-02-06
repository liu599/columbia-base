package base.ecs32.top.api.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResultVo<Object> handleException(Exception e) {
        return ResultVo.fail(ResultCode.SERVER_ERROR);
    }
}
