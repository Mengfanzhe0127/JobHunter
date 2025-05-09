package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/03/12/21:51
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionResponse {
    //接收机器学习传递的result字段
    private List<Long> result;
}
