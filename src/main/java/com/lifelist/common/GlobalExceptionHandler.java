package com.lifelist.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

/**
 * 全局异常处理器
 * 把各种异常统一转成 R 返回，避免裸抛 500
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBiz(BizException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    /** 参数校验异常（@Valid 触发） */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBind(BindException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = fe == null ? "参数错误" : fe.getField() + " " + fe.getDefaultMessage();
        return R.fail(msg);
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception e) {
        e.printStackTrace();
        return R.fail("服务器异常: " + e.getMessage());
    }
}
