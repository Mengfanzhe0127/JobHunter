package com.group.controller;

import com.group.anno.Encrypt;
import com.group.pojo.*;
import com.group.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserExperienceService userExperienceService;

    @Autowired
    private UserSelfJudgeService userSelfJudgeService;

    @Autowired
    private UserNoResumeService userNoResumeService;

    @Autowired
    private Neo4jService neo4jService;


    /**
     * 求职者查询个人信息
     * @param id
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    @Encrypt
    public Result userGetById(@PathVariable("id") Long id) {
        User u = userService.encryptGetById(id);
        if(u == null) {
            return Result.error("用户不存在");
        }
        UserNoCity userNoCity = new UserNoCity(u.getId(),u.getNickname(),u.getBirth(),u.getMinExpectedSalary(),
                u.getMaxExpectedSalary(),u.getEducation(),u.getType(),u.getSchool(),u.getMajor(),u.getName(),u.getPhone(),
                u.getEmail(),u.getAddress(),u.getPassword(),u.getCharacterType(),u.getResume(),u.getImage());
//        String[] idealCityArr ;
        UserExperience userExperience = userExperienceService.getById(id);
        UserSelfJudge userSelfJudge = userSelfJudgeService.getById(id);
        UserNoResume userNoResume = userNoResumeService.getById(id);
        if(u.getIdealCity() != null && !u.getIdealCity().isEmpty()) {

             String[] idealCityArr = u.getIdealCity().split(",");
             String userId = u.getId().toString();
             Map<String,Object> userInfoMap = new HashMap<>();
             userInfoMap.put("userId",userId);
             userInfoMap.put("user",userNoCity);
             userInfoMap.put("idealCityArr",idealCityArr);
             userInfoMap.put("userExperience",userExperience);
             userInfoMap.put("userSelfJudge",userSelfJudge);
             userInfoMap.put("userNoResume",userNoResume);
             log.info("求职者查询个人信息：{}",u);
             return Result.success(userInfoMap);
        }else {
            Integer[] idealCityArr = new Integer[0];
            String userId = u.getId().toString();
            Map<String,Object> userInfoMap = new HashMap<>();
            userInfoMap.put("userId",userId);
            userInfoMap.put("user",userNoCity);
            userInfoMap.put("idealCityArr",idealCityArr);
            userInfoMap.put("userExperience",userExperience);
            userInfoMap.put("userSelfJudge",userSelfJudge);
            userInfoMap.put("userNoResume",userNoResume);
//            log.info(userInfoMap.toString());
            log.info("求职者查询个人信息：{}",u);
            return Result.success(userInfoMap);

        }

    }

    /**
     * 求职者修改个人信息
     * @param userJson
     * @return
     */
    @CrossOrigin(origins = "*")
    @PutMapping
    @Encrypt
    public Result userUpdate(@RequestBody UserJson userJson) throws Exception{
//        User u = userService.encryptGetById(id);
        User user = userJson.getUser();
        user.setIdealCity(String.join(",",userJson.getIdealCityArr()));

        log.info("求职者修改信息：{}",user);
        userService.encryptUpdate(user);
//        log.info("求职者修改信息完毕,修改后信息：{}",u);
        Long userId = user.getId();
        String[] experiences = userJson.getExperience();
        String judge = userJson.getJudge();
        if(userExperienceService.getById(userId) != null) {
            if(experiences[0] != null) {
                userExperienceService.updateCurPos(userId, experiences[0]);
            }
            if(experiences[1] != null) {
                userExperienceService.updateCurCompany(userId,experiences[1]);
            }
            if(experiences[2] != null) {
                userExperienceService.updateWorkDescrip(userId,experiences[2]);
            }
        }else {
            userExperienceService.insertExperience(userId,experiences[0],experiences[1],experiences[2]);
        }

        if(userSelfJudgeService.getById(userId) != null) {
            userSelfJudgeService.updateJudge(userId,judge);
        }else {
            userSelfJudgeService.insertJudge(userId,judge);
        }

        if(userNoResumeService.getById(userId) != null) {
            userNoResumeService.updateInfo(userId,userJson.getUserNoResume());
        }else {
            userNoResumeService.insertInfo(userId,userJson.getUserNoResume());
        }


        return Result.success();
    }

    /**
     * 求职者获得能力评价
     * @param userId
     * @return
     */
    @CrossOrigin
    @GetMapping(value="/capacity",produces = "text/event-stream")
    public void userCapacity(@RequestParam Long userId) {
        userService.userGetCapacity2(userId);
    }



}
