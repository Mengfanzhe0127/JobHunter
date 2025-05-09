package com.group.service.impl;

import com.group.mapper.UserMapper;
import com.group.mapper.UserSelfJudgeMapper;
import com.group.pojo.PageBean;
import com.group.pojo.User;
import com.group.pojo.UserJudge;
import com.group.service.MLService;
import com.group.service.UserService;
import com.group.utils.MLServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/13/19:11
 * @Description:
 */

@Service
@Slf4j
public class MLServiceImpl implements MLService {
    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSelfJudgeMapper userSelfJudgeMapper;

    @Override
    public PageBean selectUserByDescripAndML(int pageNum, int pageSize, List<String> descriptions) {
        List<Long> targetIdList = new ArrayList<>();
        for(String descrip : descriptions) {
            List<Long> userIdList = mlServiceClient.sendDescripAndGetUserId(descrip);
            targetIdList.addAll(userIdList);
        }

        log.info("推荐求职者ID：{}",targetIdList);
        List<User> userList = userMapper.mlSelectBatchList(targetIdList);
        List<UserJudge> resultList = new ArrayList<>();
        for(User u : userList) {
            UserJudge r = new UserJudge();
            BeanUtils.copyProperties(u,r);
            String judge = userSelfJudgeMapper.selectJudgeInfoById(u.getId());
            r.setJudge(judge);
            resultList.add(r);
        }
        int total = resultList.size();
        int startIndex = (pageNum-1)*pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        List<UserJudge> usersPage = resultList.subList(startIndex,endIndex);
        PageBean pageBean = new PageBean();
        pageBean.setTotal(total);
        pageBean.setRows(usersPage);

        return pageBean;

    }
}
