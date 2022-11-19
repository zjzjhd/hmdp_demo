package com.zjzjhd.filter;

import com.alibaba.fastjson.JSON;
import com.zjzjhd.common.BaseContext;
import com.zjzjhd.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zjzjhd
 * @version 1.0
 * @description: 检查用户是否完成登录
 * @date 2022/11/13 17:25
 */

/*
 * filterName过滤器名字
 * urlPatterns拦截的请求，这里是拦截所有的请求
 */
@WebFilter(filterName = "LongCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURL = request.getRequestURI();

        //定义不需要处理的请求路径  比如静态资源(静态页面我们不需要拦截,因为此时的静态页面是没有数据的)
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURL);

        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
        //4-1、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            //把用户id存储到本地的threadLocal
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setThreadLocal(empId);

            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //4-2、判断用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {
            //把用户id存储到本地的threadLocal
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setThreadLocal(userId);

            //放行
            filterChain.doFilter(request, response);
            return;
        }

        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据,具体响应什么数据，看前端的需求，然后前端会根据登陆状态做页面跳转
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    @Override
    public void destroy() {

    }

    /**
     * @description: 把浏览器发过来的请求和我们定义的不拦截的url做比较，匹配则放行
     * @param: urls
     * @param: requestURL
     * @return: boolean
     */
    public boolean check(String[] urls, String requestURL) {
        for (String url : urls) {
            //把浏览器发过来的请求和我们定义的不拦截的url做比较，匹配则放行

            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match) {
                return true;
            }
        }
        return false;
    }
}

