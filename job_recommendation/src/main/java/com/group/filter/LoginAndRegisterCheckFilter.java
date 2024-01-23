package com.group.filter;

import com.alibaba.fastjson.JSONObject;
import com.group.pojo.Result;
import com.group.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginAndRegisterCheckFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        System.out.println("初始化方法执行");
//    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String url = req.getRequestURL().toString();
        log.info("请求的url：{}",url);

        //登陆操作不需要先判断是否含有令牌
        if(url.contains("login")||url.contains("register")) {
            log.info("注册或登陆成功，放行");
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        //获取请求头中的token
        String jwt = req.getHeader("token");

        if(!StringUtils.hasLength(jwt)) {
            log.info("请求头token为空，返回未登陆信息");
            Result error = Result.error("NOT_LOGIN");

            //无RestController，需要进行json格式转换
            String notLogin = JSONObject.toJSONString(error);

            //响应给浏览器
            resp.getWriter().write(notLogin);
            return ; // void
        }

        try{
            //parse即为解析过程
            JwtUtils.parseJWT(jwt);
        }catch(Exception e) {
            e.printStackTrace();
            log.info("解析令牌失败，返回解析前错误信息");

            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return ;
        }

        log.info("令牌合法，放行");
        filterChain.doFilter(servletRequest,servletResponse);
    }

//    @Override
//    public void destroy() {
//        System.out.println("销毁方法执行");
//    }
}
