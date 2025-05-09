package com.group.service.impl;

import com.group.mapper.UserSelfJudgeMapper;
import com.group.pojo.UserSelfJudge;
import com.group.service.UserSelfJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: mfz
 * @Date: 2024/05/08/23:14
 * @Description:
 */
@Service
public class UserSelfJudgeServiceImpl implements UserSelfJudgeService {
    @Autowired
    private UserSelfJudgeMapper userSelfJudgeMapper;

    @Override
    public UserSelfJudge getById(Long id) {
        return userSelfJudgeMapper.selectJudgeById(id);
    }

    @Override
    public void updateJudge(Long userId, String judge) {
        userSelfJudgeMapper.updateJudge(userId,judge);
    }

    @Override
    public void insertJudge(Long userId, String judge) {
        userSelfJudgeMapper.insertJudge(userId,judge);
    }
}
