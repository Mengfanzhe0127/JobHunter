package com.group.utils;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.group.mapper.UserPositionMapper;
import com.group.pojo.UserPosition;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/03/20/21:29
 * @Description:
 */
public class UserPositionsInjector extends DefaultSqlInjector {
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass,tableInfo);
        methodList.add(new InsertBatchSomeColumn());
        return methodList;

    }
}
