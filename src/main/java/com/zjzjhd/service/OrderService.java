package com.zjzjhd.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.zjzjhd.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;

public interface OrderService extends IService<Orders> {



    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
