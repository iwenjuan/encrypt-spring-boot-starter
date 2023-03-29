package cn.iwenjuan.encrypt.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li1244
 * @date 2023/3/29 11:02
 */
@Slf4j
public class AesUtils {

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            AES aes = SecureUtil.aes(key.getBytes());
            return aes.encryptBase64(content);
        } catch (Exception e) {
            log.error("【AES加密异常】：{}", e);
        }
        throw new EncryptException("AES加密异常");
    }

    /**
     * 解密
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            AES aes = SecureUtil.aes(key.getBytes());
            return aes.decryptStr(content);
        } catch (Exception e) {
            log.error("【AES解密异常】：{}", e);
        }
        throw new DecryptException("AES解密异常");
    }

}
