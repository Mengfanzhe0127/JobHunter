package com.group;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.DES;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SymmetricCryptoTest {
    @Test
    public void des() {
        String text = "HelloWorld";

        // key：DES模式下，key必须为8位
        String key = "12345678";
        // iv：偏移量，ECB模式不需要，CBC模式下必须为8位
        String iv = "12345678";

        // DES des = new DES(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
        DES des = new DES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());

        String encrypt = des.encryptBase64(text);
        System.out.println(encrypt);

        String decrypt = des.decryptStr(encrypt);
        System.out.println(decrypt);
    }

    @Test
    public void aes() {
        String text = "HelloWorld";

        // key：AES模式下，key必须为16位
        String key = "1234567812345678";
        // iv：偏移量，ECB模式不需要，CBC模式下必须为16位
        String iv = "1234567812345678";

        // AES aes = new AES(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());

        // 加密并进行Base转码
        String encrypt = aes.encryptBase64(text);
        System.out.println(encrypt);

        // 解密为字符串
        String decrypt = aes.decryptStr(encrypt);
        System.out.println(decrypt);
    }

}
