package cn.iwenjuan.encrypt.utils;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author li1244
 * @date 2023/3/29 11:06
 */
@Slf4j
public class DesUtils {

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        try {
            SymmetricCrypto crypto = new SymmetricCrypto(SymmetricAlgorithm.DES, key.getBytes());
            return crypto.encryptBase64(content);
        } catch (Exception e) {
            log.error("【DES加密异常】：{}", e);
        }
        throw new EncryptException("DES加密异常");
    }

    /**
     * 解密
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        try {
            SymmetricCrypto crypto = new SymmetricCrypto(SymmetricAlgorithm.DES, key.getBytes());
            return crypto.decryptStr(content);
        } catch (Exception e) {
            log.error("【DES解密异常】：{}", e);
        }
        throw new DecryptException("DES解密异常");
    }

}
