package com.group.controller;

import com.group.pojo.Message;
import com.group.pojo.Result;
import com.group.pojo.StrMessage;
import com.group.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/05/11/14:29
 * @Description:
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/ms")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/{uid}/{eid}" )
    public Result addFriend(@PathVariable Long uid,@PathVariable Long eid) {
        if(messageService.getRelation(uid,eid) == null) {
            messageService.addFriend(uid, eid);
        }
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getFriend(@PathVariable Long id,@RequestParam int tag) {  //tag 0 / 1

        if(tag == 0) {
            return Result.success(messageService.getEmployerByUid(id));
        }else {
            return Result.success(messageService.getUserByEid(id));
        }
    }

    @GetMapping("/message/{toUserId}/{fromUserId}")
    public Result getToUserMessage(@PathVariable Long toUserId,@PathVariable Long fromUserId) {
        HashMap<String,Object> targetMap = new HashMap<>();
        targetMap = messageService.getMessageAndUnreaded(toUserId,fromUserId);
        return Result.success(targetMap);
    }

    @PutMapping("/{toUserId}/{fromUserId}")
    public Result updateReaded(@PathVariable Long toUserId,@PathVariable Long fromUserId) {
        messageService.updateReaded(toUserId,fromUserId);
        return Result.success();
    }

}
