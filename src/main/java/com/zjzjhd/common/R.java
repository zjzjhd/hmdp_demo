package com.zjzjhd.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 通用返回结果类，服务端响应的数据最终都会封装成此对象
 * @date 2022/11/13 11:51
 */
@Data
public class R<T> {
    private Integer code; //编码：1成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据
    private Map map = new HashMap(); //动态数据

    /**
     * @description: 当响应成功时，返回一个R对象
     * @param: object
     * @return: R<T>
     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    /**
     * @description: 当响应失败时，返回一段message
     * @param: msg
     * @return: R<T>
     */
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
