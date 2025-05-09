package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: mfz
 * @Date: 2024/02/23/15:41
 * @Description:
 */

@Data
@TableName("favorite")
public class Favorite {
    /**
     * 关联ID
     */
    @TableId(type= IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 职位ID
     */
    private Long pid;

}
