package com.group.advice;

import com.group.anno.Encrypt;
import com.group.utils.AESUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 对请求参数进行解密
 */
@ControllerAdvice
public class DecodeRequestAdvice implements RequestBodyAdvice {

    // 判断是否需要对请求参数进行解密，只有标记了@Encrypt注解，并且decrypt为true的方法才需要解密
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        Encrypt encrypt = methodParameter.getMethodAnnotation(Encrypt.class);
        if (encrypt != null && encrypt.decrypt()) {
            return true;
        }
        return false;
    }

    // 在请求参数读取之前进行处理，这里不需要处理，直接返回原始的输入流
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        return httpInputMessage;
    }

    // 在请求参数读取之后进行处理，这里不需要处理，直接返回原始的对象
    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return o;
    }

    // 如果请求参数为空，也就是没有请求体的情况下，进行处理，这里需要对请求参数进行解密
    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        try {
            // 如果请求参数是字符串类型，就进行解密操作
            if (o instanceof String) {
                String cipherText = (String) o;
                // 调用AESUtil的decrypt方法进行解密，得到明文
                String plainText = AESUtil.decrypt(cipherText);
                return plainText;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
