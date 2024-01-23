package com.group.controller;

import com.group.pojo.Result;
import com.group.service.EmployerService;
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
public class EmployerRegisterController {
    @Autowired
    private EmployerService employerService;

    @CrossOrigin(origins = "*")
    @PostMapping("/employer/register/{name}/{email}/{company}/{password}")
    public Result userRegister(@PathVariable byte[] name, @PathVariable byte[] email,
                               @PathVariable String company, @PathVariable byte[] password) {
        employerService.employerRegister(name,email,company,password);
        Map<String,Object> employerMap = new HashMap<>();
        employerMap.put("name",name);
        employerMap.put("email",email);
        employerMap.put("company",company);
        employerMap.put("password",password);
        log.info("招聘者注册:{}",employerMap);
        return Result.success(employerMap);
    }
}
