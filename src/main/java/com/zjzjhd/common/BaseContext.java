package com.zjzjhd.common;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 * @date 2022/11/14 17:46
 */
public class BaseContext {
    //用来存储用户id
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
/** 
 * @description:  设置值
 * @param: id 
 * @return: void 
 */ 
    public static void setThreadLocal(long id){
        threadLocal.set(id);
    }
/** 
 * @description: 获取值
 * @param:  
 * @return: java.lang.Long 
 */ 
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
