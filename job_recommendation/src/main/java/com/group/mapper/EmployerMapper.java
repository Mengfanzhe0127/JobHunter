package com.group.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.group.pojo.Employer;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

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
    @Insert("insert into employer(name,email,company,password) values (AES_ENCRYPT(#{name},'name_secret_key')," +
            "AES_ENCRYPT(#{email},'email_secret_key'),#{company}," +
            "AES_ENCRYPT(#{password},'password_secret_key'))")
    void insertByNameEmailCompanyPassword(byte[] name, byte[] email, String company, byte[] password);

    /**
     * 招聘者邮箱登录
     * @param phoneOrEmail
     * @param password
     * @return
     */
    @Select("select * from employer where AES_DECRYPT(email,'email_secret_key') = #{phoneOrEmail} " +
            "and AES_DECRYPT(password,'password_secret_key') = #{password}")
    Employer getByEmailAndPassword(byte[] phoneOrEmail,byte[] password);
}




