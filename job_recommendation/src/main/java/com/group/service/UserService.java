package com.group.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.PageBean;
import com.group.pojo.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.sql.Date;

/**
* @author mfz
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-23 00:20:25
*/
public interface UserService extends IService<User> {
    /**
     * 求职者注册
     * @param name     * @param email
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

    /**
     *
     * @param id
     * @return
     */
    Date getBirthById(Long id);

    /**
     * 将简历url保存进数据库
     * @param url
     * @param id
     */
    void updateResume(String url,Long id);

    /**
     * 根据ID获取用户建立url
     * @param id
     * @return
     */
    String getResumeById(Long id);

    /**
     * 更新缓存中的resume字段
     * @param id
     */
    void refreshUserInfoCache(Long id);

    /**
     * 招聘者获取目标求职者
     * @param employerId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageBean getUsersByEmployerId(Long employerId,int pageNum,int pageSize,String description);

    /**
     * 求职者获得能力评价
     * @param userId
     * @return
     */
    SseEmitter userGetCapacity(Long userId);

    /**
     * 流式获得能力评价
     * @param userId
     */
    void userGetCapacity2(Long userId);

    /**
     * 更新求职者头像
     * @param id
     * @param imageUrl
     */
    void updateImageById(Long id, String imageUrl);

    String getImageById(Long uid);
}
