package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.utils.RsaUtils;
import org.springframework.stereotype.Service;

/**
 * @author li1244
 * @date 2023/3/29 16:09
 */
@Service
public class RsaDecoder implements Decoder {

    @Override
    public String decrypt(String content, String publicKey, String privateKey) {
        return RsaUtils.decrypt(content, privateKey);
    }
}
