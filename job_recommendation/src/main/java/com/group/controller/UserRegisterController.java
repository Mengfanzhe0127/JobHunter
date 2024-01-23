package com.group.controller;

import com.group.pojo.Result;
import com.group.service.UserService;
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
public class UserRegisterController {
    @Autowired
    private UserService userService;

    @CrossOrigin(origins = "*")
    @PostMapping("/user/register/{name}/{email}/{phone}/{password}")
    public Result userRegister(@PathVariable byte[] name,@PathVariable byte[] email,
                               @PathVariable byte[] phone,@PathVariable byte[] password) {
        userService.userRegister(name,email,phone,password);
//        log.info("用户注册，姓名:{},邮箱:{},电话:{},密码:{}",name,email,phone,password);
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("name",name);
        userMap.put("email",email);
        userMap.put("mobile",phone);
        userMap.put("password",password);
        log.info("求职者注册:{}",userMap);
        return Result.success(userMap);
    }
}
