package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 招聘者表
 * @TableName employer
 */
@TableName(value ="employer")
@Data
public class Employer implements Serializable {
    /**
     * 招聘者编号
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 招聘者职务
     */
    private String duty;

    /**
     * 职位编号
     */
    private Integer pid;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 招聘者姓名
     */
    private String name; //取消加密

    /**
     * 招聘者电话
     */
    private String phone; //取消加密

    /**
     * 招聘者邮箱
     */
    private String email; //取消加密

    /**
     * 招聘者密码
     */
    private String password; //取消加密

    /**
     * 招聘者类型
     * 用于前端交互，默认为1
     */
    private Integer charaterType = 1;

//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
}