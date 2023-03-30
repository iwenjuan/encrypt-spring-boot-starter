package cn.iwenjuan.encrypt.service;

/**
 * @author li1244
 * @date 2023/3/29 13:10
 */
public interface Decoder {

    /**
     * 解密
     * @param content       密文
     * @param publicKey     公钥
     * @param privateKey    私钥
     * @return
     */
    String decrypt(String content, String publicKey, String privateKey);
}
