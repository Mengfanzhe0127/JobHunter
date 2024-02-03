package com.group.controller;

import com.group.anno.Encrypt;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 求职者查询个人信息
     * @param id
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    @Encrypt
    public Result userGetById(@PathVariable("id") Long id) {
        User u = userService.encryptGetById(id);
        log.info("求职者查询个人信息：{}",u);
        return Result.success(u);
    }

    /**
     * 求职者修改个人信息
     * @param user
     * @return
     */
    @CrossOrigin(origins = "*")
    @PutMapping
    @Encrypt
    public Result userUpdate(@RequestBody User user) {
//        User u = userService.encryptGetById(id);
        log.info("求职者修改信息：{}",user);
        userService.encryptUpdate(user);
//        log.info("求职者修改信息完毕,修改后信息：{}",u);
        return Result.success();
    }


}
