package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.service.EncryptConfigService;

/**
 * @author li1244
 * @date 2023/3/29 13:13
 */
public class DefaultEncryptConfigService implements EncryptConfigService {

    private EncryptConfig encryptConfig;

    @Override
    public EncryptConfig getEncryptConfig(String appId) {
        return getDefaultEncryptConfig();
    }

    /**
     * 默认加解密配置
     * @return
     */
    private EncryptConfig getDefaultEncryptConfig() {
        if (encryptConfig == null) {
            encryptConfig = new EncryptConfig().setEnable(false);
        }
        return encryptConfig;
    }
}
