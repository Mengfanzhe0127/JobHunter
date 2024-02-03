package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value="id",type=IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 出生年份
     */
    private Integer birthYear;

    /**
     * 最低期望工资
     */
    private String minExpectedSalary;

    /**
     * 最高期望工资
     */
    private String maxExpectedSalary;

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
     * 用户简历
     */
    private String resume;


//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
}