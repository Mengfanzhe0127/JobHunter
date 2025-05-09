package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.Position;
import com.group.pojo.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author mfz
* @description 针对表【position(职位表)】的数据库操作Mapper
* @createDate 2024-01-23 00:33:46
* @Entity com.group.pojo.Position
*/
public interface PositionMapper extends BaseMapper<Position> {
    /**
     * 求职者查询收藏
     * @param userId
     * @return
     */
    @Select("SELECT p.* from position p JOIN favorite f ON p.id = f.pid JOIN user u ON u.id = f.uid where u.id = #{userId}")
    List<Position> selectFavoritePositionList(Long userId);

    /**
     * 求职者无简历职位推荐
     * @param u
     * @return
     */
    @Select("select id from position where (title like '%前端%' OR title like '%后端%')\n" +
            "                         AND (id >= 0 AND id <= 150)\n" +
            "                         AND (education like '大专' OR education like '本科')\n" +
            "                         AND (address like '%广东%' or address like '%北京%' or address like '%深圳%');")
    List<Long> selectTargetPositions(User u);
}




