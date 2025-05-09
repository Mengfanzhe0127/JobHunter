package com.group.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.group.pojo.Result;
import com.group.pojo.UserNoResume;
import com.group.service.UserNoResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: mfz
 * @Date: 2024/07/28/17:30
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/no_resume")
@CrossOrigin
public class NoResumeController {
    @Autowired
    private UserNoResumeService userNoResumeService;

    /**
     * 用户大段文字信息解析
     * @return
     */
    @PostMapping("/{id}")
    public Result fill(@PathVariable Long id) {
        JsonNode exact = userNoResumeService.fillExactById(id);
        return Result.success(exact);
    }
}
