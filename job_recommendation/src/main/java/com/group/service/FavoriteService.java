package com.group.service;

import com.group.pojo.Position;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/02/23/16:00
 * @Description:
 */
public interface FavoriteService {
    /**
     * 求职者收藏推荐职位
     * @param userId
     * @param positionId
     */
    void saveFavorite(Long userId, Long positionId);

    /**
     * 求职者删除收藏的职位
     * @param userId
     * @param positionIds
     */
    void deleteFavorite(Long userId, List<Long> positionIds);
}
