package com.group.mapper;

import com.group.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/07/19/13:12
 * @Description:
 */
@Mapper
public interface QuestionMapper {
    @Select("select * from question")
    List<Question> selectQues();
}
