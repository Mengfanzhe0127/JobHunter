package com.group.pool;

import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: mfz
 * @Date: 2024/05/11/18:24
 * @Description:
 */
public class SocketPool {
    public static final Map<Long, Session> ONLINE_USER_SESSIONS = new ConcurrentHashMap<>();

    public static void add(Long userId, Session session) {
        ONLINE_USER_SESSIONS.put(userId, session);
    }

    public static Session get(Long userId) {
        return ONLINE_USER_SESSIONS.get(userId);
    }

    public static void remove(Long userId) {
        ONLINE_USER_SESSIONS.remove(userId);
    }

    public static int count() {
        return ONLINE_USER_SESSIONS.size();
    }
}
