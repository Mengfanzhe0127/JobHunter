package com.group.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.group.pojo.Result;
import com.group.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: mfz
 * @Date: 2024/07/31/23:51
 * @Description:
 */
@RestController
@RequestMapping("/neo4j")
@CrossOrigin
public class Neo4jController {
    @Autowired
    private Neo4jService neo4jService;

    @GetMapping("/{id}")
    public Result getNeo4j(@PathVariable Long id) throws JsonProcessingException {
        JsonNode result = neo4jService.sendInfoGetNeo4j(id);
        return Result.success(result);
    }


}
