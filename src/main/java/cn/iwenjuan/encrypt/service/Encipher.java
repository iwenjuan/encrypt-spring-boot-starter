package cn.iwenjuan.encrypt.service;

/**
 * @author li1244
 * @date 2023/3/29 13:17
 */
public interface Encipher {

    /**
     * 加密
     * @param content
     * @param publicKey
     * @param privateKey
     * @return
     */
    String encrypt(String content, String publicKey, String privateKey);
}
