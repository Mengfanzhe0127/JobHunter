package com.group.service;

import com.group.pojo.UserSelfJudge;

/**
 * @Author: mfz
 * @Date: 2024/05/08/23:12
 * @Description:
 */
public interface UserSelfJudgeService {
    /**
     * 求职者获得自我评价
     * @param id
     * @return
     */
    UserSelfJudge getById(Long id);

    /**
     * 更新用户自我评价
     * @param userId
     * @param judge
     */
    void updateJudge(Long userId, String judge);

    /**
     * 插入用户自我评价
     * @param userId
     * @param judge
     */
    void insertJudge(Long userId, String judge);
}
