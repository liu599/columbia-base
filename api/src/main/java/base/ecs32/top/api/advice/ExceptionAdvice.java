package base.ecs32.top.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", message);
        return ResultVo.fail(ResultCode.USER_ERROR, message);
    }

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
