package com.flux.common.web.exception;

import com.flux.common.core.result.Result;
import com.flux.common.core.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

/**
 * @ClassName GlobalException
 * @Author njq
 * @Date 2026/10/28 16:22
 * @Description 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常 code={}, msg={}", e.getResultCode().getCode(), e.getMessage());
        return Result.failed(e.getResultCode(), e.getMessage());
    }

    /**
     * @Valid 请求体JSON校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : ResultCode.PARAM_ERROR.getMsg();
        log.warn("参数校验异常:{}", msg);
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * form表单对象校验
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : ResultCode.PARAM_ERROR.getMsg();
        log.warn("参数绑定异常:{}", msg);
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * @Validated 简单参数校验(@RequestParam/@PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String msg = violations.iterator().next().getMessage();
        log.warn("请求参数校验异常:{}", msg);
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * 缺少必填的请求参数(@RequestParam 未传)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数:{}", e.getParameterName());
        return Result.failed(ResultCode.PARAM_IS_NULL, "缺少必填参数: " + e.getParameterName());
    }

    /**
     * 请求方法不支持(例如接口只支持 POST，却用 GET 访问)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持:{}", e.getMethod());
        return Result.failed(ResultCode.INTERFACE_NOT_EXIST, "请求方法不支持: " + e.getMethod());
    }

    /**
     * 请求资源不存在(404)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("请求资源不存在:{}", e.getResourcePath());
        return Result.failed(ResultCode.RESOURCE_NOT_FOUND);
    }

    /**
     * 上传文件超过大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("上传文件超过大小限制:{}", e.getMessage());
        return Result.failed(ResultCode.USER_UPLOAD_FILE_SIZE_EXCEEDS);
    }

    /**
     * 系统未知异常兜底
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统未知异常", e);
        return Result.failed(ResultCode.SYSTEM_EXECUTION_ERROR);
    }
}
