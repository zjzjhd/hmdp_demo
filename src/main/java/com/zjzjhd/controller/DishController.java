package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjzjhd.common.R;
import com.zjzjhd.dto.DishDto;
import com.zjzjhd.entity.Category;
import com.zjzjhd.entity.Dish;
import com.zjzjhd.entity.DishFlavor;
import com.zjzjhd.service.CategoryService;
import com.zjzjhd.service.DishFlavorService;
import com.zjzjhd.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/15 13:47
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * @description: 新增菜品
     * @param: dishDto
     * @return: com.zjzjhd.common.R<java.lang.String>
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * @description:` 菜品信息分页查询
     * @param: page
     * @param: pageSize
     * @param: name
     * @return: com.zjzjhd.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造一个分页构造器对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //上面对dish泛型的数据已经赋值了，这里对DishDto我们可以把之前的数据拷贝过来进行赋值

        //构造一个条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件 注意判断是否为空  使用对name的模糊查询
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件  根据更新时间降序排
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //去数据库处理分页 和 查询
        dishService.page(dishPage, queryWrapper);

        //获取到dish的所有数据 records属性是分页插件中表示分页中所有的数据的一个集合
        List<Dish> records = dishPage.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            //对实体类DishDto进行categoryName的设值

            DishDto dishDto = new DishDto();
            //这里的item相当于Dish  对dishDto进行除categoryName属性的拷贝
            BeanUtils.copyProperties(item, dishDto);
            //获取分类的id
            Long categoryId = item.getCategoryId();
            //通过分类id获取分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //设置实体类DishDto的categoryName属性值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //对象拷贝  使用框架自带的工具类，第三个参数是不拷贝到属性
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        dishDtoPage.setRecords(list);
        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
        //所以进行了上面的一系列操作
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {//这里返回什么数据是要看前端需要什么数据,不能直接想当然的就返回Dish对象

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * @description: 更新dish表的基本信息
     * @param: dishDto
     * @return: R<String>
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {

        //更新dish表的基本信息

        dishService.updateWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * @description: 批量修改停售或起售
     * @param: status
     * @param: ids
     * @return: R<String>
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatusByBatch(@PathVariable Integer status, @RequestParam("ids") List<Long> ids) {
        dishService.updateByBatch(status, ids);
        return R.success("售卖状态修改成功");
    }

    /**
     * @description: 套餐批量删除和单个删除
     * @param: [ids]
     * @return: R<String>
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        //删除菜品  这里的删除是逻辑删除
        dishService.deleteByIds(ids);
        return R.success("菜品删除成功");

    }

//    /**
//     * @description: 根据条件查询分类数据
//     * @param: category
//     * @return: com.zjzjhd.common.R<java.util.List < com.zjzjhd.entity.Category>>
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Long categoryId) {
//        //构建条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加条件
//        queryWrapper.in(Dish::getCategoryId, categoryId);
//        //查询状态为1
//        queryWrapper.eq(Dish::getStatus, 1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//
//    }

    /**
     * @description: 根据条件查询分类数据
     * @param: category
     * @return: com.zjzjhd.common.R<java.util.List < com.zjzjhd.entity.Category>>
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId) {
        //构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.in(Dish::getCategoryId, categoryId);
        //查询状态为1
        queryWrapper.eq(Dish::getStatus, 1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //通过分类id获取分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //设置实体类DishDto的categoryName属性值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //设置口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);

            dishDto.setFlavors(dishFlavorService.list(lambdaQueryWrapper));
            return dishDto;

        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }

}
