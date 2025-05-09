package com.group.service.impl;

/**
 * @Author: mfz
 * @Date: 2024/04/06/23:42
 * @Description:
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.mapper.UserCapacityMapper;
import com.group.pojo.*;
import com.group.service.UserCapacityService;
import com.group.utils.UUIDConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * @author 86152
 * @description 针对表【user_capacity(求职者技能表)】的数据库操作Service实现
 * @createDate 2024-04-06 23:39:24
 */
@Service
@Slf4j
public class UserCapacityServiceImpl extends ServiceImpl<UserCapacityMapper,UserCapacity>
        implements UserCapacityService {

    @Autowired
    private UserCapacityMapper userCapacityMapper;
    @Autowired
    private UUIDConverter uuidConverter;
    @Override
    public void saveUserCapacity(Long id, String parseInfo) {
        Integer primaryId = uuidConverter.convertToInt(UUID.randomUUID());
        UserCapacity uc = new UserCapacity(primaryId,id,parseInfo);
        userCapacityMapper.insert(uc);

    }

    @Override
    public PageBean selectUserBatchDescrip(int pageNum,int pageSize,List<String> descriptions) {
        List<User> targetList = new ArrayList<User>();
        for(String descrip : descriptions) {
            List<User> userList = userCapacityMapper.selectUserBatchDescrip("%"+descrip+"%");
            log.info("查询到的userList为"+userList);
            for(User i : userList) {
                targetList.add(i);
            }
        }
        if(targetList == null) {
            return null;
        }
        targetList = removeDuplicate(targetList);
        log.info("targetList为"+targetList);

//        List<UserImage> imageUserList = new ArrayList<>();
//        for(User u : targetList) {
//            String image = randomImage();
//            UserImage userImage = new UserImage(u.getId(),u.getNickname(),u.getBirth(),u.getMinExpectedSalary(),
//                    u.getMaxExpectedSalary(),
//                    u.getIdealCity(),u.getEducation(),u.getType(),u.getSchool(),u.getMajor(),
//                    u.getName(),u.getPhone(),u.getEmail(),
//                    u.getAddress(),u.getPassword(),u.getCharacterType(),u.getResume(),image);
//            imageUserList.add(userImage);
//        }

        List<StrUser> strUserList = new ArrayList<>();
        for(User u : targetList) {
            String image = randomImage();
            String id = u.getId().toString();
            StrUser strUser = new StrUser(id,u.getNickname(),u.getBirth(),u.getMinExpectedSalary(),
                    u.getMaxExpectedSalary(),
                    u.getIdealCity(),u.getEducation(),u.getType(),u.getSchool(),u.getMajor(),
                    u.getName(),u.getPhone(),u.getEmail(),
                    u.getAddress(),u.getPassword(),u.getCharacterType(),u.getResume(),image);
            strUserList.add(strUser);
        }

        log.info(strUserList.toString());
        int total = strUserList.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        List<StrUser> userPage = strUserList.subList(startIndex,endIndex);
        PageBean pageBean = new PageBean();
        pageBean.setTotal(total);
        pageBean.setRows(userPage);

        return pageBean;
    }

    @Override
    public UserCapacity getUserCapacity(Long id) {
        return userCapacityMapper.selectDataById(id);
    }

    private List<User> removeDuplicate(List<User> userList) {
        return userList.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private String randomImage() {
        String image0 = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/%E5%A4%B4%E5%83%8F1.jpg";
        String image1 = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/%E5%A4%B4%E5%83%8F2.jpg";
        String image2 = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/%E5%A4%B4%E5%83%8F3.jpg";
        String image3 = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/%E5%A4%B4%E5%83%8F4.jpg";
        String image4 = "https://web-job-recommendation.oss-cn-chengdu.aliyuncs.com/%E5%A4%B4%E5%83%8F5.jpg";
        int randomNum = ((int)(Math.random()*1000))%5;
        switch(randomNum) {
            case 0 : return image0;
            case 1 : return image1;
            case 2 : return image2;
            case 3 : return image3;
            case 4 : return image4;
        }
        return null;
    }





}
