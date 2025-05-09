package com.group.handler;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.group.pool.SocketPool.ONLINE_USER_SESSIONS;


/**
 * @Author: mfz
 * @Date: 2024/05/11/18:31
 * @Description:
 */
public class SocketHandler {
    private static final Logger log = LoggerFactory.getLogger(SocketHandler.class);

    public static void sendMessage(Session session, String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("消息发送异常： {}", e.getMessage());
            }
        }
    }

    public static void sendMessageAll(String message, Long excludeUserId) {
        ONLINE_USER_SESSIONS.forEach((userId, session) -> {
            if (!userId.equals(excludeUserId)) {
                sendMessage(session, message);
            }
        });
    }
}
