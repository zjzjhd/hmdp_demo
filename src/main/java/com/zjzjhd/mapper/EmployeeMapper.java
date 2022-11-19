package com.zjzjhd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjzjhd.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
