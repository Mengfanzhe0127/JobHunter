package com.group.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.mapper.UserPositionMapper;
import com.group.pojo.UserPosition;
import com.group.service.UserPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: mfz
 * @Date: 2024/03/20/20:50
 * @Description:
 */
@Service
public class UserPositionServiceImpl extends ServiceImpl<UserPositionMapper,UserPosition>
        implements UserPositionService {
    @Autowired
    private UserPositionMapper userPositionMapper;
    @Override
    public void saveUserPositions(Long id, List<Long> positionIds) {
        List<UserPosition> userPositions = positionIds.stream()
                .map(positionId -> new UserPosition(id,positionId))
                .collect(Collectors.toList());
        this.saveBatch(userPositions);
    }
}
