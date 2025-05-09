package com.group.advice;

import com.group.anno.Encrypt;
import com.group.utils.AESUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//对响应结果进行加密
@ControllerAdvice
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    // 判断是否需要对响应结果进行加密，只有标记了@Encrypt注解，并且encrypt为true的方法才需要加密
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        Encrypt encrypt = methodParameter.getMethodAnnotation(Encrypt.class);
        if (encrypt != null && encrypt.encrypt()) {
            return true;
        }
        return false;
    }

    // 在响应结果写入之前进行处理，这里需要对响应结果进行加密
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            // 如果响应结果是字符串类型，就进行加密操作
            if (o instanceof String) {
                String plainText = (String) o;
                // 调用AESUtil的encrypt方法进行加密，得到密文
                String cipherText = AESUtil.encrypt(plainText);
                return cipherText;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
