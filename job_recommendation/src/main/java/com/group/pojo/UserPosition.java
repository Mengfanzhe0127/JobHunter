package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: mfz
 * @Date: 2024/03/20/20:42
 * @Description: 关联用户收到的职位推荐
 */
@TableName(value = "user_position")
@Data
public class UserPosition {
    /**
     * 关联表ID
     */
//    @TableId(type= IdType.AUTO)
//    private Integer id;

    /**
     * 用户ID
     */
    @TableId(value="uid")
    private Long uid;

    /**
     * 职位ID
     */
    private Long pid;

    public UserPosition(Long uid,Long pid) {
        this.pid = pid;
        this.uid = uid;
    }
}
