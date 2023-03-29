package cn.iwenjuan.encrypt.service;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author li1244
 * @date 2023/3/29 13:12
 */
public interface EncryptConfigService {

    /**
     * 获取加解密配置
     * @param request
     * @param properties
     * @return
     */
    default EncryptConfig getEncryptConfig(HttpServletRequest request, EncryptProperties properties) {
        String header = request.getHeader(properties.getAppIdHeaderName());
        EncryptConfig config;
        if (StringUtils.isBlank(header)) {
            // 没有请求头，使用默认的加解密配置
            config = new EncryptConfig().setEnable(properties.isEnable())
                    .setAlgorithm(properties.getAlgorithm())
                    .setDecoder(properties.getDecoder())
                    .setEncipher(properties.getEncipher())
                    .setPublicKey(properties.getPublicKey())
                    .setPrivateKey(properties.getPrivateKey());
        } else {
            config = getEncryptConfig(header);
        }
        return config;
    }

    /**
     * 获取加解密配置
     * @param appId
     * @return
     */
    EncryptConfig getEncryptConfig(String appId);
}
