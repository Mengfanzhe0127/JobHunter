package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: mfz
 * @Date: 2024/05/12/15:07
 * @Description:
 */
@Data
@AllArgsConstructor
public class StrEmployer {
    /**
     * 招聘者编号
     */
    private String id;

    /**
     * 招聘者姓名
     */
    private String name;

    /**
     * 招聘者头像
     */
    private String image;

    /**
     * 未读消息记录
     */
    private Integer unreaded;
}
