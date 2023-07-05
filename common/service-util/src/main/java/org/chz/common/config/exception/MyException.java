package org.chz.common.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chz.result.ResultCodeNum;

@Data

public class MyException extends RuntimeException{
    private Integer code;   //状态码
    private String msg; //描述信息

    public MyException(Integer code,String msg){
        super();
        this.code = code;
        this.msg = msg;
    }

}
