package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.annotation.Encrypt;
import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.enums.EncryptModel;
import cn.iwenjuan.encrypt.enums.EncryptStrategy;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.EncryptService;
import cn.iwenjuan.encrypt.service.RequestDecryptService;
import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.utils.ObjectUtils;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import cn.iwenjuan.encrypt.wrappers.RequestWrapper;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/12 11:00
 */
@Service
public class RequestDecryptServiceImpl implements RequestDecryptService {

    private List<Pattern> ignoreRequestDecryptPatterns = null;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptService encryptService;

    @Resource
    private RequestMappingService requestMappingService;

    @PostConstruct
    public void initIgnoreRequestDecryptPatterns() {
        ignoreRequestDecryptPatterns = new ArrayList<>();
        List<String> ignoreRequestDecryptPaths = properties.getIgnoreRequestDecryptPaths();
        if (ObjectUtils.isNotEmpty(ignoreRequestDecryptPaths)) {
            for (String ignoreRequestDecryptPath : ignoreRequestDecryptPaths) {
                ignoreRequestDecryptPatterns.add(Pattern.compile(PatternUtils.getPathRegStr(ignoreRequestDecryptPath)));
            }
        }
    }

    @Override
    public ServletRequest decrypt(HttpServletRequest request) {

        if (request instanceof MultipartHttpServletRequest) {
            // 文件上传请求，不做处理
            return request;
        }
        RequestWrapper requestWrapper = new RequestWrapper(request);
        if (encryptService.isInternalRequest(request, properties)) {
            // 内部请求，不做处理
            return requestWrapper;
        }

        if (ignoreRequestDecrypt(requestWrapper)) {
            // 此接口不需要对请求参数解密，不做处理
            return requestWrapper;
        }
        EncryptConfig config = encryptService.getEncryptConfig(request, properties);
        if (config == null || !config.isEnable()) {
            // 不需要解密，不做处理
            return requestWrapper;
        }
        // URL参数解密
        String parameter = requestWrapper.getParameter(properties.getUrlParameterName());
        if (StringUtils.isNotBlank(parameter)) {
            parameter = parameter.replaceAll(" ", "+");
            String decrypt = decrypt(parameter, config);
            if (StringUtils.isNotBlank(decrypt)) {
                // 解析参数
                MultiValueMap<String, String> multiParameterMap = new LinkedMultiValueMap<>();
                String[] params = decrypt.split("&");
                for (String param : params) {
                    String[] array = param.split("=");
                    if (array.length == 2) {
                        String parameterName = array[0];
                        String parameterValue = array[1];
                        multiParameterMap.add(parameterName, parameterValue);
                    }
                }
                Map<String, String[]> parameterMap = new HashMap<>(16);
                for (Map.Entry<String, List<String>> entry : multiParameterMap.entrySet()) {
                    List<String> value = entry.getValue();
                    parameterMap.put(entry.getKey(), value.toArray(new String[value.size()]));
                }
                // 设置请求参数
                requestWrapper.setParameterMap(parameterMap);
            }
        }
        // body参数解密
        String body = requestWrapper.getBody();
        if (StringUtils.isNotBlank(body)) {
            JSONObject jsonBody = JSONObject.parseObject(body);
            String decrypt = decrypt(jsonBody.getString(properties.getBodyParameterName()), config);
            if (StringUtils.isNotBlank(decrypt)) {
                requestWrapper.setBody(decrypt);
            }
        }
        return requestWrapper;
    }

    /**
     * 判断接口是否忽略请求参数解密
     * @param request
     * @return
     */
    private boolean ignoreRequestDecrypt(HttpServletRequest request) {
        if (!properties.isEnable()) {
            return true;
        }
        String requestURI = request.getRequestURI();
        EncryptModel encryptModel = properties.getModel();
        if (EncryptModel.filter == encryptModel) {
            for (Pattern pattern : ignoreRequestDecryptPatterns) {
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
            if (EncryptStrategy.REQUEST != strategy && EncryptStrategy.REQUEST_AND_RESPONSE != strategy) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解密
     * @param content
     * @param config
     * @return
     */
    private String decrypt(String content, EncryptConfig config) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        Algorithm algorithm = config.getAlgorithm();
        if (algorithm == null) {
            throw new DecryptException("未找到加解密算法，请检查配置");
        }
        Class<? extends Decoder> decoderClass = algorithm.getDecoder();
        if (Algorithm.CUSTOM == algorithm) {
            decoderClass = config.getDecoder();
        }
        if (decoderClass == null) {
            throw new DecryptException("未找到解密器，请检查配置");
        }
        Decoder decoder = SpringApplicationContext.getBean(decoderClass);
        return decoder.decrypt(content, config.getPublicKey(), config.getPrivateKey());
    }
}
