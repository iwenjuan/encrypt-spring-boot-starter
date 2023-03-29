package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.exception.EncryptException;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.utils.Sm2Utils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:30
 */
@Service
public class Sm2Encipher implements Encipher {

    @Override
    public String encrypt(String content, String publicKey, String privateKey) {
        if (StringUtils.isBlank(publicKey)) {
            throw new EncryptException("公钥不能为空");
        }
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return Sm2Utils.encrypt(content, publicKey);
    }
}
