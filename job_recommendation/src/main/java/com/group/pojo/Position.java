package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 职位表
 * @TableName position
 */
@TableName(value ="position")
@Data
public class Position implements Serializable {
    /**
     * 职位ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位名称
     */
    private String title;

    /**
     * 薪资范围
     */
    private String salary;

    /**
     * 学历要求
     */
    private String education;

    /**
     * 职位描述
     */
    private String description;

    /**
     * 招聘负责人
     */
    private String hiringManager;

    /**
     * 最后活跃时间
     */
    private String lastActive;

    /**
     * 工作地点
     */
    private String address;

    /**
     * 职位链接
     */
    private String link;

    /**
     * 招聘者ID
     */
    private Long eid;

//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
}