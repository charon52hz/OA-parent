package org.chz.common.config.exception;

import org.chz.result.Result;
import org.chz.result.ResultCodeNum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)  //全局异常处理，执行的方法
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail().message("全局异常处理");
    }


    @ExceptionHandler(ArithmeticException.class)    //算术异常
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("特定异常处理");
    }

    @ExceptionHandler(MyException.class)    //自定义异常
    @ResponseBody
    public Result error(MyException e){
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }

    /**
     * spring security异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.build(null, ResultCodeNum.PERMISSION);
    }
}
