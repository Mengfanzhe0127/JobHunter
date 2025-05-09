package com.group.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @Author: mfz
 * @Date: 2024/07/30/16:00
 * @Description:
 */
public interface Neo4jService {
    /**
     * 获取Neo4j参数
     * @param id
     * @return
     */
    JsonNode sendInfoGetNeo4j(Long id) throws JsonProcessingException;
}
