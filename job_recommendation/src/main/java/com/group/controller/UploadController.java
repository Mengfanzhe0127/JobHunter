package com.group.controller;

import com.group.pojo.Result;
import com.group.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: mfz
 * @Date: 2024/02/03/22:00
 * @Description:
 */
@RestController
@Slf4j
public class UploadController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    @CrossOrigin(origins = "*")
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile resume) throws IOException {
        log.info("简历上传，文件名：{}",resume.getOriginalFilename());

        String url = aliOSSUtils.upload(resume);
        log.info("简历上传成功，访问的url:{}",url);
        return Result.success(url);
    }

}
