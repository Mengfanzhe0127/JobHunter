package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: mfz
 * @Date: 2024/05/11/14:39
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRelation {
    private Long uid;
    private Long eid;
}
