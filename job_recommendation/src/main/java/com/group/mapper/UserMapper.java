package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
* @author mfz
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-01-23 00:20:25
* @Entity com.group.pojo.User
*/
public interface UserMapper extends BaseMapper<User> {
    /**
     * 求职者注册
     * 插入真实姓名，邮箱，电话，密码
     * @param id
     * @param name
     * @param email
     * @param phone
     * @param password
     */
    @Insert("insert into user(id,name,email,phone,password) values (#{id},AES_ENCRYPT(#{name},'name_secret_key')," +
            "AES_ENCRYPT(#{email},'email_secret_key'),AES_ENCRYPT(#{phone},'phone_secret_key')," +
            "AES_ENCRYPT(#{password},'password_secret_key'))")
    void insertByNameEmailPhonePassword(Long id, byte[] name, byte[] email, byte[] phone, byte[] password);

    /**
     * 求职者电话登陆
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from user where AES_DECRYPT(phone,'phone_secret_key') = #{phoneOrEmail} and " +
            "AES_DECRYPT(password,'password_secret_key') = #{password}")
    User getByPhoneAndPassword(byte[] phoneOrEmail,byte[] password);

    /**
     * 求职者邮箱登录
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from user where AES_DECRYPT(email,'email_secret_key') = #{phoneOrEmail} and " +
            "AES_DECRYPT(password,'password_secret_key') = #{password}")
    User getByEmailAndPassword(byte[] phoneOrEmail,byte[] password);
}




