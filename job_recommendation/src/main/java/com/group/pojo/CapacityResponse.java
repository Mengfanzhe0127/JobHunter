package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: mfz
 * @Date: 2024/04/03/22:00
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapacityResponse {
    //接受机器学习能力评价String
    private String result;
}
