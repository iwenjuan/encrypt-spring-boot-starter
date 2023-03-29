package cn.iwenjuan.encrypt.service;

import cn.iwenjuan.encrypt.domain.EncryptConfig;

/**
 * @author li1244
 * @date 2023/3/29 13:12
 */
public interface EncryptConfigService {

    /**
     * 获取加解密配置
     * @param appId
     * @return
     */
    EncryptConfig getEncryptConfig(String appId);
}
