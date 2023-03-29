package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.Sm4Utils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:31
 */
@Service
public class Sm4Encipher implements Encipher {

    @Override
    public String encrypt(String content, String publicKey, String privateKey) {
        return Sm4Utils.encrypt(content, privateKey);
    }
}
