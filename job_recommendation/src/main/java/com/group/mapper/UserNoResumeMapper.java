package com.group.mapper;

import com.group.pojo.UserNoResume;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: mfz
 * @Date: 2024/07/28/15:09
 * @Description:
 */
@Mapper
public interface UserNoResumeMapper {
    @Select("select * from user_no_resume where id = #{userId}")
    UserNoResume selectById(Long userId);

    void updateById(Long userId,UserNoResume userNoResume);

    @Insert("insert into user_no_resume(id, birth, work_experience, awards, skills, self_judge, self_info) values " +
            "(#{userId}, #{userNoResume.birth}, #{userNoResume.workExperience}, #{userNoResume.awards}, " +
            "#{userNoResume.skills}, #{userNoResume.selfJudge}, #{userNoResume.selfInfo})")
    void insertById(Long userId,UserNoResume userNoResume);

    @Select("select self_info from user_no_resume where id = #{id}")
    String selectSelfInfoById(Long id);
}
