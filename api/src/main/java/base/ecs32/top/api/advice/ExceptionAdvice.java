package base.ecs32.top.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResultVo<Object> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("Missing request part: {}", e.getMessage());
        return ResultVo.fail(ResultCode.USER_ERROR, "Required part '" + e.getRequestPartName() + "' is not present");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("Missing request parameter: {}", e.getMessage());
        return ResultVo.fail(ResultCode.USER_ERROR, "Required parameter '" + e.getParameterName() + "' is not present");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Request body not readable: {}", e.getMessage());
        return ResultVo.fail(ResultCode.USER_ERROR, "Request body is malformed or missing");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("Method not supported: {}", e.getMessage());
        return ResultVo.fail(ResultCode.USER_ERROR, "Method '" + e.getMethod() + "' is not supported for this request");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultVo<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("File size exceeded: {}", e.getMessage());
        return ResultVo.fail(ResultCode.FILE_SIZE_EXCEEDED, e.getMessage());
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
