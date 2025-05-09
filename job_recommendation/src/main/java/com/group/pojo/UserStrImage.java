package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @Author: mfz
 * @Date: 2024/05/12/19:41
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStrImage {
    /**
     * 转换ID
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 求职者头像
     */
    private String image;

    /**
     * 未读
     */
    private Integer unreaded;
}
