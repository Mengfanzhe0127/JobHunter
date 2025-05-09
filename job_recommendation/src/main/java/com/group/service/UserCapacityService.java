package com.group.service;

/**
 * @Author: mfz
 * @Date: 2024/04/06/23:41
 * @Description:
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.group.pojo.PageBean;
import com.group.pojo.User;
import com.group.pojo.UserCapacity;

import java.util.List;


/**
 * @author 86152
 * @description 针对表【user_capacity(求职者技能表)】的数据库操作Service
 * @createDate 2024-04-06 23:39:24
 */
public interface UserCapacityService extends IService<UserCapacity> {
    /**
     * 存储简历解析
     * @param id
     * @param parseInfo
     */
    void saveUserCapacity(Long id, String parseInfo);

    /**
     * 根据描述查询User
     *
     * @param descriptions
     * @return
     */
    PageBean selectUserBatchDescrip(int pageNum, int pageSize, List<String> descriptions);

    /**
     * 查询求职者能力记录
     * @param id
     * @return
     */
    UserCapacity getUserCapacity(Long id);
}
