package com.group.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.group.anno.Encrypt;
import com.group.pojo.*;
import com.group.service.EmployerService;
import com.group.service.MLService;
import com.group.service.UserCapacityService;
import com.group.service.UserService;
import com.group.utils.AliOSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/employer")
@CrossOrigin
public class EmployerController {
    @Autowired
    private EmployerService employerService;

    @Autowired
    private UserService userService;

    @Autowired
    private AliOSSProperties aliOSSProperties;

    @Autowired
    private UserCapacityService userCapacityService;

    @Autowired
    private MLService mlService;

    /**
     * 招聘者查看个人信息
     * @param id
     * @return
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    @Encrypt
    public Result employerGetById(@PathVariable("id") Long id) {
        Employer e = employerService.encryptGetById(id);
        String employerId = e.getId().toString();
        Map<String,Object> employerInfoMap = new HashMap<>();
        employerInfoMap.put("employerId",employerId);
        employerInfoMap.put("employer",e);
        log.info("招聘者查询个人信息：{}",e);
        return Result.success(employerInfoMap);
    }

    /**
     * 招聘者修改个人信息
     * @param employer
     * @return
     */
    @CrossOrigin(origins = "*")
    @PutMapping
    @Encrypt
    public Result employerUpdate(@RequestBody Employer employer) {
        log.info("招聘者修改个人信息：{}",employer);
        //json务必携带Id
        employerService.encryptUpdate(employer);
        return Result.success();
    }

    /**
     * 招聘者获得推荐求职者
     * @param employerId
     * @param pageNum
     * @param pageSize
     * @return
     * @throws Exception
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/recommendation/{employerId}")
    public Result employerGetUsers(@PathVariable Long employerId,@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "4") int pageSize,
                                   @RequestParam(required = false) List<String> descriptions,
                                   @RequestParam(required = false) MultipartFile demand) throws Exception{
        /*
          demand文件云存储
         */
        if(demand != null && !demand.isEmpty()) {
            String endpoint= aliOSSProperties.getEndpoint();
            String accessKeyId= aliOSSProperties.getAccessKeyId();
            String accessKeySecret = aliOSSProperties.getAccessKeySecret();
            String bucketName = "web-employee-recommendation";

            // 获取上传的文件的输入流
            InputStream inputStream = demand.getInputStream();

            // 避免文件覆盖
            String originalFilename = demand.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

            //上传文件到 OSS
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, fileName, inputStream);

            //文件访问路径
            String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
            // 关闭ossClient
            ossClient.shutdown();
        }
        PageBean capacityUsers = null;
        if(descriptions != null) {
//            capacityUsers  = userCapacityService.selectUserBatchDescrip(pageNum,pageSize,descriptions);
            capacityUsers = mlService.selectUserByDescripAndML(pageNum,pageSize,descriptions);

        }else {
            capacityUsers  = userCapacityService.selectUserBatchDescrip(pageNum,pageSize,descriptions);
        }
        log.info("capacityUsers为"+capacityUsers);
//        PageBean userResult = userService.getUsersByEmployerId(employerId,pageNum,pageSize,description);

        return (capacityUsers!=null)?Result.success(capacityUsers):Result.error("未匹配到期望人员");
    }


    /**
     * 招聘者获得发布职位
     * @param employerId
     * @return
     */
    @GetMapping("/position")
    @CrossOrigin
    public Result employerGetPositions(@RequestParam Long employerId) {
        log.info("招聘者"+employerId+"查询发布职位");
        List<Position> positionList = employerService.employerGetPositions(employerId);
        return Result.success(positionList);
    }

}
