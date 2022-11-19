package com.zjzjhd.common;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 自定义业务异常
 * @date 2022/11/14 21:38
 */
public class CustomException extends RuntimeException {

    /** 
     * @description: 把异常信息传进来 
     * @param: message 
     * @return:  
     */ 
    public CustomException(String message) {
        super(message);
    }

}
