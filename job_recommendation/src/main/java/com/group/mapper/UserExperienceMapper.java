package com.group.mapper;

import com.group.pojo.UserExperience;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author: mfz
 * @Date: 2024/05/08/22:40
 * @Description:
 */
@Mapper
public interface UserExperienceMapper {
    @Select("select * from user_experience where uid = #{id}")
    UserExperience selectExperienceById(Long id);

    /**
     * 更新现有职位
     * @param experience
     */
    @Update("update user_experience set cur_position = #{experience}")
    void updateCurPos(String experience);

    /**
     * 更新现有公司
     * @param experience
     */
    @Update("update user_experience set cur_company = #{experience}")
    void updateCurCompany(String experience);

    /**
     * 更新工作经历
     * @param experience
     */
    @Update("update user_experience set work_description = #{experience} ")
    void updateCurDescrip(String experience);

    /**
     * 插入工作经历
     * @param userId
     * @param experience
     * @param experience1
     * @param experience2
     */
    @Insert("insert into user_experience(uid,cur_position,cur_company,work_description) " +
            "values (#{userId},#{experience},#{experience1},#{experience2})")
    void insertExperience(Long userId, String experience, String experience1, String experience2);
}
