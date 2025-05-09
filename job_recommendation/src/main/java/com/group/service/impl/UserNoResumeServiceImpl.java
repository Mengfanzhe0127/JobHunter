package com.group.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group.mapper.UserNoResumeMapper;
import com.group.pojo.UserNoResume;
import com.group.service.UserNoResumeService;
import com.group.utils.MLServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: mfz
 * @Date: 2024/07/28/15:01
 * @Description:
 */
@Service
public class UserNoResumeServiceImpl implements UserNoResumeService {
    @Autowired
    private UserNoResumeMapper userNoResumeMapper;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Override
    public UserNoResume getById(Long userId) {
        return userNoResumeMapper.selectById(userId);
    }

    @Override
    public void updateInfo(Long userId, UserNoResume userNoResume) {
        userNoResumeMapper.updateById(userId,userNoResume);
    }

    @Override
    public void insertInfo(Long userId, UserNoResume userNoResume) {
        userNoResumeMapper.insertById(userId,userNoResume);
    }

    @Override
    public JsonNode fillExactById(Long id) {
        JsonNode info = mlServiceClient.UpdateExactById(id);
        return info;
    }
}
