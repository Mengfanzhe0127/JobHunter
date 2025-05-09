package com.group.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Date;


/**
 * @Author: mfz
 * @Date: 2024/07/28/14:48
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserNoResume {
    private Long id;
    private Date birth;
    private String workExperience;
    private String awards;
    private String skills;
    private String selfJudge;
    private String selfInfo;
}
