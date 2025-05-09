package com.group.controller;

import com.group.pojo.Position;
import com.group.pojo.Result;
import com.group.service.FavoriteService;
import com.group.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/03/05/13:07
 * @Description:
 */

@Slf4j
@RestController
@RequestMapping("/favorite")
@CrossOrigin
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private PositionService positionService;
    /**
     * 用户收藏推荐职位
     * @param userId
     * @param positionId
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping
    public Result addFavorite(@RequestParam Long userId,@RequestParam Long positionId){
        favoriteService.saveFavorite(userId,positionId);
        return Result.success();
    }

    /**
     * 查询收藏列表
     * @param userId
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping
    public Result userGetFavoritePositions(@RequestParam Long userId) {
        List<Position> userFavoriteList = positionService.selectFavoritePositionList(userId);
        return Result.success(userFavoriteList);
    }

    /**
     * 取消职位收藏
     * @param userId
     * @param positionIds
     * @return
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/{userId}/{positionIds}")
    public Result deleteFavorite(@PathVariable Long userId,@PathVariable List<Long> positionIds) {
        log.info("用户："+userId+" 删除的职位ID列表为"+positionIds);
        favoriteService.deleteFavorite(userId,positionIds);
        return Result.success();
    }
}
