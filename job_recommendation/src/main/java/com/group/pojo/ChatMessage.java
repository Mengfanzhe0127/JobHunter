package com.group.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: mfz
 * @Date: 2024/05/11/13:25
 * @Description:
 */

@Document(collection = "message")
public class ChatMessage {
    @Id
    private Long id;

    /**
     * 接收方
     */
    private Long toUser;
    private Long toUserAvatar;
    private String fromUser;
    private String fromUserAvatar;
    private String type;
    private String message;
    private Date time;
    private Boolean readed;
}
