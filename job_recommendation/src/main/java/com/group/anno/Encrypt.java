package com.group.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {
    // 是否对请求参数进行解密，默认为true
    boolean decrypt() default true;

    // 是否对响应结果进行加密，默认为true
    boolean encrypt() default true;
}
