package cn.iwenjuan.encrypt.service;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/12 11:13
 */
public interface RequestMappingService {

    /**
     * 获取忽略请求解密的接口
     * @return
     */
    Map<Pattern, String> getIgnoreRequestDecryptPatternMap();

    /**
     * 获取忽略响应加密的接口
     * @return
     */
    Map<Pattern, String> getIgnoreResponseEncryptPatternMap();
}
