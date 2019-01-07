package com.github.zgynhqf.rafy4j.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

/**
 * @author: huqingfang
 * @date: 2018-11-30 21:33
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class Result<T> extends HashMap<String, Object> {
    /**
     * A string message used by the success result.
     */
    public static String SuccessMessage = "操作成功！";

    /**
     * A string message used by the failed result.
     */
    public static String FailedMessage = "操作失败！";

    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    private Result() {
    }

    /**
     * create a Result with success status.
     * @param success
     */
    public Result(boolean success) {
        this.success = success;
        statusCode = 0;
        message = success ? SuccessMessage : FailedMessage;
        data = null;
    }

    /**
     * create a failed Result.
     * @param message
     */
    public Result(String message) {
        success = false;
        statusCode = 0;
        this.message = message;
        data = null;
    }

    /**
     * create a error message with its status.
     * @param statusCode
     */
    public Result(int statusCode) {
        success = false;
        this.statusCode = statusCode;
        message = FailedMessage;
        data = null;
    }

    /**
     * create a successful result with corresponding data.
     * @param data
     */
    public Result(T data) {
        success = true;
        statusCode = 0;
        message = SuccessMessage;
        this.data = data;
    }

    /**
     * create a result with its message.
     * @param success
     * @param message
     */
    public Result(boolean success, String message) {
        this.success = success;
        statusCode = 0;
        this.message = message;
        data = null;
    }

    /**
     * create a result by specifying its success status and a message.
     * @param success
     * @param statusCode
     */
    public Result(boolean success, int statusCode) {
        this.success = success;
        this.statusCode = statusCode;
        message = success ? SuccessMessage : FailedMessage;
        data = null;
    }

    /**
     * create a failed result by its statusCode and a error message.
     * @param statusCode
     * @param message
     */
    public Result(int statusCode, String message) {
        success = false;
        this.statusCode = statusCode;
        this.message = message;
        data = null;
    }

    /**
     * create a result by specifing all its status.
     * @param success
     * @param statusCode
     * @param message
     */
    public Result(boolean success, int statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        data = null;
    }

    /**
     * Indicates whether this instance's StatusCode and a specified object's StatusCode are equal.
     * @param another
     * @return
     */
    public boolean statusEquals(Result another) {
        return success == another.success && statusCode == another.statusCode;
    }

    /**
     * Reset this result to a unsuccessful status.
     */
    public void reset() {
        success = false;
        statusCode = 0;
        message = "";
    }

    public static Result ok() {
        return ok(SuccessMessage);
    }

    public static Result ok(String msg) {
        Result r = new Result();
        r.setSuccess(true);
        r.setStatusCode(1);
        r.setMessage(msg);
        return r;
    }

    public static <T> Result<T> data(T data) {
        Result r = Result.ok();
        r.setData(data);
        return r;
    }

    public static Result error() {
        return error(FailedMessage);
    }

    public static Result error(String msg) {
        return error(0, msg);
    }

    public static Result error(int code, String msg) {
//        if(code == 0) throw new InvalidParameterException("code");

        Result r = new Result();
        r.setStatusCode(code);
        r.setSuccess(false);
        r.setMessage(msg);
        return r;
    }
}
