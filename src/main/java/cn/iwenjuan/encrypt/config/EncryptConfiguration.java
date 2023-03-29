package cn.iwenjuan.encrypt.config;

import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.service.EncryptConfigService;
import cn.iwenjuan.encrypt.service.impl.DefaultDecoder;
import cn.iwenjuan.encrypt.service.impl.DefaultEncipher;
import cn.iwenjuan.encrypt.service.impl.DefaultEncryptConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author li1244
 * @date 2023/3/29 13:55
 */
@Configuration
@ComponentScan(basePackages = {"cn.iwenjuan.encrypt.config", "cn.iwenjuan.encrypt.filter"})
public class EncryptConfiguration {

    @Bean
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder decoder() {
        return new DefaultDecoder();
    }

    @Bean
    @ConditionalOnMissingBean(Encipher.class)
    public Encipher encipher() {
        return new DefaultEncipher();
    }

    @Bean
    @ConditionalOnMissingBean(EncryptConfigService.class)
    public EncryptConfigService encryptConfigService() {
        return new DefaultEncryptConfigService();
    }
}
