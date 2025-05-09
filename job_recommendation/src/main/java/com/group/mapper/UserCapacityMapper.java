package com.group.mapper;

/**
 * @Author: mfz
 * @Date: 2024/04/06/23:43
 * @Description:
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.User;
import com.group.pojo.UserCapacity;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author 86152
 * @description 针对表【user_capacity(求职者技能表)】的数据库操作Mapper
 * @createDate 2024-04-06 23:39:24
 * @Entity pojo.UserCapacity
 */
public interface UserCapacityMapper extends BaseMapper<UserCapacity> {
    /**
     * 查询能力匹配求职者
     * @param s
     * @return
     */
    @Select("SELECT id,AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       nickname, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(address, 'address_secret_key', 'AES/CBC/PKCS5Padding') AS address,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       birth, min_expected_salary, max_expected_salary, ideal_city, education, type, school, major,resume\n" +
            "FROM user WHERE id IN (select uid from user_capacity where capacity like #{s});")
    List<User> selectUserBatchDescrip(String s);

    @Select("select capacity from user_capacity where uid = #{id};")
    String selectCapacityById(Long id);

    @Select("select * from user_capacity where uid = #{id}")
    UserCapacity selectDataById(Long id);
}
