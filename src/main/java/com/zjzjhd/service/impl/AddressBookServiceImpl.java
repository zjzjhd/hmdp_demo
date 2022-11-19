package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.entity.AddressBook;
import com.zjzjhd.entity.Category;
import com.zjzjhd.mapper.AddressBookMapper;
import com.zjzjhd.mapper.CategoryMapper;
import com.zjzjhd.service.AddressBookService;
import com.zjzjhd.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/18 19:27
 */

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {


}
