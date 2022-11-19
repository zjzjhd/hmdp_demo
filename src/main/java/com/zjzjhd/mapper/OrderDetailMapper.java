package com.zjzjhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjzjhd.entity.OrderDetail;
import com.zjzjhd.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
