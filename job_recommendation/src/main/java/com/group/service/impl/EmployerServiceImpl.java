package com.group.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.EmployerMapper;
import com.group.pojo.Employer;
import com.group.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author mfz
* @description 针对表【employer(招聘者表)】的数据库操作Service实现
* @createDate 2024-01-23 00:23:15
*/
@Service
public class EmployerServiceImpl extends ServiceImpl<EmployerMapper, Employer>
    implements EmployerService{
    @Autowired
    private EmployerMapper employerMapper;

    @Override
    public void employerRegister(byte[] name, byte[] email, String company, byte[] password) {
        //利用雪花算法为用户生成唯一Id
//        Long id = getUniqueIdBySnowFlake();
        employerMapper.insertByNameEmailCompanyPassword(name,email,company,password);
    }

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Override
    public Long getUniqueIdBySnowFlake() {

        return idGenerator.snowFlakeId();
    }

    @Override
    public Employer employerLogin(byte[] phoneOrEmail, byte[] password) {
//        String phoneOrEmailStr = new String(phoneOrEmail);
//        boolean isPhone = phoneOrEmailStr.matches("^1[3-9]\\d{9}$");

        Employer employer = employerMapper.getByEmailAndPassword(phoneOrEmail,password);
        if(employer != null) {
            return employer;
        }else {
            return null;
        }
    }

    @Override
    public Employer encryptGetById(Long id) {
        return employerMapper.encryptSelectById(id);
    }

    @Override
    public void encryptUpdate(Employer employer) {
        employerMapper.encryptUpdate(employer);
    }
}




