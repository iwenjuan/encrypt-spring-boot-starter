package cn.iwenjuan.encrypt.enums;

import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.service.impl.*;

/**
 * @author li1244
 * @date 2023/3/29 10:48
 */
public enum Algorithm {

    AES(AesDecoder.class, AesEncipher.class),
    DES(DesDecoder.class, DesEncipher.class),
    RSA(RsaDecoder.class, RsaEncipher.class),
    SM2(Sm2Decoder.class, Sm2Encipher.class),
    SM4(Sm4Decoder.class, Sm4Encipher.class),
    CUSTOM(null, null);

    /**
     * 解密器
     */
    private Class<? extends Decoder> decoder;
    /**
     * 加密器
     */
    private Class<? extends Encipher> encipher;

    Algorithm(Class<? extends Decoder> decoder, Class<? extends Encipher> encipher) {
        this.decoder = decoder;
        this.encipher = encipher;
    }

    public Class<? extends Decoder> getDecoder() {
        return decoder;
    }

    public Class<? extends Encipher> getEncipher() {
        return encipher;
    }
}
