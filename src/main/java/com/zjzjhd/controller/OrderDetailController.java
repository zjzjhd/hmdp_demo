package com.zjzjhd.controller;

import com.zjzjhd.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/19 12:34
 */
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private  OrderDetailService orderDetailService;
}
