package com.group.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.group.pojo.UserNoResume;

/**
 * @Author: mfz
 * @Date: 2024/07/28/15:01
 * @Description:
 */
public interface UserNoResumeService {
    /**
     * 获取无简历信息
     * @param userId
     * @return
     */
    UserNoResume getById(Long userId);

    /**
     * 更新无简历信息
     * @param userId
     * @param userNoResume
     */
    void updateInfo(Long userId, UserNoResume userNoResume);

    /**
     * 查询无简历信息
     * @param userId
     * @param userNoResume
     */
    void insertInfo(Long userId, UserNoResume userNoResume);

    /**
     * 填充简历
     * @param id
     * @return
     */
    JsonNode fillExactById(Long id);
}
