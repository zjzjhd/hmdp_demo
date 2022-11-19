package com.zjzjhd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjzjhd.entity.Employee;
import com.zjzjhd.entity.User;
import com.zjzjhd.mapper.EmployeeMapper;
import com.zjzjhd.mapper.UserMapper;
import com.zjzjhd.service.EmployeeService;
import com.zjzjhd.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/13 11:43
 */
@Service						//这两个泛型一个是实体类对应的mapper,一个是实体类
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
