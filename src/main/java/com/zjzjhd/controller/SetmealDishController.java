package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjzjhd.common.R;
import com.zjzjhd.dto.SetmealDto;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Setmeal;
import com.zjzjhd.service.CategoryService;
import com.zjzjhd.service.SetmealDishService;
import com.zjzjhd.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/16 16:43
 */

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealDishController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//所有缓存都清除
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("信息 {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("信息保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造一个分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        //构造一个条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件 注意判断是否为空  使用对name的模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件  根据更新时间降序排
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //去数据库处理分页 和 查询
        setmealService.page(pageInfo, queryWrapper);

//        获取查询数据列表
        List<Setmeal> records = pageInfo.getRecords();
        //设置套餐分类
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        setmealDtoPage.setRecords(list);

        return R.success(setmealDtoPage);
    }

    /**
     * @description: 套餐批量删除和单个删除
     * @param: [ids]
     * @return: R<String>
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//所有缓存都清除
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        //删除菜品  这里的删除是逻辑删除
        setmealService.deleteByIds(ids);
        return R.success("菜品删除成功");

    }

    @PostMapping("/status/{status}")
    public R<String> updateStatusByBatch(@PathVariable Integer status, @RequestParam("ids") List<Long> ids) {
        setmealService.updateByBatch(status, ids);
        return R.success("售卖状态修改成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> list(@RequestParam long categoryId, int status) {
        log.info("id = {}", categoryId);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(Setmeal::getStatus, status);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);

    }

}
