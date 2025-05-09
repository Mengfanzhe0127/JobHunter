package com.group.controller;

import com.group.pojo.AnswerJsonBody;
import com.group.pojo.PageBean;
import com.group.pojo.Result;
import com.group.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: mfz
 * @Date: 2024/07/19/12:28
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/qs")
@CrossOrigin
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @GetMapping
    public Result getQuestion(@RequestParam(defaultValue = "1") int pageNum,
                              @RequestParam(defaultValue = "6") int pageSize) {
        PageBean questionResult = questionService.getQues(pageNum,pageSize);
        return Result.success(questionResult);
    }

    @PostMapping("/code")
    public Result getMaskById(@RequestParam int id,@RequestBody String answer) {
        Integer mask = 0;
        switch (id) {
            case 1 : mask = questionService.getMask1(answer); break;
            case 2 : mask = questionService.getMask2(answer); break;
            case 3 : mask = questionService.getMask3(answer); break;
            case 4 : mask = questionService.getMask4(answer);
        }
        return Result.success(mask);
    }

    @PostMapping("/short")
    public Result getShortMaskById(@RequestParam int id,@RequestBody String answer) {
        Integer mask = 0;
        switch (id) {
            case 1 : mask = questionService.getMask5(answer); break;
            case 2 : mask = questionService.getMask6(answer);
        }
        return Result.success(mask);
    }
}
