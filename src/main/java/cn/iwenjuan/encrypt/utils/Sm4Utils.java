package cn.iwenjuan.encrypt.utils;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li1244
 * @date 2023/3/29 11:25
 */
@Slf4j
public class Sm4Utils {

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", key.getBytes());
            return sm4.encryptBase64(content);
        } catch (Exception e) {
            log.error("【SM4加密异常】：{}", e);
        }
        throw new EncryptException("SM4加密异常");
    }

    /**
     * 解密
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding", key.getBytes());
            return sm4.decryptStr(content);
        } catch (Exception e) {
            log.error("【SM4解密异常】：{}", e);
        }
        throw new DecryptException("SM4解密异常");
    }

}
