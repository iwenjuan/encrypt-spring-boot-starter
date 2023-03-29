package cn.iwenjuan.encrypt.service;

/**
 * @author li1244
 * @date 2023/3/29 13:10
 */
public interface Decoder {

    /**
     * 解密
     * @param content
     * @param publicKey
     * @param privateKey
     * @return
     */
    String decrypt(String content, String publicKey, String privateKey);
}
