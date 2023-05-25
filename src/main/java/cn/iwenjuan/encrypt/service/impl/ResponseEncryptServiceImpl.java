package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.enums.EncryptModel;
import cn.iwenjuan.encrypt.exception.EncryptException;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.service.EncryptService;
import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.service.ResponseEncryptService;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/15 13:32
 */
@Service
public class ResponseEncryptServiceImpl implements ResponseEncryptService {

    private Map<Pattern, String> ignoreResponseEncryptPatternMap;

    private String contextPath;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptService encryptService;

    @Resource
    private RequestMappingService requestMappingService;

    @PostConstruct
    private void initIgnoreResponseEncryptPatterns() {
        ignoreResponseEncryptPatternMap = new HashMap<>(16);
        // 获取上下文路径
        contextPath = SpringApplicationContext.getContextPath();
        // 获取application.yml配置的忽略响应加密的接口
        Set<String> ignoreResponseEncryptPaths = properties.getIgnoreResponseEncryptPaths();
        for (String ignoreResponseEncryptPath : ignoreResponseEncryptPaths) {
            // “#”分隔符之前的是接口地址，“#”分隔符之后的是接口的请求方法（如：GET,POST)，多个方法以“,”隔开，没有“#”分隔符或“#”分隔符后面的内容为空，代表匹配所有请求方法
            String[] array = ignoreResponseEncryptPath.split("#");
            // 接口地址
            String path = array[0];
            if (StringUtils.isNotBlank(contextPath) && path.startsWith(contextPath)) {
                // 接口地址去掉上下文路径，方便后续正则匹配
                path = path.substring(path.indexOf(contextPath) + contextPath.length());
            }
            Pattern pattern = Pattern.compile(path);
            if (array.length < 2) {
                ignoreResponseEncryptPatternMap.put(pattern, "ALL");
            } else {
                // 匹配的接口请求方法（如：GET,POST)，多个方法以“,”隔开
                String requestMethods = array[1];
                ignoreResponseEncryptPatternMap.put(pattern, requestMethods);
            }
        }
    }

    @Override
    public boolean ignoreResponseEncrypt(HttpServletRequest request) {
        if (encryptService.isInternalRequest(request, properties)) {
            // 内部请求，不需要对响应结果加密
            return true;
        }
        // 获取加解密配置
        EncryptConfig config = encryptService.getEncryptConfig(request, properties);
        if (config == null || !config.isEnable()) {
            // 加解密配置为空，或配置不需要加密
            return true;
        }
        String requestURI = request.getRequestURI();
        String method = request.getMethod().toUpperCase();
        // 请求接口地址去掉上下文路径，方便正则匹配
        if (StringUtils.isNotBlank(contextPath)) {
            requestURI = requestURI.substring(requestURI.indexOf(contextPath) + contextPath.length());
        }
        EncryptModel encryptModel = properties.getModel();
        if (EncryptModel.FILTER == encryptModel) {
            return matches(requestURI, method, ignoreResponseEncryptPatternMap);
        }
        if (EncryptModel.ANNOTATION == encryptModel) {
            // 获取@Encrypt注解标记忽略响应加密的接口
            Map<Pattern, String> ignoreResponseEncryptPatternMap = requestMappingService.getIgnoreResponseEncryptPatternMap();
            return matches(requestURI, method, ignoreResponseEncryptPatternMap);
        }
        return false;
    }

    /**
     * 正则匹配是否忽略响应加密
     * @param requestURI
     * @param method
     * @param ignoreResponseEncryptPatternMap
     * @return
     */
    private boolean matches(String requestURI, String method, Map<Pattern, String> ignoreResponseEncryptPatternMap) {
        for (Map.Entry<Pattern, String> entry : ignoreResponseEncryptPatternMap.entrySet()) {
            Pattern pattern = entry.getKey();
            String requestMethods = entry.getValue();
            if (pattern.matcher(requestURI).matches() && (requestMethods.contains(method) || requestMethods.contains("ALL"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String encrypt(HttpServletRequest request, String content) {

        if (StringUtils.isBlank(content)) {
            return content;
        }
        EncryptConfig config = encryptService.getEncryptConfig(request, properties);
        if (config == null || !config.isEnable()) {
            // 不需要加密
            return content;
        }
        Algorithm algorithm = config.getAlgorithm();
        if (algorithm == null) {
            throw new EncryptException("未找到加解密算法，请检查配置");
        }
        Class<? extends Encipher> encipherClass = algorithm.getEncipher();
        if (encipherClass == null) {
            throw new EncryptException("未找到加密器，请检查配置");
        }
        Encipher encipher = SpringApplicationContext.getBean(encipherClass);
        return encipher.encrypt(content, config.getPublicKey(), config.getPrivateKey());
    }
}
