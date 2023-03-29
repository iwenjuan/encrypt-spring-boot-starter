package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.utils.Sm2Utils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:24
 */
@Service
public class Sm2Decoder implements Decoder {

    @Override
    public String decrypt(String content, String publicKey, String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            throw new DecryptException("私钥不能为空");
        }
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return Sm2Utils.decrypt(content, privateKey);
    }
}
