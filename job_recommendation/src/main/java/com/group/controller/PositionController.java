package com.group.controller;

import com.group.Component.IDGeneratorSnowFlake;
import com.group.mapper.UserMapper;
import com.group.pojo.PageBean;
import com.group.pojo.Position;
import com.group.pojo.Result;
import com.group.pojo.User;
import com.group.service.EmployerService;
import com.group.service.PositionService;
import com.group.service.UserPositionService;
import com.group.service.UserService;
import com.group.utils.MLServiceClient;
import com.group.utils.ResumeSDKParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.group.utils.ResumeSDKConstants.APP_CODE;
import static com.group.utils.ResumeSDKConstants.ResumeSDK_URL;

@Slf4j
@RestController
@RequestMapping("/position")
@CrossOrigin
public class PositionController {
    @Autowired
    private PositionService positionService;

    @Autowired
    private UserService userService;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private UserPositionService userPositionService;

    @Autowired
    private IDGeneratorSnowFlake idGenerator;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户简历(可无)得到职位推荐
     * @param id
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public Result getPositions(@PathVariable("id") Long id, @RequestParam(defaultValue = "1") int pageNum,
                                      @RequestParam(defaultValue = "8") int pageSize) throws Exception{
//        log.info("ID为"+id+"的求职者发送获取职位推荐请求");
//        User u = userService.encryptGetById(id);
        String resume = userMapper.selectResumeById(id);
//        log.info("ID为{}的用户简历为{}",id,resume);
        if(resume != null) {
            PageBean positionResult = positionService.getPositionsByML2(id, pageNum, pageSize);
            Result positionResponse = Result.success(positionResult);
            return positionResponse;
        }else {

            //用户未上传简历
            PageBean positionResult = positionService.getPositionByML3(id,pageNum,pageSize);
            return Result.success(positionResult);
        }
    }

    /**
     * 招聘者发布招聘职位
     * @param employerId
     * @param position
     * @return
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/{employerId}")
    public Result employerAddPosition(@PathVariable Long employerId, @RequestBody Position position) {
        Long positionId = idGenerator.snowFlakeId();
        position.setId(positionId);
        position.setEid(employerId);
        positionService.addPosition(position);
        log.info("ID为："+employerId+"的招聘者成功发布职位："+position);
        return Result.success(position);
//        Result employerPositionResponse = Result.success(position);

        //异步执行将参数写入position
//        CompletableFuture.runAsync(() -> {
//            positionService.saveEid(employerId,positionId);
//        });

//        return employerPositionResponse;
    }


}
