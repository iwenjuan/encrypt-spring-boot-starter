package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.RsaUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:30
 */
@Service
public class RsaEncipher implements Encipher {

    @Override
    public String encrypt(String content, String publicKey, String privateKey) {
        return RsaUtils.encrypt(content, publicKey);
    }
}
