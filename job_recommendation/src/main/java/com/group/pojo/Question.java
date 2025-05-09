package com.group.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: mfz
 * @Date: 2024/07/19/13:09
 * @Description:
 */
@Data
@TableName(value = "question")
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    private Integer id;
    private String ques;
    private Integer tag;
    private String example;
    private String answer;
}
