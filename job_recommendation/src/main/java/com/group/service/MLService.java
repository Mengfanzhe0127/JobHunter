package com.group.service;

import com.group.pojo.PageBean;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/13/19:10
 * @Description:
 */
public interface MLService {
    /**
     * 获得招聘者推荐
     * @param pageNum
     * @param pageSize
     * @param descriptions
     * @return
     */
    PageBean selectUserByDescripAndML(int pageNum, int pageSize, List<String> descriptions);
}
