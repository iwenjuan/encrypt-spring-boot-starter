package cn.iwenjuan.encrypt.domain;

import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.Encipher;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author li1244
 * @date 2023/3/29 10:41
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class EncryptConfig implements Serializable {

    private static final long serialVersionUID = 7609050882143213227L;

    /**
     * 应用ID
     */
    private String appId;
    /**
     * 是否启用
     */
    private boolean enable = false;
    /**
     * 加密算法
     */
    private Algorithm algorithm;
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
}
