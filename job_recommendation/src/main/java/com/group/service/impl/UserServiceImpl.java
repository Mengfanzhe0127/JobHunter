package com.group.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.UserMapper;
import com.group.pojo.User;
import com.group.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author mfz
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-23 00:20:25
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;
    @Override
    public void userRegister(byte[] name, byte[] email, byte[] phone, byte[] password) {
        //利用雪花算法为用户生成唯一Id
        Long id = getUniqueIdBySnowFlake();
        userMapper.insertByNameEmailPhonePassword(id,name,email,phone,password);
    }

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Override
    public Long getUniqueIdBySnowFlake() {

        return idGenerator.snowFlakeId();
    }

    @Override
    public User userLogin(byte[] phoneOrEmail, byte[] password) {
        //将byte数组转换为字符串
        String phoneOrEmailStr = new String(phoneOrEmail);
        //判断邮箱or手机号
        boolean isPhone = phoneOrEmailStr.matches("^1[3-9]\\d{9}$"); // 使用正则表达式匹配手机号
        //根据手机号或邮箱查询用户
        User user = isPhone ? userMapper.getByPhoneAndPassword(phoneOrEmail,password) : userMapper.getByEmailAndPassword(phoneOrEmail,password);
        //如果用户存在，比较密码是否正确
        if (user != null) {
            return user; // 密码正确，返回用户
        } else {
            return null; // 用户不存在或密码错误，返回null
        }
    }

}




