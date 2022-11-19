package com.zjzjhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjzjhd.dto.SetmealDto;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐  同时保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    void deleteByIds(List<Long> ids);

    void updateByBatch(Integer status, List<Long> ids);
}
