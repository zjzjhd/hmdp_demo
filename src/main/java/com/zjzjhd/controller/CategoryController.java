package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjzjhd.common.R;
import com.zjzjhd.entity.Category;
import com.zjzjhd.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 分类管理
 * @date 2022/11/14 19:54
 */

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * @description: 新增套餐分类
     * @param: category
     * @return: com.zjzjhd.common.R<java.lang.String>
     */
    @PostMapping()
    public R<String> add(@RequestBody Category category) {

        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * @description: 新增分类
     * @param: page
     * @param: pageSize
     * @return: com.zjzjhd.common.R<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     */

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //这里之所以是返回page对象(mybatis-plus的page对象)，是因为前端需要这些分页的数据(比如当前页，总页数)
        //在编写前先测试一下前端传过来的分页数据有没有被我们接受到

        //构造分页构造器  就是page对象
        //Page pageInfo = new Page(page, pageSize);
        Page categoryPage = new Page(page, pageSize);
        //构造条件构造器  就是动态的封装前端传过来的过滤条件  记得加泛型
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加一个排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询  这里不用封装了mybatis-plus帮我们做好了
        categoryService.page(categoryPage, queryWrapper);

        return R.success(categoryPage);
    }

    /**
     * @description: 删除分类信息
     * @param: ids
     * @return: com.zjzjhd.common.R<java.lang.String>
     */

    @DeleteMapping()
    public R<String> delete(@RequestParam("ids") Long ids) { //注意这里前端传过来的数据是ids

        categoryService.remove(ids);
        return R.success("分类信息删除成功");

    }
    /** 
     * @description: 根据Id修改分类信息
     * @param: category 
     * @return: com.zjzjhd.common.R<java.lang.String> 
     */ 

    @PutMapping()
    public R<String> update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /** 
     * @description: 根据条件查询分类数据
     * @param: category 
     * @return: com.zjzjhd.common.R<java.util.List<com.zjzjhd.entity.Category>> 
     */ 
    @GetMapping("/list")
    public  R<List<Category>> list(Category category){
        //构建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);

    }

}
