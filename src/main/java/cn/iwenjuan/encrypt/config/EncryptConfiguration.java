package cn.iwenjuan.encrypt.config;

import cn.iwenjuan.encrypt.service.EncryptService;
import cn.iwenjuan.encrypt.service.impl.DefaultEncryptService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author li1244
 * @date 2023/3/29 13:55
 */
@Configuration
@ComponentScan(basePackages = {
        "cn.iwenjuan.encrypt.config",
        "cn.iwenjuan.encrypt.context",
        "cn.iwenjuan.encrypt.filter",
        "cn.iwenjuan.encrypt.service"
})
public class EncryptConfiguration {

    @Bean
    @ConditionalOnMissingBean(EncryptService.class)
    public EncryptService encryptConfigService() {
        return new DefaultEncryptService();
    }
}
