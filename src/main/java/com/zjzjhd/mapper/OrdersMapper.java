package com.zjzjhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjzjhd.entity.AddressBook;
import com.zjzjhd.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
