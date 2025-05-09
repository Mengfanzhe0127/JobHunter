package com.group.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.mapper.EmployerMapper;
import com.group.mapper.FavoriteMapper;
import com.group.pojo.Employer;
import com.group.pojo.Favorite;
import com.group.pojo.Position;
import com.group.service.EmployerService;
import com.group.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/02/23/16:00
 * @Description:
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
        implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Override
    public void saveFavorite(Long userId, Long positionId) {
        favoriteMapper.insertFavorite(userId,positionId);
    }

    @Override
    public void deleteFavorite(Long userId, List<Long> positionIds) {
        favoriteMapper.deleteFavorite(userId,positionIds);
    }


}
