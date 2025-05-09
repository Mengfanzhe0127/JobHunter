package com.group.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: mfz
 * @Date: 2024/05/12/17:00
 * @Description:
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrMessage {
    private String toUser;
    private String toUserAvatar;
    private String fromUser;
    private String fromUserAvatar;
    private String type;
    private String message;
    private LocalDateTime time;
    private Boolean readed;

}
