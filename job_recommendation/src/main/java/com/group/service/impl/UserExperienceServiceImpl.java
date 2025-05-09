package com.group.service.impl;

import com.group.mapper.UserExperienceMapper;
import com.group.pojo.UserExperience;
import com.group.service.UserExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: mfz
 * @Date: 2024/05/08/22:36
 * @Description:
 */
@Service
public class UserExperienceServiceImpl implements UserExperienceService {
    @Autowired
    private UserExperienceMapper userExperienceMapper;

    @Override
    public UserExperience getById(Long id) {
        return userExperienceMapper.selectExperienceById(id);
    }

    @Override
    public void updateCurPos(Long userId,String experience) {
        userExperienceMapper.updateCurPos(experience);
    }

    @Override
    public void updateCurCompany(Long userId,String experience) {
        userExperienceMapper.updateCurCompany(experience);
    }

    @Override
    public void updateWorkDescrip(Long userId,String experience) {
        userExperienceMapper.updateCurDescrip(experience);
    }

    @Override
    public void insertExperience(Long userId, String experience, String experience1, String experience2) {
        userExperienceMapper.insertExperience(userId,experience,experience1,experience2);
    }


}
