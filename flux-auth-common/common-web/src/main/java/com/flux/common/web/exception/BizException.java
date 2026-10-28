package com.flux.common.web.exception;

import com.flux.common.core.result.IResultCode;
import com.flux.common.core.result.ResultCode;
import lombok.Getter;

/**
 * @ClassName BizException
 * @Author njq
 * @Date 2026/10/28 16:22
 * @Description 自定义业务异常
 */
@Getter
public class BizException extends RuntimeException{

    private final IResultCode resultCode;

    public BizException(IResultCode resultCode) {
        super(resultCode.getMsg());
        this.resultCode = resultCode;
    }

    public BizException(String message) {
        super(message);
        this.resultCode = ResultCode.SYSTEM_EXECUTION_ERROR;
    }

    public BizException(IResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public BizException(IResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }
}
