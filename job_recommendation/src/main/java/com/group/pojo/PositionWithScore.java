package com.group.pojo;

import lombok.Data;

/**
 * @Author: mfz
 * @Date: 2024/08/02/16:05
 * @Description:
 */
@Data
public class PositionWithScore {
    /**
     * 职位ID
     */
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

    /**
     * 匹配度
     */
    private Integer score;
}
