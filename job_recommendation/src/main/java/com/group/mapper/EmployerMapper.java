package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.Employer;
import com.group.pojo.Position;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author mfz
* @description 针对表【employer(招聘者表)】的数据库操作Mapper
* @createDate 2024-01-23 00:23:15
* @Entity com.group.pojo.Employer
*/
public interface EmployerMapper extends BaseMapper<Employer> {
    /**
     * 招聘者注册
     * 插入真实姓名，邮箱，公司名称，密码
     * @param name
     * @param email
     * @param company
     * @param password
     */
    @Insert("insert into employer(id,name,email,company,password) values " +
            "(#{id}," +
            "AES_ENCRYPT(#{name},'name_secret_key','AES/CBC/PKCS5Padding')," +
            "AES_ENCRYPT(#{email},'email_secret_key','AES/CBC/PKCS5Padding')," +
            "#{company}," +
            "AES_ENCRYPT(#{password},'password_secret_key','AES/CBC/PKCS5Padding'))")
    void insertByNameEmailCompanyPassword(Long id,byte[] name, byte[] email, String company, byte[] password);

    /**
     * 招聘者邮箱登录
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from employer where AES_DECRYPT(email,'email_secret_key','AES/CBC/PKCS5Padding') = #{phoneOrEmail} " +
            "and AES_DECRYPT(password,'password_secret_key','AES/CBC/PKCS5Padding') = #{password}")
    Employer getByEmailAndPassword(byte[] phoneOrEmail,byte[] password);

    /**
     * 招聘者查看个人信息
     * @param id
     * @return
     */
    @Select("SELECT id, AES_DECRYPT(name, 'name_secret_key', 'AES/CBC/PKCS5Padding') AS name,\n" +
            "       duty, AES_DECRYPT(phone, 'phone_secret_key', 'AES/CBC/PKCS5Padding') AS phone,\n" +
            "       AES_DECRYPT(email, 'email_secret_key', 'AES/CBC/PKCS5Padding') AS email,\n" +
            "       AES_DECRYPT(password, 'password_secret_key', 'AES/CBC/PKCS5Padding') AS password,\n" +
            "       company,character_type,image\n" +
            "FROM employer WHERE id = #{id};")
    Employer encryptSelectById(Long id);

    /**
     * 求职者修改个人信息
     * 动态SQL
     * @param employer
     */
    void encryptUpdate(Employer employer);

    /**
     * 招聘者得到个人发布职位信息
     * @param employerId
     * @return
     */
    @Select("select * from position where eid = #{employerId}")
    List<Position> selectPositionsById(Long employerId);


    @Update("UPDATE employer SET image = #{imageUrl} where id = #{id}")
    void updateImageById(Long id, String imageUrl);

    @Select("select image from employer where id = #{l}")
    String selectImageById(long l);
}




