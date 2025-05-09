package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @Author: mfz
 * @Date: 2024/07/28/21:51
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJudge {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 出生日期
     */
    private Date birth;

    /**
     * 最低期望工资
     */
    private Integer minExpectedSalary;

    /**
     * 最高期望工资
     */
    private Integer maxExpectedSalary;

    /**
     * 理想工作城市
     */
    private String idealCity;

    /**
     * 最高学历
     */
    private Object education;

    /**
     * 工作类型
     */
    private Object type;

    /**
     * 学校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户通讯地址
     */
    private String address;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 求职者类型
     * 用于前端判断，求职者默认为0
     */
    private Integer characterType = 0;

    /**
     * 求职者简历
     */
    private String resume;

    /**
     * 求职者头像
     */
    private String image;

    /**
     * 自我评价
     */
    private String judge;
}
