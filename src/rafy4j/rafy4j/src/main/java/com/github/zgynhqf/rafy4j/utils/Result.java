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
public class Result<T>  extends HashMap<String, Object> {
    /// <summary>
    /// A string message used by the success result.
    /// </summary>
    public static String SuccessMessage = "操作成功！";

    /// <summary>
    /// A string message used by the failed result.
    /// </summary>
    public static String FailedMessage = "操作失败！";

    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    private Result() {
    }

    /// <summary>
    /// Message=String.Empty
    /// </summary>
    /// <param name="success"></param>
    public Result(boolean success) {
        this.success = success;
        statusCode = 0;
        message = success ? SuccessMessage : FailedMessage;
        data = null;
    }

    /// <summary>
    /// Success = false
    /// </summary>
    /// <param name="message"></param>
    public Result(String message) {
        success = false;
        statusCode = 0;
        this.message = message;
        data = null;
    }

    /// <summary>
    /// create a error message with its status.
    /// </summary>
    /// <param name="statusCode"></param>
    public Result(int statusCode) {
        success = false;
        this.statusCode = statusCode;
        message = FailedMessage;
        data = null;
    }

    /// <summary>
    /// create a successful result with corresponding data.
    /// </summary>
    /// <param name="data">The data.</param>
    public Result(T data) {
        success = true;
        statusCode = 0;
        message = SuccessMessage;
        this.data = data;
    }

    /// <summary>
    /// create a result with its message.
    /// </summary>
    /// <param name="success"></param>
    /// <param name="message"></param>
    public Result(boolean success, String message) {
        this.success = success;
        statusCode = 0;
        this.message = message;
        data = null;
    }

    /// <summary>
    /// create a result by specifying its success status and a message.
    /// </summary>
    /// <param name="success"></param>
    /// <param name="statusCode"></param>
    public Result(boolean success, int statusCode) {
        this.success = success;
        this.statusCode = statusCode;
        message = success ? SuccessMessage : FailedMessage;
        data = null;
    }

    /// <summary>
    /// create a failed result by its statusCode and a error message.
    /// </summary>
    /// <param name="statusCode"></param>
    /// <param name="message"></param>
    public Result(int statusCode, String message) {
        success = false;
        this.statusCode = statusCode;
        this.message = message;
        data = null;
    }

    /// <summary>
    /// create a result by specifing all its status.
    /// </summary>
    /// <param name="success"></param>
    /// <param name="statusCode"></param>
    /// <param name="message"></param>
    public Result(boolean success, int statusCode, String message) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        data = null;
    }

    /// <summary>
    /// Indicates whether this instance's StatusCode and a specified object's StatusCode are equal.
    /// </summary>
    /// <param name="another"></param>
    /// <returns></returns>
    public boolean statusEquals(Result another) {
        return success == another.success && statusCode == another.statusCode;
    }

    /// <summary>
    /// Reset this result to a unsuccessful status.
    /// </summary>
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

    public static Result data(Object data) {
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
