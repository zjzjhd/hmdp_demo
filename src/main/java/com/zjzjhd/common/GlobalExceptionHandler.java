package com.zjzjhd.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/13 21:29
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class}) //表示拦截哪些类型的controller注解
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * @description: id重复异常处理方法
     * @param: exception
     * @return: R
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException exception) {
        String error;
        error = exception.getMessage();
        log.error(error);//报错记得打日志
        if (error.contains("Duplicate entry")) {
            /*
            错误信息包含重复录入
             */
            String[] split = error.split(" ");
            String msg = split[2] + "这个用户名已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");

    }

    /**
     * @description: 菜品关联删除异常处理方法
     * @param: exception
     * @return: R
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException exception) {
        String error;
        error = exception.getMessage();
        log.error(error);//报错记得打日志
        return R.error(error);
    }
}
