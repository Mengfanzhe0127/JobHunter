package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/13/19:53
 * @Description:
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private List<Long> result;
}
