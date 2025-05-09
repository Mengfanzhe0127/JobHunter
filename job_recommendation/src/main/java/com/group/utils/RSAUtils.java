package com.group.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.group.utils.RSAConstants.RSA_KEY;

/**
 * @Author: mfz
 * @Date: 2024/04/07/17:53
 * @Description:
 */
@Component
public class RSAUtils {
//    @Value("${rsa-public-key}")
    private String publicKeyStr = RSA_KEY;

    public String encrypt(String origin) throws Exception{
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        //设置加密模式
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // 需要加密的原始数据
        byte[] encryptedData = cipher.doFinal(origin.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

}
