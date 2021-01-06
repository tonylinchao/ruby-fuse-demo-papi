package com.hkt.ruby.fuse.demo.utils;

import lombok.Data;
import org.apache.camel.Exchange;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * General class for response body
 *
 * @author Tony C Lin
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private boolean success;
    private T data;
    private String msg;

    private R(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMessage());
    }

    private R(IResultCode resultCode, String msg) {
        this(resultCode, null, msg);
    }

    private R(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMessage());
    }

    private R(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = ResultCode.SUCCESS.code == code;
    }

    public static boolean isSuccess(R<?> result) {
        return (Boolean)Optional.ofNullable(result).map((x) -> {
            return ObjectUtils.nullSafeEquals(ResultCode.SUCCESS.code, x.code);
        }).orElse(Boolean.FALSE);
    }

    public static boolean isNotSuccess(R<?> result) {
        return !isSuccess(result);
    }

    public static <T> R<T> data(T data) {
        return data(data, "SUCCESS");
    }

    public static <T> R<T> data(T data, String msg) {
        return data(200, data, msg);
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R(code, data, data == null ? "NULL DATA" : msg);
    }

    public static <T> R<T> data(Exchange result, T data, String msg) {
        int code = Integer.parseInt(result.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString());
        return new R(code, data, data == null ? "NULL DATA" : msg);
    }

    public static <T> R<T> success(String msg) {
        return new R(ResultCode.SUCCESS, msg);
    }

    public static <T> R<T> success(IResultCode resultCode) {
        return new R(resultCode);
    }

    public static <T> R<T> success(IResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> fail(String msg) {
        return new R(ResultCode.FAILURE, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R(code, (Object)null, msg);
    }

    public static <T> R<T> fail(IResultCode resultCode) {
        return new R(resultCode);
    }

    public static <T> R<T> fail(IResultCode resultCode, String msg) {
        return new R(resultCode, msg);
    }

    public static <T> R<T> status(boolean flag) {
        return flag ? success("SUCCESS") : fail("Failed");
    }

}
