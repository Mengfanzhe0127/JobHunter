package com.group.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: mfz
 * @Date: 2024/05/08/20:45
 * @Description:
 */

@TableName(value = "user_experience")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain=true)
@AllArgsConstructor
@NoArgsConstructor
public class UserExperience {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long uid;

    private String curPosition;

    private String curCompany;

    private String workDescription;

}
