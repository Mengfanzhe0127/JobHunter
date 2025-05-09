package com.group.controller;

import com.group.pojo.Position;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.EmployerService;
import com.group.service.PositionService;
import com.group.service.UserCapacityService;
import com.group.service.UserService;
import com.group.utils.AliOSSUtils;
import com.group.utils.ResumeSDKParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.group.utils.ResumeSDKConstants.APP_CODE;
import static com.group.utils.ResumeSDKConstants.ResumeSDK_URL;

/**
 * @Author: mfz
 * @Date: 2024/02/03/22:00
 * @Description:
 */
@RestController
@Slf4j
@CrossOrigin
public class UploadController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Autowired
    private PositionService positionService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployerService employerService;

//    @Autowired
//    private UserCapacityService userCapacityService;

    @CrossOrigin(origins = "*")
    @PostMapping("/upload/{id}")
    public Result upload(@RequestParam MultipartFile resume, @PathVariable Long id) throws Exception {
        log.info("简历上传，文件名：{}",resume.getOriginalFilename());
        String url = aliOSSUtils.upload(resume);
        log.info("简历上传成功，访问的url:{}",url);

        //将简历url保存进数据库
        userService.updateResume(url,id);
        //重要逻辑：更新缓存中的Resume字段
        userService.refreshUserInfoCache(id);
        User u = userService.encryptGetById(id);
        u.setBirth(userService.getBirthById(id));
        String userId = u.getId().toString();
        Map<String,Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userId",userId);
        userInfoMap.put("user",u);
        log.info("求职者上传简历成功，个人信息为：{}",u);

        return Result.success(userInfoMap);
    }

    /**
     * 求职者头像上传
     * @param image
     * @param id
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/user/image/{id}")
    public Result imageUpload(@RequestParam MultipartFile image, @PathVariable Long id) throws Exception {
        log.info("头像上传，文件名：{}",image.getOriginalFilename());
        String imageUrl = aliOSSUtils.upload(image);
        log.info("简历上传成功，访问的url:{}",imageUrl);

        userService.updateImageById(id,imageUrl);
        return Result.success(imageUrl);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/employer/image/{id}")
    public Result imageUploadForEmployer(@RequestParam MultipartFile image, @PathVariable Long id) throws Exception {
        log.info("头像上传，文件名：{}",image.getOriginalFilename());
        String imageUrl = aliOSSUtils.upload(image);
        log.info("简历上传成功，访问的url:{}",imageUrl);

        employerService.updateImageById(id,imageUrl);
        return Result.success(imageUrl);
    }

}
