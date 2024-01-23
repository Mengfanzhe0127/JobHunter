package com.group.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.group.mapper.PositionMapper;
import com.group.pojo.Position;
import com.group.service.PositionService;
import org.springframework.stereotype.Service;

/**
* @author mfz
* @description 针对表【position(职位表)】的数据库操作Service实现
* @createDate 2024-01-23 00:33:46
*/
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position>
    implements PositionService{

}




