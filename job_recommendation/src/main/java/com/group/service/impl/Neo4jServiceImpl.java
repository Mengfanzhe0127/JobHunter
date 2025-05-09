package com.group.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group.mapper.UserCapacityMapper;
import com.group.mapper.UserMapper;
import com.group.mapper.UserNoResumeMapper;
import com.group.pojo.User;
import com.group.pojo.UserNoResume;
import com.group.service.Neo4jService;
import com.group.service.UserCapacityService;
import com.group.utils.MLServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/07/30/16:01
 * @Description:
 */
@Service
@Slf4j
public class Neo4jServiceImpl implements Neo4jService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCapacityMapper userCapacityMapper;

    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private UserNoResumeMapper userNoResumeMapper;

    @Override
    public JsonNode sendInfoGetNeo4j(Long id) throws JsonProcessingException {
        String capacity = userCapacityMapper.selectCapacityById(id);
        User userInfo = userMapper.encryptSelectById(id);
//        log.info("userInfo为：{}", userInfo.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String realInfo = objectMapper.writeValueAsString(userInfo);
        JsonNode jsonNode = objectMapper.readTree(realInfo);
        ((ObjectNode) jsonNode).put("birth", userInfo.getBirth().toString());
        realInfo = jsonNode.toString();
//        log.info("realInfo为：{}", realInfo);

        if (capacity != null) {
            JsonNode infoNode = objectMapper.readTree(capacity);
            return mlServiceClient.getNeo4jGraph(infoNode.toString(), id, 1);
        }

        UserNoResume noResumeCapacity = userNoResumeMapper.selectById(id);
        if (noResumeCapacity != null) {
            String noResumeJson = objectMapper.writeValueAsString(noResumeCapacity);
            JsonNode infoNode = objectMapper.readTree(noResumeJson);
            ((ObjectNode) infoNode).put("birth", userInfo.getBirth().toString());
            JsonNode userNode = objectMapper.readTree(realInfo);
            ((ObjectNode) infoNode).set("user", userNode);
            return mlServiceClient.getNeo4jGraph(infoNode.toString(), id, 0);
        }

        return null;
    }
}
