package org.example;

import java.io.File;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class TestParseAli {

    public static void testParser(String url, String fname, String appcode) throws Exception {
        // 设置头字段
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "APPCODE " + appcode);
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.addHeader("Content-Type", "application/json");

        // 读取简历内容
        byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(new File(fname));
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
        System.out.println(resCont);

        JSONObject res = new JSONObject(resCont);
        System.out.println(res.toString(4));
    }

    public static void main(String[] args) throws Exception {
        String url = "http://resumesdk.market.alicloudapi.com/ResumeParser";
        String fname = "C:/Users/86152/Desktop/图片模板.jpg";	//替换为你的文件名
        String appcode = "d991536fa278471eb80b689685f6679d";

        testParser(url, fname, appcode);
    }
}
