package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zjzjhd.entity.ShoppingCart;
import com.zjzjhd.mapper.ShoppingCartMapper;
import com.zjzjhd.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
