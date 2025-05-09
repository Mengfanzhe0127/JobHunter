package com.group.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: mfz
 * @Date: 2024/05/11/21:02
 * @Description:
 */
@TableName("message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long toUser;
    private String toUserAvatar;
    private Long fromUser;
    private String fromUserAvatar;
    private String type;
    private String message;
    private LocalDateTime time;
    private Boolean readed;
}
