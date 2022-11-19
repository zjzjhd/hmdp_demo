package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.common.CustomException;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Dish;
import com.zjzjhd.entity.Setmeal;
import com.zjzjhd.entity.SetmealDish;
import com.zjzjhd.mapper.CategoryMapper;
import com.zjzjhd.mapper.SetmealDishMapper;
import com.zjzjhd.service.CategoryService;
import com.zjzjhd.service.DishService;
import com.zjzjhd.service.SetmealDishService;
import com.zjzjhd.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/14 19:51
 */
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

}