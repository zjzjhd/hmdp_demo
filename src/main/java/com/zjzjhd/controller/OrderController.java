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
import org.apache.commons.lang.StringUtils;
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
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageDto = new Page<>(page, pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //分页查询
        orderService.page(pageInfo, queryWrapper);

        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) -> {
            OrderDto orderDto = new OrderDto();

            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            Long orderId = Long.valueOf(item.getNumber());//获取订单id

            List<OrderDetail> orderDetailList = getOrderDetailListByOrderId(orderId);

            BeanUtils.copyProperties(item, orderDto);

            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);

            return orderDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }

    private List<OrderDetail> getOrderDetailListByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        return orderDetailList;
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number, String beginTime, String endTime) {
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //添加查询条件  动态sql  字符串使用StringUtils.isNotEmpty这个方法来判断
        //这里使用了范围查询的动态SQL，这里是重点！！！
        queryWrapper.like(number != null, Orders::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);

        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * @description: 订单派送，订单完成操作 在后端进行操作
     * @param: [orders]
     * @return: R<Orders>
     */
    @PutMapping
    public R<Orders> dispatch(@RequestBody Orders orders) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(orders.getId() != null, Orders::getId, orders.getId());
        Orders one = orderService.getOne(queryWrapper);

        one.setStatus(orders.getStatus());
        orderService.updateById(one);
        return R.success(one);
    }

}
