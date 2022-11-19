package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.common.CustomException;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Dish;
import com.zjzjhd.entity.Setmeal;
import com.zjzjhd.mapper.CategoryMapper;
import com.zjzjhd.service.CategoryService;
import com.zjzjhd.service.DishService;
import com.zjzjhd.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/14 19:51
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    /**
     * @description: 根据id删除分类，删除之前进行判断
     * @param: ids
     * @return: void
     */
    @Override
    public void remove(long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件 根据id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联，直接抛出一个业务异常
        if (dishCount > 0) {
            throw new CustomException("当前分类项关联了菜品,不能删除");
    //已经关联了菜品，抛出一个业务异常
        }
        //查询当前分类是否关联了套餐，如果已经管理，直接抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件 根据id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count();
        if (setmealCount > 0) {
        //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类项关联了套餐,不能删除");
        }

        //正常删除
    }
}
