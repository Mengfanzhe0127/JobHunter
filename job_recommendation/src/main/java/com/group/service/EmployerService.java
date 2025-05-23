package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.Employer;
import com.group.pojo.Position;

import java.util.List;

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

    /**
     * 招聘者查看个人信息
     * @param id
     * @return
     */
    Employer encryptGetById(Long id);

    /**
     * 招聘者修改个人信息
     * @param employer
     */
    void encryptUpdate(Employer employer);

    /**
     * 招聘者查看自己发布的招聘职位
     * @param employerId
     * @return
     */
    List<Position> employerGetPositions(Long employerId);

    /**
     * 招聘者上传头像
     * @param id
     * @param imageUrl
     */
    void updateImageById(Long id, String imageUrl);

    String getImageById(long l);


}
