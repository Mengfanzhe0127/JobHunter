package com.group.controller;

import com.group.anno.Encrypt;
import com.group.pojo.Employer;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.EmployerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employer")
public class EmployerController {
    @Autowired
    private EmployerService employerService;

    /**
     * 招聘者查看个人信息
     * @param id
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    @Encrypt
    public Result employerGetById(@PathVariable("id") Long id) {
        Employer e = employerService.encryptGetById(id);
        log.info("招聘者查询个人信息：{}",e);
        return Result.success(e);
    }

    /**
     * 招聘者修改个人信息
     * @param employer
     * @return
     */
    @CrossOrigin(origins = "*")
    @PutMapping
    @Encrypt
    public Result employerUpdate(@RequestBody Employer employer) {
        log.info("招聘者修改个人信息：{}",employer);
        //json务必携带Id
        employerService.encryptUpdate(employer);
        return Result.success();
    }
}
