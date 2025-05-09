package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: mfz
 * @Date: 2024/03/29/10:10
 * @Description: 用户修改个人信息辅助实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJson {
    private User user;
    private String[] idealCityArr;
    //求职者工作经验封装
    private String[] experience;
    //求职者自我评价封装
    private String judge;

    /**
     * 无简历信息封装
     */
    private UserNoResume userNoResume;
}
