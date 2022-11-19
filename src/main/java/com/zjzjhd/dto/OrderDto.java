package com.zjzjhd.dto;

import com.zjzjhd.entity.OrderDetail;
import com.zjzjhd.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/19 13:22
 */

@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}