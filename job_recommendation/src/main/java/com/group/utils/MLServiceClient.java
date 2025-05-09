package com.group.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.group.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: mfz
 * @Date: 2024/03/10/16:31
 * @Description: 向机器学习脚本发送请求，接收脚本的会传参数
 */
@Service
@Slf4j
public class MLServiceClient {
      @Autowired
      private JdbcTemplate jdbcTemplate;

      //职位推荐访问url（有简历）
      private final String mlServiceUrl = "http://172.27.23.51:5000/ml_service_endpoint";
      //能力评价访问url
      private final String mlCapacityUrl = "http://172.27.23.51:5000/ml_capacity_endpoint";
      //职位推荐访问url（无简历）
      private final String mlNoResumeUrl = "http://172.27.23.51:5000/no_resume_service_endpoint"; //无简历URL

      //人才推荐url
      private final String mlUserUrl = "http://172.27.23.51:5000/user_recommendation";

      //填充 url
      private final String mlFillUrl = "http://172.27.23.51:5000/fill";

      //知识图谱可视化url
      private final String neo4jUrl = "http://172.27.23.51:5002/neo4j_endpoint";

    /**
     * 有简历职位推荐
     * @param parseInfo
     * @param userId
     * @return
     */
    public List<PositionWithScore> sendParseAndGetPositions(String parseInfo, Long userId) {
        //创建发送给机器学习的请求头
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");
        headers.set("userId",userId.toString());
        //创建HTTP请求体
        HttpEntity<String> request = new HttpEntity<>(parseInfo,headers);

//        log.info("机器学习请求体创建为："+request);
//        log.info("向公网url为"+mlServiceUrl+"的用户传递信息");
        //发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                mlServiceUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<JsonNode>() {}
        );
//        log.info("接收推荐结果完成，向后端传输result");
        //接收状态响应码(200为成功)和请求结果
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode result = response.getBody().get("result");
            if (result != null) {
                List<PositionWithScore> positionsWithScores = new ArrayList<>();
                for (JsonNode node : result) {
                    PositionWithScore positionWithScore = new PositionWithScore();
                    positionWithScore.setId(node.get(0).asLong());
                    positionWithScore.setScore(node.get(1).asInt());
                    positionsWithScores.add(positionWithScore);
                }
                return positionsWithScores;
            } else {
                log.info("未获得职位推荐");
                return null;
            }
        } else {
            log.info("机器学习脚本调用失败");
            return null;
        }
    }


    public String getCapacity(String userId){
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");

        //创建请求体
        HttpEntity<String> request = new HttpEntity<>(headers);
        log.info("机器学习请求体创建为："+request);
        log.info("向公网url为"+mlCapacityUrl+"的用户传递信息");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CapacityResponse> response = restTemplate.exchange(
                mlServiceUrl+"?userId="+userId,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<CapacityResponse>() {}
        );
        log.info("接收能力评价结果完成");
        CapacityResponse capacityResponse = response.getBody();
        return capacityResponse.getResult();
    }

    public List<Long> sendDescripAndGetUserId(String descrip) {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description",descrip);
        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(),headers);

//        log.info("机器学习请求体创建为："+request);
//        log.info("向公网url为"+mlUserUrl+"的用户传递信息");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserResponse> response = restTemplate.exchange(
                mlUserUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<UserResponse>() {}
        );

        if(response.getStatusCode()==HttpStatus.OK) {
            UserResponse userResponse = response.getBody();
            if(userResponse != null) {
                return userResponse.getResult();
            }else {
                log.info("未获得职位推荐");
                return null;
            }
        }else {
            log.info("机器学习脚本调用失败");
            return null;
        }

    }

    /**
     * 无简历下ML请求
     * @param noResumeInfo
     * @param id
     * @return
     */
    public List<PositionWithScore> sendNoResumeInfoAndGetPositions(UserNoResume noResumeInfo, Long id) {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");
        headers.set("userId",id.toString());
        //创建HTTP请求体
        HttpEntity<UserNoResume> request = new HttpEntity<>(noResumeInfo,headers);

//        log.info("机器学习请求体创建为："+request);
//        log.info("向公网url为"+mlNoResumeUrl+"的用户传递信息");
        //发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                mlNoResumeUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<JsonNode>() {}
        );
//        log.info("接收推荐结果完成，向后端传输result");
        //接收状态响应码(200为成功)和请求结果
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode result = response.getBody().get("result");
            if (result != null) {
                List<PositionWithScore> positionsWithScores = new ArrayList<>();
                for (JsonNode node : result) {
                    PositionWithScore positionWithScore = new PositionWithScore();
                    positionWithScore.setId(node.get(0).asLong());
                    positionWithScore.setScore(node.get(1).asInt());
                    positionsWithScores.add(positionWithScore);
                }
                return positionsWithScores;
            } else {
                log.info("未获得职位推荐");
                return null;
            }
        } else {
            log.info("机器学习脚本调用失败");
            return null;
        }
    }

    /**
     * 简历填充
     * @param id
     */
    public JsonNode UpdateExactById(Long id) {
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");
        headers.set("userId",id.toString());
        //创建HTTP请求体
        HttpEntity<String> request = new HttpEntity<>(headers);

//        log.info("机器学习请求体创建为："+request);
//        log.info("向公网url为"+mlFillUrl+"的用户传递信息");
        //发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                mlFillUrl,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<JsonNode>() {}
        );

        //接收状态响应码(200为成功)和请求结果
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode result = response.getBody().get("result");
            if (result != null) {
                // 更新数据库
                updateDatabase(id, result);
                return result;
            } else {
                log.info("未获得职位推荐");
                return null;
            }
        } else {
            log.info("机器学习脚本调用失败");
            return null;
        }
    }

    /**
     * jdbc更新数据库
     * @param id
     * @param result
     */
    private void updateDatabase(Long id, JsonNode result) {
        String birth = result.get("出生日期").asText();
        String workExperience = result.get("工作经历").asText();
        String awards = result.get("奖项荣誉").asText();
        String skills = result.get("个人技能").asText();
        String selfJudge = result.get("自我评价").asText();

        String sql = "UPDATE user_no_resume SET birth = ?, work_experience = ?, awards = ?, skills = ?, self_judge = ? WHERE id = ?";
        jdbcTemplate.update(sql, birth, workExperience, awards, skills, selfJudge, id);
    }

    /**
     * 知识图谱参数请求
     * @param info
     * @param userId
     * @return
     */
    public JsonNode getNeo4jGraph(String info, Long userId, Integer tag) {
//        try {
//            FileWriter ft = new FileWriter("new_resume_result.txt");
//            BufferedWriter bf = new BufferedWriter(ft);
//            bf.write(info.toString());
//            bf.close();
//            ft.close();
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-ML-Request","true");
        headers.set("userId",userId.toString());
        headers.set("tag",tag.toString());
        //创建HTTP请求体
        HttpEntity<String> request = new HttpEntity<>(info,headers);


//        log.info("机器学习请求体创建为："+request);
//        log.info("向url为"+neo4jUrl+"的用户传递信息");
        //发送POST请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(neo4jUrl, request, JsonNode.class);

        return response.getBody();
    }
}
