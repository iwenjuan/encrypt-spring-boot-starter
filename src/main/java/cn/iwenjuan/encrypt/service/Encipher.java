package cn.iwenjuan.encrypt.service;

import cn.iwenjuan.encrypt.enums.Algorithm;

/**
 * @author li1244
 * @date 2023/3/29 13:17
 */
public interface Encipher {

    /**
     * 加密
     * @param content
     * @param algorithm
     * @param publicKey
     * @param privateKey
     * @return
     */
    String encrypt(String content, Algorithm algorithm, String publicKey, String privateKey);
}
