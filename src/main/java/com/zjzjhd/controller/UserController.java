package com.zjzjhd.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjzjhd.common.R;
import com.zjzjhd.entity.User;
import com.zjzjhd.service.UserService;
import com.zjzjhd.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: TODO
 * @date 2022/11/18 17:24
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            // 生成验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4).toString();
            log.info("瑞吉外卖验证码：code为：" + code);
            // 调用阿里云短信服务API完成发送短信
            // SMSUtils.sendMessage("瑞吉外卖","",phone,validateCode4String);
            // 将生成的验证码保存
            session.setAttribute(phone, code);
            return R.success("短信发送成功");
        }
        return R.error("短信发送失败");

    }

    /**
     * @description: 移动端用户登录
     * @param: [user, session]
     * @return: R<String>
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.values().toString());
        //获取手机号
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        String codeInSession = session.getAttribute(phone).toString();
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && code != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            //判断是否为新用户，如果不是则自动注册
            if (user == null) {
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1); //可设置也可不设置，因为数据库我们设置了默认值
                userService.save(user);
            }
            session.setAttribute("user", user.getId()); //在session中保存用户的登录状态,这样才过滤器的时候就不会被拦截了
            return R.success(user);
        }
        return R.error("登录失败");

    }

}
