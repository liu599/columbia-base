package base.ecs32.top.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
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
        return ResultVo.fail(ResultCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResultVo<Object> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("Missing request part: {}", e.getMessage());
        return ResultVo.fail(ResultCode.MISSING_REQUEST_PART, "缺少必需的文件参数：" + e.getRequestPartName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("Missing request parameter: {}", e.getMessage());
        return ResultVo.fail(ResultCode.MISSING_REQUIRED_PARAMETER, "缺少必需参数：" + e.getParameterName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Request body not readable: {}", e.getMessage());
        return ResultVo.fail(ResultCode.INVALID_REQUEST_BODY, "请求体格式错误或为空");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("Method not supported: {}", e.getMessage());
        return ResultVo.fail(ResultCode.METHOD_NOT_SUPPORTED, "不支持的请求方法：" + e.getMethod());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultVo<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("File size exceeded: {}", e.getMessage());
        return ResultVo.fail(ResultCode.FILE_SIZE_EXCEEDED, "文件大小超过限制");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVo<Object> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("Duplicate key error: {}", e.getMessage());
        return ResultVo.fail(ResultCode.DATA_INTEGRITY_ERROR, "数据已存在，请勿重复提交");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResultVo<Object> handleDataAccessException(DataAccessException e) {
        log.error("Database access error: {}", e.getMessage());
        return ResultVo.fail(ResultCode.DATABASE_ERROR, "数据库操作失败");
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
