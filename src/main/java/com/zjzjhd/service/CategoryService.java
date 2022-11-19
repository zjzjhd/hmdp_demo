package com.zjzjhd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjzjhd.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(long id);
}
