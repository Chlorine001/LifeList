package com.lifelist.common;

import lombok.Data;

/**
 * 统一返回体
 * 所有接口都返回这个结构，前端好处理：
 * { "code": 0, "message": "ok", "data": ... }
 * code=0 表示成功，非 0 表示失败
 */
@Data
public class R<T> {

    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.message = "ok";
        r.data = data;
        return r;
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> fail(int code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        return r;
    }

    public static <T> R<T> fail(String message) {
        return fail(1, message);
    }
}
