package com.group.service;

import cn.hutool.core.lang.hash.Hash;
import com.group.pojo.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/11/14:31
 * @Description:
 */
public interface MessageService {
    /**
     * 添加好友
     * @param uid
     * @param eid
     */
    void addFriend(Long uid, Long eid);

    /**
     * 获得记录
     * @param uid
     * @param eid
     * @return
     */
    UserRelation getRelation(Long uid,Long eid);

    List<StrEmployer> getEmployerByUid(Long id);

    List<UserStrImage> getUserByEid(Long id);

    void saveMessage(Long fromUserId,Long toUserId,String textMessage,String type);

    HashMap<String,Object> getMessageAndUnreaded(Long toUserId, Long fromUserId);

    void updateReaded(Long toUserId, Long fromUserId);
}
