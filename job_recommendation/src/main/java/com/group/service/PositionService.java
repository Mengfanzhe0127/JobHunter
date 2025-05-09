package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.group.pojo.PageBean;
import com.group.pojo.Position;
import com.group.pojo.PositionWithScore;
import com.group.pojo.User;

import java.util.List;
import java.util.Map;

/**
* @author mfz
* @description 针对表【position(职位表)】的数据库操作Service
* @createDate 2024-01-23 00:33:46
*/
public interface PositionService extends IService<Position> {
    /**
     * 生成ID
     * @return
     */
    Long getUniqueIdBySnowFlake();

    /**
     * 查询收藏列表
     * @param userId
     * @return
     */
    List<Position> selectFavoritePositionList(Long userId);

    /**
     * 获取职位推荐方法二
     * @param positionsWithScores
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageBean getPositionsByIds(List<PositionWithScore> positionsWithScores, int pageNum, int pageSize);

    /**
     * 招聘者发布职位
     * @param position
     */
    void addPosition(Position position);

    /**
     * 招聘者发布职位信息
     * @param employerId
     */
    void saveEid(Long employerId,Long positionId);


    /**
     * 数据库查询resume到获得推荐职位的整体封装
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageBean getPositionsByML2(Long id, int pageNum, int pageSize);

    /**
     * 无简历情况下求职者获得职位推荐
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageBean getPositionByML3(Long id, int pageNum, int pageSize) throws Exception;
}
