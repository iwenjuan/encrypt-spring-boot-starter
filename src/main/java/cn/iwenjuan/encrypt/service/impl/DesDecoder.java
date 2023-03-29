package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.utils.DesUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:03
 */
@Service
public class DesDecoder implements Decoder {

    @Override
    public String decrypt(String content, String publicKey, String privateKey) {
        if (StringUtils.isBlank(privateKey)) {
            throw new DecryptException("秘钥不能为空");
        }
        if (StringUtils.isBlank(content)) {
            return content;
        }
        return DesUtils.decrypt(content, privateKey);
    }
}
