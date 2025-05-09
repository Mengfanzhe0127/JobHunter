package com.group.service;

import com.group.pojo.PageBean;

/**
 * @Author: mfz
 * @Date: 2024/07/19/12:59
 * @Description:
 */
public interface QuestionService {
    /**
     * 获取题目列表
     * @param pageNum
     * @param pagSize
     * @return
     */
    PageBean getQues(int pageNum,int pagSize);

    /**
     * 反转链表题
     * @return
     */
    Integer getMask1(String answer);

    /**
     * 二分查找
     * @return
     */
    Integer getMask2(String answer);

    /**
     * IP判断
     * @return
     */
    Integer getMask3(String answer);

    /**
     * SQL
     * @return
     */
    Integer getMask4(String answer);

    /**
     * Redis简答
     * @param answer
     * @return
     */
    Integer getMask5(String answer);

    /**
     * tcp,udp区别
     * @param answer
     * @return
     */
    Integer getMask6(String answer);
}
