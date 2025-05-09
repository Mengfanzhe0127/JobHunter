package com.group.controller;

import com.group.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: mfz
 * @Date: 2024/07/28/22:21
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/advertise")
@CrossOrigin
public class AdvertiseController {
    @GetMapping
    public Result getAdvertise() {
        String tar = randomImage();
        return Result.success(tar);

    }

    private String randomImage() {
        String image0 = "https://web-employee-recommendation.oss-cn-chengdu.aliyuncs.com/%E7%A8%8B%E5%BA%8F%E5%91%98%E5%9F%B9%E8%AE%AD1.png";
        String image1 = "https://web-employee-recommendation.oss-cn-chengdu.aliyuncs.com/%E7%A8%8B%E5%BA%8F%E5%91%98%E5%9F%B9%E8%AE%AD2.png";
        String image2 = "https://web-employee-recommendation.oss-cn-chengdu.aliyuncs.com/%E6%A0%A1%E5%9B%AD%E6%8B%9B%E8%81%98.png";
        String image3 = "https://web-employee-recommendation.oss-cn-chengdu.aliyuncs.com/%E8%81%8C%E4%B8%9A%E8%A7%84%E5%88%92.png";
        int randomNum = ((int)(Math.random()*1000))%4;
        switch(randomNum) {
            case 0 : return image0;
            case 1 : return image1;
            case 2 : return image2;
            case 3 : return image3;
        }
        return null;
    }
}
