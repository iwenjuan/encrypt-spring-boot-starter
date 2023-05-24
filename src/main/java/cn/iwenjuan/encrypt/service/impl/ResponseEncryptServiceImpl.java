package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.annotation.Encrypt;
import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.enums.EncryptModel;
import cn.iwenjuan.encrypt.enums.EncryptStrategy;
import cn.iwenjuan.encrypt.exception.EncryptException;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.service.EncryptService;
import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.service.ResponseEncryptService;
import cn.iwenjuan.encrypt.utils.ObjectUtils;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/15 13:32
 */
@Service
public class ResponseEncryptServiceImpl implements ResponseEncryptService {

    private List<Pattern> ignoreResponseEncryptPatterns = null;

    private String contextPath;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptService encryptService;

    @Resource
    private RequestMappingService requestMappingService;

    @PostConstruct
    private void initIgnoreResponseEncryptPatterns() {
        ignoreResponseEncryptPatterns = new ArrayList<>();
        contextPath = SpringApplicationContext.getContextPath();
        List<String> ignoreRequestDecryptPaths = properties.getIgnoreResponseEncryptPaths();
        if (ObjectUtils.isNotEmpty(ignoreRequestDecryptPaths)) {
            for (String ignoreRequestDecryptPath : ignoreRequestDecryptPaths) {
                if (StringUtils.isNotBlank(contextPath) && ignoreRequestDecryptPath.startsWith(contextPath)) {
                    ignoreRequestDecryptPath = ignoreRequestDecryptPath.substring(ignoreRequestDecryptPath.indexOf(contextPath) + contextPath.length());
                }
                ignoreResponseEncryptPatterns.add(Pattern.compile(PatternUtils.getPathRegStr(ignoreRequestDecryptPath)));
            }
        }
    }

    @Override
    public boolean ignoreResponseEncrypt(HttpServletRequest request) {
        if (!properties.isEnable()) {
            return true;
        }
        if (encryptService.isInternalRequest(request, properties)) {
            // 内部请求，不需要对响应结果加密
            return true;
        }
        EncryptModel encryptModel = properties.getModel();
        if (EncryptModel.filter == encryptModel) {
            String requestURI = request.getRequestURI();
            if (StringUtils.isNotBlank(contextPath)) {
                requestURI = requestURI.substring(requestURI.indexOf(contextPath) + contextPath.length());
            }
            for (Pattern pattern : ignoreResponseEncryptPatterns) {
                if (pattern.matcher(requestURI).matches()) {
                    return true;
                }
            }
        }
        if (EncryptModel.annotation == encryptModel) {
            // 获取目标Controller的处理方法
            HandlerMethod handlerMethod = requestMappingService.getHandlerMethod(request);
            if (handlerMethod == null) {
                return true;
            }
            // 获取目标方法上的@Encrypt注解
            Encrypt encrypt = handlerMethod.getMethodAnnotation(Encrypt.class);
            if (encrypt == null) {
                return true;
            }
            if (!encrypt.enable()) {
                return true;
            }
            EncryptStrategy strategy = encrypt.strategy();
            if (EncryptStrategy.RESPONSE != strategy && EncryptStrategy.REQUEST_AND_RESPONSE != strategy) {
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
