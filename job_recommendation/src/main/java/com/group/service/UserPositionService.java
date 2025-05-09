package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.UserPosition;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/03/20/20:41
 * @Description:
 */
public interface UserPositionService extends IService<UserPosition>{

    /**
     * 将推荐信息保存进user_position关联表
     * @param id
     * @param positionIds
     */
    void saveUserPositions(Long id, List<Long> positionIds);
}
