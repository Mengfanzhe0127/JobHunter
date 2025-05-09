package com.group.controller;

import com.group.handler.SocketHandler;
import com.group.pool.SocketPool;
import com.group.service.MessageService;
import com.group.service.UserService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;



/**
 * @Author: mfz
 * @Date: 2024/05/11/18:17
 * @Description:
 */

@Component
@ServerEndpoint("/websocket/{userId}")
public class SocketEndPoint {
    private static final Logger log = LoggerFactory.getLogger(SocketEndPoint.class);

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        SocketEndPoint.userService = userService;
    }


    private static MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        SocketEndPoint.messageService = messageService;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        log.info("有新的连接：{}，用户ID：{}", session, userId);
        SocketPool.add(userId, session);
        SocketHandler.sendMessageAll("用户【" + userId + "】已上线", userId);
        log.info("在线人数：{}", SocketPool.count());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        JSONObject jsonMessage = new JSONObject(message);
        Long receiverId = jsonMessage.getLong("receiver");
        String chatMessage = jsonMessage.getString("message");

        Session receiverSession = SocketPool.get(receiverId);
        if (receiverSession != null) {
            SocketHandler.sendMessage(receiverSession, chatMessage);
        }
        log.info("用户 {} 发送消息给 {}: {}", userId, receiverId, chatMessage);
        messageService.saveMessage(userId,receiverId,chatMessage,"text");

    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session) {
        log.info("用户 {} 的连接关闭： {}", userId, session);
        SocketPool.remove(userId);
        SocketHandler.sendMessageAll("用户【" + userId + "】已离开聊天室", userId);
        log.info("在线人数：{}", SocketPool.count());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("连接出现异常： {}", throwable.getMessage());
        try {
            session.close();
        } catch (IOException e) {
            log.error("关闭连接时发生异常： {}", e.getMessage());
        }
    }
}
