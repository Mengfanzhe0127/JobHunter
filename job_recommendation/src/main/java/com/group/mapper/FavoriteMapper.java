package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.Favorite;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/02/23/15:55
 * @Description:
 */
public interface FavoriteMapper extends BaseMapper<Favorite> {
    /**
     * 求职者添加收藏
     * @param userId
     * @param positionId
     */
    @Insert("insert into favorite(uid,pid) values (#{userId},#{positionId})")
    void insertFavorite(Long userId, Long positionId);

    /**
     * 求职者取消收藏
     * @param userId
     * @param positionIds
     */
    void deleteFavorite(Long userId, List<Long> positionIds);
}
