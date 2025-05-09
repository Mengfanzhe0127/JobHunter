package com.group.mapper;

import com.group.pojo.UserSelfJudge;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: mfz
 * @Date: 2024/05/08/23:16
 * @Description:
 */
@Mapper
public interface UserSelfJudgeMapper {
    @Select("select * from user_self_judge where uid = #{id}")
    UserSelfJudge selectJudgeById(Long id);

    @Update("update user_self_judge set self_judge = #{judge} where uid = #{userId}")
    void updateJudge(Long userId, String judge);

    @Insert("insert into user_self_judge(uid,self_judge) values (#{userId},#{judge})")
    void insertJudge(Long userId, String judge);
    @Select("select self_judge from user_self_judge where uid = #{id}")
    String selectJudgeInfoById(Long id);
}
