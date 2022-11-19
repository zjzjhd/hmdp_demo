package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjzjhd.common.R;
import com.zjzjhd.entity.Employee;
import com.zjzjhd.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/13 11:46
 */
@Slf4j
@RestController
/*
@RestController是让类注入Spring boot容器，并实例化。
 并让这个类中所有的api接口返回的数据，都以Json字符串返回客户端
 */

@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * @description: 员工登录
     * @param: request
     * @param: employee
     * @return: R<Employee>
     */
    @PostMapping("/login") //使用restful风格开发
    public R<Employee> Login(HttpServletRequest request, @RequestBody Employee employee) {
        //这里为什么还有接收一个request对象的数据?
        //登陆成功后，我们需要从请求中获取员工的id，并且把这个id存到session中
        //这样我们想要获取登陆对象的时候就可以随时获取

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();//从前端用户登录拿到的用户密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());//对用户密码进行加密

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("用户不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码不正确");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() != 1) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中的用户id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */

    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //对新增的员工设置初始化密码123456,需要进行md5加密处理，后续员工可以直接修改密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //获得当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setCreateUser(empId); //创建人的id,就是当前用户的id（在进行添加操作的id）
//        employee.setUpdateUser(empId);//最后的更新人是谁

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * @param page     当前页数
     * @param pageSize 当前页最多存放数据条数,就是这一页查几条数据
     * @param name     根据name查询员工的信息
     * @description: 员工信息分页
     * @return:
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //这里之所以是返回page对象(mybatis-plus的page对象)，是因为前端需要这些分页的数据(比如当前页，总页数)
        //在编写前先测试一下前端传过来的分页数据有没有被我们接受到
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);


        //构造条件构造器  就是动态的封装前端传过来的过滤条件  记得加泛型
        Page pageInfo = new Page(page, pageSize);
        //根据条件查询  注意这里的条件是不为空
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询  这里不用封装了mybatis-plus帮我们做好了
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);


    }


    /**
     * @param employee
     * @return
     * @description: 根据id修改员工信息
     */
    @PutMapping()
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //获取管理员id

        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        //更新
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * @description: 根据前端传过来的员工id查询数据库进行数据会显给前端
     * @param: id
     * @return:
     */

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到该员工信息");
    }


}
