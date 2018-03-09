package com.core.common.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@ApiModel(value = "ResponseResult", discriminator = "ͨ通用返回信息", subTypes = {ResponseResult.class})
public class ResponseResult<T> {
    @ApiModelProperty("状态码")
    private int code;
    @ApiModelProperty("返回描述")
    private String msg;
    @ApiModelProperty("返回对象")
    private T result;

        public static <T> ResponseResult<T> success() {
        return new ResponseResultBuilder<T>().code(HttpStatus.OK.value()).msg("success").build();
    }

    public static <T> ResponseResult<T> success(T result) {
        return new ResponseResultBuilder<T>().code(HttpStatus.OK.value()).msg("success").result(result).build();
    }

    public static <T> ResponseResult<T> success(T result, String msg) {
        return new ResponseResultBuilder<T>().code(HttpStatus.OK.value()).msg(msg).result(result).build();
    }

    public static <T> ResponseResult<T> fail() {
        return new ResponseResultBuilder<T>().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
    }

    public static <T> ResponseResult<T> fail(Exception e) {
        return new ResponseResultBuilder<T>().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).msg(e.getMessage().toString()).build();
    }

    public static <T> ResponseResult<T> fail(String msg) {
        return new ResponseResultBuilder<T>().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).msg(msg).build();
    }

    public static <T> ResponseResult<T> fail(Integer code, String msg) {
        return new ResponseResultBuilder<T>().code(code).msg(msg).build();
    }

    public static String buildSuccessResultStr(Object result) {
        if (null == result) {
            return "{\"msg\": \"success\",\"code\": " + HttpStatus.OK.value() + ",\"result\":  " + result + "}";
        }
        return "{\"msg\": \"success\",\"code\": " + HttpStatus.OK.value() + ",\"result\": \"" + result + "\"}";
    }

}
