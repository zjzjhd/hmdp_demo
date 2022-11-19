package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.common.CustomException;
import com.zjzjhd.dto.SetmealDto;
import com.zjzjhd.entity.Setmeal;
import com.zjzjhd.entity.SetmealDish;
import com.zjzjhd.mapper.SetmealMapper;
import com.zjzjhd.service.SetmealDishService;
import com.zjzjhd.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/14 19:51
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * @description: 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param: [setmealDto]
     * @return: void
     */
    @Transactional  // 同时操作两个表，为保证数据一致性，需要开启事务
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存套餐和菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //批量保存
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * @description: 删除套餐，同时需要删除套餐和菜品的关联关系
     * @param: [ids]
     * @return: void
     */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {

        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.in(Setmeal::getId, ids);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
        }
        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //removeByIds需要主键Id
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        //删除关系表的数据setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * @description: 批量更新状态
     * @param: [status, ids]
     * @return: void
     */
    @Override
    public void updateByBatch(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        List<Setmeal> list = setmealService.list(queryWrapper);
        if (list == null) return;
        for (Setmeal setmeal : list) {
            if (setmeal != null) {
                setmeal.setStatus(status);
                setmealService.updateById(setmeal);
            }
        }
    }
}
