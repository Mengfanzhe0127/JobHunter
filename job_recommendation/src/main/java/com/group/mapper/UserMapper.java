package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.util.List;

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
    @Select("SELECT id,AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       nickname, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(address, 'address_secret_key', 'AES/CBC/PKCS5Padding') AS address,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       birth, min_expected_salary, max_expected_salary, ideal_city, education, type, school, major,resume,image\n" +
            "FROM user WHERE id = #{id};")
    User encryptSelectById(@Param("id") Long id);


    /**
     * 求职者修改个人信息
     * 动态SQL
     * @param u
     * @return
     */
    void encryptUpdate(User u);

    /**
     * 用户生日查询
     * @param id
     * @return
     */
    @Select("select birth from user where id = #{id}")
    Date selectBirthById(Long id);

    /**
     * 根据ID修改用户简历url
     * @param url
     * @param id
     */
    @Select("update user set resume = #{url} where id = #{id}")
    void updateResumeById(String url, Long id);

    /**
     * 根据ID查询用户建立url
     * @param id
     * @return
     */
    @Select("select resume from user where id = #{id}")
    String selectResumeById(Long id);

    /**
     * 根据求职者ID获得推荐用户
     * @param employerId
     * @return
     */
    @Select("SELECT id,AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       nickname, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(address, 'address_secret_key', 'AES/CBC/PKCS5Padding') AS address,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       birth, min_expected_salary, max_expected_salary, ideal_city, education, type, school, major,resume\n" +
            "from user where id in (select uid from user_position where " +
            "pid IN (select pid from employer where id = #{employerId}));")
    List<User> selectListByEmployerId(Long employerId);

    @Update("UPDATE user SET image = #{imageUrl} where id = #{id}")
    void updateImageById(Long id, String imageUrl);

    @Select("select image from user where id = #{uid}")
    String selectImageById(Long uid);


    List<User> mlSelectBatchList(List<Long> targetIdList);

    /**
     * 查询理想城市
     * @param id
     * @return
     */
    @Select("select ideal_city from user where id = #{id}")
    String selectIdealCityById(Long id);

    /**
     * 查询最低期望工资
     * @param id
     * @return
     */
    @Select("select min_expected_salary from user where id = #{id}")
    Integer selectMinSalaryById(Long id);

    /**
     * 查询最高期望工资
     * @param id
     * @return
     */
    @Select("select max_expected_salary from user where id = #{id}")
    Integer selectMaxSalaryById(Long id);
}




