package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.*;

/**
 * @author li1244
 * @date 2023/3/29 13:17
 */
public class DefaultEncipher implements Encipher {

    @Override
    public String encrypt(String content, Algorithm algorithm, String publicKey, String privateKey) {

        if (StringUtils.isBlank(content)) {
            return content;
        }
        switch (algorithm) {
            case AES:
                return AesUtils.encrypt(content, privateKey);
            case DES:
                return DesUtils.encrypt(content, privateKey);
            case RSA:
                return RsaUtils.encrypt(content, publicKey);
            case SM2:
                return Sm2Utils.encrypt(content, publicKey);
            case SM4:
                return Sm4Utils.encrypt(content, privateKey);
            default:
                return content;
        }
    }
}
