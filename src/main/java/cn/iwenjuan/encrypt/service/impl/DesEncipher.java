package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.DesUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:29
 */
@Service
public class DesEncipher implements Encipher {

    @Override
    public String encrypt(String content, String publicKey, String privateKey) {
        return DesUtils.encrypt(content, privateKey);
    }
}
