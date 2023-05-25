package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.enums.EncryptModel;
import cn.iwenjuan.encrypt.exception.DecryptException;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.EncryptService;
import cn.iwenjuan.encrypt.service.RequestDecryptService;
import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.utils.StringUtils;
import cn.iwenjuan.encrypt.wrappers.RequestWrapper;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/12 11:00
 */
@Service
@Slf4j
public class RequestDecryptServiceImpl implements RequestDecryptService {

    private Map<Pattern, String> ignoreRequestDecryptPatternMap;

    private String contextPath;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptService encryptService;

    @Resource
    private RequestMappingService requestMappingService;

    @PostConstruct
    public void initIgnoreRequestDecryptPatterns() {
        ignoreRequestDecryptPatternMap = new HashMap<>(16);
        // 获取上下文路径
        contextPath = SpringApplicationContext.getContextPath();
        // 获取application.yml配置的忽略请求解密的接口
        Set<String> ignoreRequestDecryptPaths = properties.getIgnoreRequestDecryptPaths();
        for (String ignoreRequestDecryptPath : ignoreRequestDecryptPaths) {
            // “#”分隔符之前的是接口地址，“#”分隔符之后的是接口的请求方法（如：GET,POST)，多个方法以“,”隔开，没有“#”分隔符或“#”分隔符后面的内容为空，代表匹配所有请求方法
            String[] array = ignoreRequestDecryptPath.split("#");
            // 接口地址
            String path = array[0];
            if (StringUtils.isNotBlank(contextPath) && path.startsWith(contextPath)) {
                // 接口地址去掉上下文路径，方便后续正则匹配
                path = path.substring(path.indexOf(contextPath) + contextPath.length());
            }
            Pattern pattern = Pattern.compile(path);
            if (array.length < 2) {
                ignoreRequestDecryptPatternMap.put(pattern, "ALL");
            } else {
                // 匹配的接口请求方法（如：GET,POST)，多个方法以“,”隔开
                String requestMethods = array[1];
                ignoreRequestDecryptPatternMap.put(pattern, requestMethods);
            }
        }
    }

    @Override
    public ServletRequest decrypt(HttpServletRequest request) {

        boolean multipartContent = ServletFileUpload.isMultipartContent(request);
        if (multipartContent) {
            // form表单提交请求，不做处理
            return request;
        }
        RequestWrapper requestWrapper = new RequestWrapper(request);
        if (ignoreRequestDecrypt(requestWrapper)) {
            // 此接口不需要对请求参数解密，不做处理
            return requestWrapper;
        }
        // 获取加解密配置
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
        if (encryptService.isInternalRequest(request, properties)) {
            // 内部请求，不做处理
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
            return matches(requestURI, method, ignoreRequestDecryptPatternMap);
        }
        if (EncryptModel.ANNOTATION == encryptModel) {
            // 获取@Encrypt注解标记忽略请求解密的接口
            Map<Pattern, String> ignoreRequestDecryptPatternMap = requestMappingService.getIgnoreRequestDecryptPatternMap();
            return matches(requestURI, method, ignoreRequestDecryptPatternMap);
        }
        return false;
    }

    /**
     * 正则匹配是否忽略请求解密
     * @param requestURI
     * @param method
     * @param ignoreRequestDecryptPatternMap
     * @return
     */
    private boolean matches(String requestURI, String method, Map<Pattern, String> ignoreRequestDecryptPatternMap) {
        for (Map.Entry<Pattern, String> entry : ignoreRequestDecryptPatternMap.entrySet()) {
            Pattern pattern = entry.getKey();
            String requestMethods = entry.getValue();
            if (pattern.matcher(requestURI).matches() && (requestMethods.contains(method) || requestMethods.contains("ALL"))) {
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
