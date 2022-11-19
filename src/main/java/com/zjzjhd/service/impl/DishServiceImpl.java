package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.common.CustomException;
import com.zjzjhd.dto.DishDto;
import com.zjzjhd.entity.Dish;
import com.zjzjhd.entity.DishFlavor;
import com.zjzjhd.entity.SetmealDish;
import com.zjzjhd.mapper.DishMapper;
import com.zjzjhd.service.DishFlavorService;
import com.zjzjhd.service.DishService;
import org.springframework.beans.BeanUtils;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;

    /**
     * @description: 新增菜品同时保存对应的口味数据
     * @param: dishDto
     * @return: void
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish中
        this.save(dishDto);
        Long dishId = dishDto.getId();//菜品Id

        //为了把dishId  set进flavors表中
        //拿到菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //这里对集合进行赋值 可以使用循环或者是stream流
        flavors = flavors.stream().map((item) -> {
            //拿到的这个item就是这个DishFlavor集合
            item.setDishId(dishId);
            return item; //记得把数据返回去
        }).collect(Collectors.toList()); //把返回的集合搜集起来,用来被接收

        //把菜品口味的数据到口味表 dish_flavor  注意dish_flavor只是封装了name value 并没有封装dishId(从前端传过来的数据发现的,然而数据库又需要这个数据)
        dishFlavorService.saveBatch(dishDto.getFlavors()); //这个方法是批量保存

    }

    /**
     * @description: 根据id来查询菜品信息和对应的口味信息
     * @param: id
     * @return: com.zjzjhd.dto.DishDto
     */

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息  从dish表查询
        Dish dish = this.getById(id);
        //查询当前菜品对应的口味信息,从dish_flavor查询  条件查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;

    }
    /**
     * @description: 
     * @param: [dishDto]
     * @return: void
     */

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息  因为这里的dishDto是dish的子类
        this.updateById(dishDto);

        //更新口味信息---》先清理再重新插入口味信息
        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //这里对集合进行赋值 可以使用循环或者是stream流
        flavors = flavors.stream().map((item) -> {
            //拿到的这个item就是这个DishFlavor集合
            item.setDishId(dishDto.getId());
            return item; //记得把数据返回去
        }).collect(Collectors.toList()); //把返回的集合搜集起来,用来被接收

        dishFlavorService.saveBatch(flavors);//可能有多种口味

    }

    @Override
    public void updateByBatch(Integer status, List<Long> ids) {
        //每一次都要查询表
//        for (long id:ids) {
//            Dish dish = dishService.getById(id);
//            if (dish != null) {
//                dish.setStatus(status);
//                dishService.updateById(dish);
//            }
//        }
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);
        //根据数据进行批量查询
        List<Dish> list = dishService.list(queryWrapper);

        for (Dish dish : list) {
            if (dish != null) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids != null, Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
        }
        //先删除套餐表中的数据
        this.removeByIds(ids);

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getCategoryId, ids);
        //删除关系表的数据
        dishService.remove(lambdaQueryWrapper);
    }
}




