package com.zjzjhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
