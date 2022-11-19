package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zjzjhd.entity.OrderDetail;
import com.zjzjhd.mapper.OrderDetailMapper;
import com.zjzjhd.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}