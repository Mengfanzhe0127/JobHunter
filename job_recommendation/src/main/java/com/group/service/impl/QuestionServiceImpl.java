package com.group.service.impl;

import com.group.mapper.QuestionMapper;
import com.group.pojo.PageBean;
import com.group.pojo.Position;
import com.group.pojo.Question;
import com.group.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: mfz
 * @Date: 2024/07/19/12:59
 * @Description:
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private static final String CORRECT_ANSWER1 = "struct ListNode* ReverseList(struct ListNode* pHead ) { if(pHead == NULL) return NULL; if(pHead->next == NULL) return pHead; struct ListNode *p = NULL; struct ListNode *temp = NULL; p = pHead->next; pHead->next = NULL; while(p->next != NULL) { temp = p->next; p->next = pHead; pHead = p; p = temp; } p->next = pHead; pHead = p; return pHead; }";
    private static final String CORRECT_ANSWER2 = "int search(int* nums, int numsLen, int target ) { if(numsLen==0) return -1; int low=0,mid=numsLen/2,high=numsLen-1; while(low<=high) { if(nums[mid]==target) return mid; else if(nums[mid]<target) { low=mid+1; mid=(high+low)/2; } else { high=mid-1; mid=(high+low)/2; } } return -1; }";
    private static final String CORRECT_ANSWER3 = "private static final String CORRECT_ANSWER = \"public String validIPAddress(String IP) { return validIPv4(IP) ? \\\"IPv4\\\" : (validIPv6(IP) ? \\\"IPv6\\\" : \\\"Neither\\\"); } private boolean validIPv4(String IP) { String[] strs = IP.split(\\\"\\\\\\\\.\\\", -1); if (strs.length != 4) { return false; } for (String str : strs) { if (str.length() > 1 && str.startsWith(\\\"0\\\")) { return false; } try { int val = Integer.parseInt(str); if (!(val >= 0 && val <= 255)) { return false; } } catch (NumberFormatException numberFormatException) { return false; } } return true; } private boolean validIPv6(String IP) { String[] strs = IP.split(\\\":\\\", -1); if (strs.length != 8) { return false; } for (String str : strs) { if (str.length() > 4 || str.length() == 0) { return false; } try { int val = Integer.parseInt(str, 16); } catch (NumberFormatException numberFormatException) { return false; } } return true; }\";";
    private static final String CORRECT_ANSWER4 = "SELECT DATE_FORMAT(t_time, '%Y-%m') AS time, SUM(t_amount) AS total FROM trade JOIN customer ON trade.t_cus = customer.c_id WHERE customer.c_name = 'Tom' AND t_type = 1 AND YEAR(t_time) = 2023 GROUP BY DATE_FORMAT(t_time, '%Y-%m') ORDER BY time;";

    @Autowired
    private QuestionMapper questionMapper;
    @Override
    public PageBean getQues(int pageNum,int pageSize) {
        List<Question> quesList = questionMapper.selectQues();
        int total = quesList.size();
//        int startIndex = (pageNum - 1) * pageSize;
//        int endIndex = Math.min(startIndex + pageSize, total);
        PageBean pageBean = new PageBean();
        pageBean.setTotal(total);
        pageBean.setRows(quesList);

        return pageBean;
    }

    @Override
    public Integer getMask1(String answer) {
        int score = 0;

        // 去除多余的空格
        String trimmedAnswer = answer.trim();

        if (CORRECT_ANSWER1.equals(trimmedAnswer)) {
            score = 100;
        } else {
            if (trimmedAnswer.contains("struct ListNode") && trimmedAnswer.contains("return") && trimmedAnswer.contains("NULL")) {
                score += 30;
            }
            if (trimmedAnswer.contains("while") && trimmedAnswer.contains("if") && trimmedAnswer.contains("p->next")) {
                score += 30;
            }
            if (trimmedAnswer.contains("pHead->next") && trimmedAnswer.contains("p->next") && trimmedAnswer.contains("temp")) {
                score += 20;
            }
            if (trimmedAnswer.contains("return pHead")) {
                score += 20;
            }
        }

        return score;
    }

    @Override
    public Integer getMask2(String answer) {
        int score = 0;

        // 去除多余的空格
        String trimmedAnswer = answer.trim();

        if (CORRECT_ANSWER2.equals(trimmedAnswer)) {
            score = 100;
        } else {
            if (trimmedAnswer.contains("int") && trimmedAnswer.contains("return") && trimmedAnswer.contains("while")) {
                score += 30;
            }

            if (trimmedAnswer.contains("if") && trimmedAnswer.contains("else if") && trimmedAnswer.contains("else")) {
                score += 30;
            }

            if (trimmedAnswer.contains("low") && trimmedAnswer.contains("mid") && trimmedAnswer.contains("high")) {
                score += 20;
            }

            if (trimmedAnswer.contains("return -1") && trimmedAnswer.contains("return mid")) {
                score += 20;
            }
        }

        return score;
    }

    @Override
    public Integer getMask3(String answer) {
        int score = 0;

        // 去除多余的空格
        String trimmedAnswer = answer.trim();

        if (CORRECT_ANSWER3.equals(trimmedAnswer)) {
            score = 100;
        } else {
            if (trimmedAnswer.contains("public String validIPAddress") && trimmedAnswer.contains("return") && trimmedAnswer.contains("boolean")) {
                score += 30;
            }
            if (trimmedAnswer.contains("if") && trimmedAnswer.contains("for") && trimmedAnswer.contains("try")) {
                score += 30;
            }
            if (trimmedAnswer.contains("split") && trimmedAnswer.contains("Integer.parseInt") && trimmedAnswer.contains("NumberFormatException")) {
                score += 20;
            }
            if (trimmedAnswer.contains("return false") && trimmedAnswer.contains("return true")) {
                score += 20;
            }
        }

        return score;
    }

    @Override
    public Integer getMask4(String answer) {
        int score = 0;

        // 去除多余的空格
        String trimmedAnswer = answer.trim();

        // 完全正确的答案
        if (CORRECT_ANSWER4.equals(trimmedAnswer)) {
            score = 100;
        } else {
            // 检查是否包含必要的关键字
            if (trimmedAnswer.contains("SELECT") && trimmedAnswer.contains("FROM") && trimmedAnswer.contains("WHERE")) {
                score += 30;
            }

            if (trimmedAnswer.contains("trade") && trimmedAnswer.contains("customer") && trimmedAnswer.contains("t_time") && trimmedAnswer.contains("t_amount")) {
                score += 30;
            }

            if (trimmedAnswer.contains("customer.c_name = 'Tom'") && trimmedAnswer.contains("t_type = 1") && trimmedAnswer.contains("YEAR(t_time) = 2023")) {
                score += 20;
            }

            if (trimmedAnswer.contains("GROUP BY") && trimmedAnswer.contains("ORDER BY")) {
                score += 20;
            }
        }

        return score;
    }

    @Override
    public Integer getMask5(String answer) {
        int score = 0;
        if(answer.contains("定时")) {
            score += 30;
        }
        if(answer.contains("懒惰")) {
            score += 30;
        }
        if(answer.contains("定期")) {
            score += 20;
        }
        if(answer.contains("CPU") || answer.contains("数据库") || answer.contains("缓存")) {
            score += 20;
        }
        return score;
    }

    @Override
    public Integer getMask6(String answer) {
        int score = 0;
        if(answer.contains("连接")) {
            score += 30;
        }
        if(answer.contains("可靠")) {
            score += 30;
        }
        if(answer.contains("速度")) {
            score += 20;
        }
        if(answer.contains("应用场景")) {
            score += 20;
        }
        return score;
    }
}
