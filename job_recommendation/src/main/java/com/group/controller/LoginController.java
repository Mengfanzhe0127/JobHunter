package com.group.controller;

import com.group.pojo.Employer;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.EmployerService;
import com.group.service.UserService;
import com.group.utils.JwtUtils;
import com.group.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class LoginController {
    @Autowired
    private UserService userService; //求职者服务

    @Autowired
    private EmployerService employerService; //招聘者服务类

//    @Autowired
//    private RedisUtils redisUtil;

    @CrossOrigin(origins = "*")
    @PostMapping("/login/{phoneOrEmail}/{password}")
    public Result login(@PathVariable byte[] phoneOrEmail,@PathVariable byte[] password) {
        User u = userService.userLogin(phoneOrEmail,password);
        //登陆成功：生成并下发令牌
        if(u != null) {
            log.info("求职者登录:{}",u);
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",u.getId());
//            claims.put("nickname",u.getNickname());

            //生成新令牌
            String jwt = JwtUtils.generateJwt(claims);
//            //将令牌存储金Redis
//            redisUtil.setToken(u.getId().toString(),jwt);

            Map<String,Object> userLoginMap = new HashMap<>();
            userLoginMap.put("token",jwt);
            //由于前端判断
            userLoginMap.put("type",0);

            //返回给前端id,用于用户个人信息查询和编辑
            String userId = u.getId().toString();//字符串
            userLoginMap.put("id",userId);

            return Result.success(userLoginMap);
        }

        Employer e = employerService.employerLogin(phoneOrEmail,password);
        //登陆成功：生成并下发令牌
        if(e != null) {
            log.info("招聘者登录:{}",e);
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",e.getId());
//            claims.put("nickname",e.getNickname());

            String jwt = JwtUtils.generateJwt(claims);
            Map<String,Object> employerLoginMap = new HashMap<>();
            employerLoginMap.put("token",jwt);
            //用于前端判断
            employerLoginMap.put("type",1);

            //返回给前端id,用于用户个人信息查询和编辑
            String eId = e.getId().toString();
            employerLoginMap.put("id",eId);

            return Result.success(employerLoginMap);
        }

        //无相关账号或令牌过期
        return Result.error("无相关账号或令牌过期");
    }




}
