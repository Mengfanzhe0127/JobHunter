package com.group.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONObject;


/**
 * @Author: mfz
 * @Date: 2024/03/04/21:18
 * @Description: 数据解析接口调用
 */
@Slf4j
@Component
public class ResumeSDKParseUtils {
    public static String parser(String url, String fname, String appcode) throws Exception {
        // 设置头字段
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "APPCODE " + appcode);
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.addHeader("Content-Type", "application/json");

        // 读取简历内容
        // 修改：直接使用URL图片的字节数组，而不需要进行Base64编码
        URL resumeUrl = new URL(fname);
        InputStream in = resumeUrl.openStream();
        byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(in);
        String data = new String(Base64.encodeBase64(bytes), Consts.UTF_8);

        // 设置内容信息
        JSONObject json = new JSONObject();
        json.put("file_name", fname);	// 文件名
        json.put("file_cont", data);	// 经base64编码过的文件内容
        json.put("need_avatar", 0);		// 是否需要解析头像
        json.put("ocr_type", 1);		// 1为高级ocr
        StringEntity params = new StringEntity(json.toString(), Consts.UTF_8);
        httpPost.setEntity(params);

        // 发送请求
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = httpclient.execute(httpPost);

        // 处理返回结果
        String resCont = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
//        System.out.println(resCont);
        log.info("resCont:"+resCont);
        JSONObject res = new JSONObject(resCont);
        JSONObject result = res.getJSONObject("result");
//        System.out.println(res);
//        log.info(res.toString());
//        System.out.println(res.toString(4));

        //本地测试
//        try(BufferedWriter writer = new BufferedWriter(new FileWriter("resume_result.txt"))) {
//            writer.write(result.toString(4));
//        }catch(IOException e) {
//            log.error("Error writing json to file",e);
//        }

        return (result.toString(4));
    }

}
