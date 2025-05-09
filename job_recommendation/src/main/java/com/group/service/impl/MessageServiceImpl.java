package com.group.service.impl;

import com.group.mapper.MessageMapper;
import com.group.pojo.*;
import com.group.service.EmployerService;
import com.group.service.MessageService;
import com.group.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/11/14:31
 * @Description:
 */

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private UserService userService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void addFriend(Long uid, Long eid) {
        messageMapper.insertFriend(uid,eid);
    }

    @Override
    public UserRelation getRelation(Long uid, Long eid) {
        return messageMapper.getRelation(uid,eid);
    }

    @Override
    public List<StrEmployer> getEmployerByUid(Long id) {
        List<Employer> employerList =  messageMapper.selectEmployerByUid(id);
        List<StrEmployer> strList = new ArrayList<>();
        for (Employer el : employerList) {
            String strId = el.getId().toString();
            Integer count = 0;
            count = messageMapper.selectUnreadedById(id,el.getId());
            StrEmployer strEmployer = new StrEmployer(strId,el.getName(),el.getImage(),count);
            strList.add(strEmployer);
        }

        return strList;
    }

    @Override
    public List<UserStrImage> getUserByEid(Long id) {
        List<User> userList = messageMapper.selectUserByEid(id);
        List<UserStrImage> strList = new ArrayList<>();
        for(User u : userList) {
            String strId = u.getId().toString();
            Integer count = 0;
            count = messageMapper.selectUnreadedById(id,u.getId());
            UserStrImage strUser = new UserStrImage(strId,u.getName(),u.getImage(),count);
            strList.add(strUser);
        }
        return strList;
    }

    @Override
    public void saveMessage(Long fromUserId, Long toUserId, String textMessage, String type) {
        LocalDateTime saveTime = LocalDateTime.now();
        Long uid;
        Long eid;
        String toImage;
        String fromImage;

        boolean readed = false;
        if(userService.encryptGetById(fromUserId) != null ) {
            uid = fromUserId;
            eid = toUserId;
            fromImage = userService.getImageById(uid);
            toImage = employerService.getImageById(eid);
        }else {
            eid = fromUserId;
            uid =toUserId;
            toImage = userService.getImageById(uid);
            fromImage = employerService.getImageById(eid);
        }

        messageMapper.saveMessage(toUserId,toImage,fromUserId,fromImage,type,textMessage,saveTime,readed);
    }

    @Override
    public HashMap<String,Object> getMessageAndUnreaded(Long toUserId, Long fromUserId) {
        int unreaded = 0;
        List<Message> messageList = messageMapper.selectMessage(toUserId,fromUserId);
        List<StrMessage> targetList = new ArrayList<>();
        for(Message m : messageList) {
            if(!m.getReaded() && m.getToUser().equals(fromUserId) && m.getFromUser().equals(toUserId)) {
                ++unreaded;
            }
            String toUserStr = m.getToUser().toString();
            String fromUserStr = m.getFromUser().toString();
            StrMessage sm = new StrMessage(toUserStr,m.getToUserAvatar(),fromUserStr,
                    m.getFromUserAvatar(),m.getType(),m.getMessage(),m.getTime(),m.getReaded());

            targetList.add(sm);
        }
//        return targetList;
        HashMap<String,Object> targetMap = new HashMap<>();
        targetMap.put("message",targetList);
        targetMap.put("unreaded",unreaded);
        return targetMap;
    }

    @Override
    public void updateReaded(Long toUserId, Long fromUserId) {
        messageMapper.updateReaded(toUserId,fromUserId);
    }
}
