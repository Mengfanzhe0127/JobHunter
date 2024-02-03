package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
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
    @Insert("insert into user(id,name,email,phone,password) values (#{id}," +
            "AES_ENCRYPT(#{name},'name_secret_key','AES/CBC/PKCS5Padding')," +
            "AES_ENCRYPT(#{email},'email_secret_key','AES/CBC/PKCS5Padding')," +
            "AES_ENCRYPT(#{phone},'phone_secret_key','AES/CBC/PKCS5Padding')," +
            "AES_ENCRYPT(#{password},'password_secret_key','AES/CBC/PKCS5Padding'))")
    void insertByNameEmailPhonePassword(Long id, byte[] name, byte[] email, byte[] phone, byte[] password);

    /**
     * 求职者电话登陆
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from user where AES_DECRYPT(phone,'phone_secret_key','AES/CBC/PKCS5Padding') = #{phoneOrEmail} and " +
            "AES_DECRYPT(password,'password_secret_key','AES/CBC/PKCS5Padding') = #{password}")
    User getByPhoneAndPassword(byte[] phoneOrEmail,byte[] password);

    /**
     * 求职者邮箱登录
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from user where AES_DECRYPT(email,'email_secret_key','AES/CBC/PKCS5Padding') = #{phoneOrEmail} and " +
            "AES_DECRYPT(password,'password_secret_key','AES/CBC/PKCS5Padding') = #{password}")
    User getByEmailAndPassword(byte[] phoneOrEmail,byte[] password);

    /**
     * 求职者查询个人信息
     * @param id
     * @return
     */
    @Select("SELECT id, AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       nickname, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(address, 'address_secret_key', 'AES/CBC/PKCS5Padding') AS address,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       birth_year, min_expected_salary, max_expected_salary, ideal_city, education, type, school, major\n" +
            "FROM user WHERE id = #{id};")
    User encryptSelectById(@Param("id") Long id);


    /**
     * 求职者修改个人信息
     * 动态SQL
     * @param u
     * @return
     */
    void encryptUpdate(User u);
}




