package cn.iwenjuan.encrypt.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.SM2;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.exception.EncryptException;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li1244
 * @date 2023/3/29 11:10
 */
@Slf4j
public class Sm2Utils {

    /**
     * 生成秘钥对
     * @return
     */
    public static Map<String, String> generateKeyPair() {
        try {
            SM2 sm2 = SmUtil.sm2();
            PublicKey publicKey = sm2.getPublicKey();
            PrivateKey privateKey = sm2.getPrivateKey();

            byte[] pubEncBytes = publicKey.getEncoded();
            byte[] priEncBytes = privateKey.getEncoded();
            // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
            String pubEncBase64 = Base64.getEncoder().encodeToString(pubEncBytes);
            String priEncBase64 = Base64.getEncoder().encodeToString(priEncBytes);

            Map<String, String> map = new HashMap(2);
            map.put("publicKey", pubEncBase64);
            map.put("privateKey", priEncBase64);
            return map;
        } catch (Exception e) {
            log.error("【生成SM2秘钥异常】：{}", e);
        }
        return null;
    }

    /**
     * 公钥加密
     *
     * @param content   要加密的内容
     * @param publicKey 公钥
     */
    public static String encrypt(String content, String publicKey) {
        try {
            SM2 sm2 = new SM2(null, publicKey);
            return sm2.encryptBase64(content, KeyType.PublicKey);
        } catch (Exception e) {
            log.error("【SM2加密异常】：{}", e);
        }
        throw new EncryptException("SM2加密异常");
    }

    /**
     * 私钥解密
     *
     * @param content    要解密的内容
     * @param privateKey 私钥
     */
    public static String decrypt(String content, String privateKey) {
        try {
            SM2 sm2 = new SM2(privateKey, null);
            return sm2.decryptStr(content, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error("【SM2解密异常】：{}", e);
        }
        throw new DecryptException("SM2解密异常");
    }

}
