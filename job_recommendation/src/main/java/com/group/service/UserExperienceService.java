package com.group.service;

import com.group.pojo.UserExperience;

/**
 * @Author: mfz
 * @Date: 2024/05/08/22:36
 * @Description:
 */
public interface UserExperienceService {
    /**
     * 获得求职者工作经历
     * @param id
     * @return
     */
    UserExperience getById(Long id);

    /**
     * 工作经验更新职位
     * @param experience
     */
    void updateCurPos(Long userId,String experience);

    /**
     * 工作经验更新公司
     * @param experience
     */
    void updateCurCompany(Long userId,String experience);

    /**
     * 工作经验更新描述
     * @param experience
     */
    void updateWorkDescrip(Long userId,String experience);

    /**
     * 插入工作经历
     * @param userId
     * @param experience
     * @param experience1
     * @param experience2
     */
    void insertExperience(Long userId, String experience, String experience1, String experience2);
}
