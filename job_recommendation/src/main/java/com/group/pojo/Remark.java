package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 评论表
 * @TableName remark
 */
@TableName(value ="remark")
@Data
public class Remark implements Serializable {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 推荐指数
     */
    private Object score;

    /**
     * 评论内容
     */
    private String detail;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 招聘者ID
     */
    private Long eid;

    /**
     * 招聘者姓名
     */
    private String employerName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}