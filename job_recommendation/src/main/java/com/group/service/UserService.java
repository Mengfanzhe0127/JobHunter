package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.User;

/**
* @author mfz
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-23 00:20:25
*/
public interface UserService extends IService<User> {
    /**
     * 求职者注册
     * @param name
     * @param email
     * @param phone
     * @param password
     */
    void userRegister(byte[] name, byte[] email, byte[] phone, byte[] password);

    /**
     * 生成ID
     * @return
     */
    Long getUniqueIdBySnowFlake();

    /**
     * 求职者登陆
     * @param phoneOrEmail
     * @param password
     * @return
     */
    User userLogin(byte[] phoneOrEmail, byte[] password);

    /**
     * 求职者查询个人信息
     * @param id
     * @return
     */
    User encryptGetById(Long id);

    /**
     * 求职者修改个人信息
     * @param u
     * @return
     */
     void encryptUpdate(User u);
}
