package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjzjhd.common.BaseContext;
import com.zjzjhd.common.R;
import com.zjzjhd.dto.OrderDto;
import com.zjzjhd.entity.OrderDetail;
import com.zjzjhd.entity.Orders;
import com.zjzjhd.service.OrderDetailService;
import com.zjzjhd.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/19 12:34
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(@RequestParam int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageDto = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);



        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();

            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = item.getId();//获取订单id

            List<OrderDetail> orderDetailList = this.getOrderDetailListByOrderId(orderId);

            BeanUtils.copyProperties(item, orderDto);

            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);

            return orderDto;
        }).collect(Collectors.toList());

        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    private List<OrderDetail> getOrderDetailListByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;

    }

}
