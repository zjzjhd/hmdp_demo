package com.zjzjhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjzjhd.dto.DishDto;
import com.zjzjhd.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品,同时插入菜品对应的口味数据,需要同时操作两张表:dish  dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品和口味信息
    void updateWithFlavor(DishDto dishDto);

    //批量起售或停售
    void updateByBatch(Integer status, List<Long> ids);

    //批量删除


    void deleteByIds(List<Long> ids);
}
