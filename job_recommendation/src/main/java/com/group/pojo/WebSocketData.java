package com.group.pojo;

import cn.hutool.db.Session;
import lombok.Data;

/**
 * @Author: mfz
 * @Date: 2024/05/04/22:11
 * @Description:
 */
@Data
public class WebSocketData {
    /**
     * 当前连接
     */
    private Session session;
    /**
     * 当前通讯ID
     */
    private String communicationId;
}

