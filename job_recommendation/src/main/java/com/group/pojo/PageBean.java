package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/03/05/19:09
 * @Description: 分页插件组装
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean {
    private long total;//总记录数
    private List rows;//数据列表
}
