package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.Employer;

/**
* @author mfz
* @description 针对表【employer(招聘者表)】的数据库操作Service
* @createDate 2024-01-23 00:23:15
*/
public interface EmployerService extends IService<Employer> {
    /**
     * 招聘者注册
     * @param name
     * @param email
     * @param company
     * @param password
     */
    void employerRegister(byte[] name, byte[] email, String company, byte[] password);

    /**
     * 生成ID
     * @return
     */
    Long getUniqueIdBySnowFlake();

    /**
     * 招聘者登陆
     * @param phoneOrEmail
     * @param password
     * @return
     */
    Employer employerLogin(byte[] phoneOrEmail, byte[] password);
}
