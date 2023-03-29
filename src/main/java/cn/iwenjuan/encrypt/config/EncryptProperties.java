package cn.iwenjuan.encrypt.config;

import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.Encipher;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author li1244
 * @date 2023/3/29 10:39
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.encrypt")
public class EncryptProperties {

    /**
     * 是否启用
     */
    private boolean enable = false;
    /**
     * 加密算法
     */
    private Algorithm algorithm = Algorithm.RSA;
    /**
     * 解密器
     */
    private Class<? extends Decoder> decoder;
    /**
     * 加密器
     */
    private Class<? extends Encipher> encipher;
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * 忽略请求参数解密接口
     */
    private List<String> ignoreRequestDecryptPaths;
    /**
     * 忽略响应结果加密接口
     */
    private List<String> ignoreResponseEncryptPaths;
    /**
     * appId请求头名称
     */
    private String appIdHeaderName = "app-id";
    /**
     * url密文参数名称
     */
    private String urlParameterName = "en";
    /**
     * 请求body密文参数名称
     */
    private String bodyParameterName = "data";

}
