package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.annotation.Encrypt;
import cn.iwenjuan.encrypt.enums.EncryptStrategy;
import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.utils.ObjectUtils;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/5/12 11:13
 */
@Service
public class RequestMappingServiceImpl implements RequestMappingService {

    private static final String PATH_PARAM_REGEX = "\\{[\\w-]+}";

    private Map<Pattern, String> ignoreRequestDecryptPatternMap;

    private Map<Pattern, String> ignoreResponseEncryptPatternMap;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void initHandlerMethodMap() {

        ignoreRequestDecryptPatternMap = new HashMap<>(16);
        ignoreResponseEncryptPatternMap = new HashMap<>(16);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = methodsCondition.getMethods();
            StringBuilder builder = new StringBuilder();
            if (ObjectUtils.isNotEmpty(methods)) {
                for (RequestMethod method : methods) {
                    builder.append(method.name().toUpperCase()).append(",");
                }
            }
            String requestMethods = builder.toString();
            if (StringUtils.isBlank(requestMethods)) {
                requestMethods = "ALL";
            }
            PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
            Set<PathPattern> patterns = pathPatternsCondition.getPatterns();
            for (PathPattern pathPattern : patterns) {
                String pathRegStr = PatternUtils.getPathRegStr(pathPattern.getPatternString().replaceAll(PATH_PARAM_REGEX, "**"));
                Pattern pattern = Pattern.compile(pathRegStr);
                Encrypt encrypt = handlerMethod.getMethodAnnotation(Encrypt.class);
                if (encrypt == null) {
                    continue;
                }
                if (!encrypt.enable()) {
                    // 不启用加解密
                    ignoreRequestDecryptPatternMap.put(pattern, requestMethods);
                    ignoreResponseEncryptPatternMap.put(pattern, requestMethods);
                    continue;
                }
                EncryptStrategy strategy = encrypt.strategy();
                switch (strategy) {
                    case REQUEST:
                        // 仅请求加密，响应不需要加密
                        ignoreResponseEncryptPatternMap.put(pattern, requestMethods);
                        break;
                    case RESPONSE:
                        // 仅响应加密，请求不需要解密
                        ignoreRequestDecryptPatternMap.put(pattern, requestMethods);
                        break;
                    case REQUEST_AND_RESPONSE:
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public Map<Pattern, String> getIgnoreRequestDecryptPatternMap() {
        return ignoreRequestDecryptPatternMap;
    }

    @Override
    public Map<Pattern, String> getIgnoreResponseEncryptPatternMap() {
        return ignoreResponseEncryptPatternMap;
    }
}
