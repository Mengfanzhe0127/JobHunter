package com.group.pojo;

/**
 * @Author: mfz
 * @Date: 2024/04/06/23:40
 * @Description:
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 求职者技能表
 * @TableName user_capacity
 */
@TableName(value ="user_capacity")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCapacity implements Serializable {
    /**
     * 求职者_技能ID
     */
    @TableId
    private Integer id;

    /**
     * 求职者ID
     */
    private Long uid;

    /**
     * 简要评价
     */
    private String capacity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

//
}
