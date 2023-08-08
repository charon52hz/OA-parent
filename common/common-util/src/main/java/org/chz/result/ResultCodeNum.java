package org.chz.result;

import lombok.Getter;

@Getter
public enum ResultCodeNum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    LOGIN_ERROR(210,"登录认证失败");

    private Integer code;
    private String message;
    private ResultCodeNum(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
