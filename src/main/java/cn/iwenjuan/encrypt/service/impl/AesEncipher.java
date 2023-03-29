package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.exception.EncryptException;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.AesUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:28
 */
@Service
public class AesEncipher implements Encipher {

    @Override
    public String encrypt(String content, String publicKey, String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            throw new EncryptException("秘钥不能为空");
        }
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return AesUtils.encrypt(content, privateKey);
    }
}
