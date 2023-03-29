package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.utils.*;

/**
 * @author li1244
 * @date 2023/3/29 13:10
 */
public class DefaultDecoder implements Decoder {

    @Override
    public String decrypt(String content, Algorithm algorithm, String publicKey, String privateKey) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        switch (algorithm) {
            case AES:
                return AesUtils.decrypt(content, privateKey);
            case DES:
                return DesUtils.decrypt(content, privateKey);
            case RSA:
                return RsaUtils.decrypt(content, privateKey);
            case SM2:
                return Sm2Utils.decrypt(content, privateKey);
            case SM4:
                return Sm4Utils.decrypt(content, privateKey);
            default:
                return content;
        }
    }
}
