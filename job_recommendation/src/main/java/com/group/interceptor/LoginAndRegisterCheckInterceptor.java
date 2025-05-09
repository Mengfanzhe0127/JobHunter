package com.group.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.group.pojo.Result;
import com.group.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: mfz
 * @Date: 2024/04/10/19:30
 * @Description:
 */
@Component
@Slf4j
public class LoginAndRegisterCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
//        System.out.println("preHandle...");
//        HttpServletRequest req = (HttpServletRequest) servletRequest;
//        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        //检查机器学习脚本的请求
        String mlRequestHeader = req.getHeader("X-ML-Request");
        boolean isMlRequest = "true".equals(mlRequestHeader);

        String url = req.getRequestURL().toString();
        log.info("请求的url：{}",url);

        //登陆，注册操作不需要先判断是否含有令牌
        if(url.contains("login")||url.contains("register")||url.contains("webSocket")||url.contains("websocket")
        ||url.contains("ms")) {
            log.info("注册或登陆成功，放行");
//            filterChain.doFilter(servletRequest,servletResponse);
//            return;
            return true;
        }else if (isMlRequest){
            log.info("机器学习脚本被调用");
//            filterChain.doFilter(servletRequest,servletResponse);
//            return;
            return true;
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
//            return ; // void
            return false;
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
            return false;
        }

        log.info("令牌合法，放行");
//        filterChain.doFilter(servletRequest,servletResponse);
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle...");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion...");
    }
}
