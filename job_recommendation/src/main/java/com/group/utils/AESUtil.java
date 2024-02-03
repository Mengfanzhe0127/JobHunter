package com.group.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 */
public class AESUtil {

    // 密钥，必须是16位的字符串
    private static final String KEY = "1234567812345678";

    // 初始化向量，必须是16位的字符串
    private static final String IV = "8765432112345678";

    // 加密模式和填充方式
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // 加密方法，将明文转为密文
    public static String encrypt(String plainText) throws Exception {
        // 创建密钥对象
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        // 创建初始化向量对象
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
        // 创建加密器对象
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化加密器为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        // 加密明文，得到密文的字节数组
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        // 对密文的字节数组进行Base64编码，得到密文的字符串
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 解密方法，将密文转为明文
    public static String decrypt(String cipherText) throws Exception {
        // 创建密钥对象
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        // 创建初始化向量对象
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
        // 创建解密器对象
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        // 初始化解密器为解密模式
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        // 对密文的字符串进行Base64解码，得到密文的字节数组
        byte[] encrypted = Base64.getDecoder().decode(cipherText);
        // 解密密文，得到明文的字节数组
        byte[] decrypted = cipher.doFinal(encrypted);
        // 将明文的字节数组转为字符串，得到明文
        return new String(decrypted);
    }
}
