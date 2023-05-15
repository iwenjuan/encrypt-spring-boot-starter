package cn.iwenjuan.encrypt.service.impl;

import cn.iwenjuan.encrypt.service.RequestMappingService;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    private static final String PATH_PARAM_REGEX = "\\{\\w+}";

    public Map<Pattern, Map<String, HandlerMethod>> handlerMethodMap = null;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void initHandlerMethodMap() {

        Map<String, Map<String, HandlerMethod>> pathHandlerMethodMap = new HashMap<>(16);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = methodsCondition.getMethods();
            PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
            Set<PathPattern> patterns = pathPatternsCondition.getPatterns();
            for (PathPattern pathPattern : patterns) {
                String pathRegStr = PatternUtils.getPathRegStr(pathPattern.getPatternString().replaceAll(PATH_PARAM_REGEX, "**"));
                Map<String, HandlerMethod> requestMethodHandlerMethodMap = pathHandlerMethodMap.get(pathRegStr);
                if (requestMethodHandlerMethodMap == null) {
                    requestMethodHandlerMethodMap = new HashMap<>(16);
                }
                if (methods == null || methods.size() == 0) {
                    requestMethodHandlerMethodMap.put("ALL", handlerMethod);
                } else {
                    for (RequestMethod method : methods) {
                        requestMethodHandlerMethodMap.put(method.name(), handlerMethod);
                    }
                }

                pathHandlerMethodMap.put(pathRegStr, requestMethodHandlerMethodMap);
            }
        }
        handlerMethodMap = new HashMap<>(16);
        for (Map.Entry<String, Map<String, HandlerMethod>> entry : pathHandlerMethodMap.entrySet()) {
            String pathRegStr = entry.getKey();
            Map<String, HandlerMethod> requestMethodHandlerMethodMap = entry.getValue();
            handlerMethodMap.put(Pattern.compile(pathRegStr), requestMethodHandlerMethodMap);
        }
    }

    @Override
    public HandlerMethod getHandlerMethod(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        for (Map.Entry<Pattern, Map<String, HandlerMethod>> entry : handlerMethodMap.entrySet()) {
            Pattern pattern = entry.getKey();
            if (pattern.matcher(requestURI).matches()) {
                Map<String, HandlerMethod> requestMethodHandlerMethodMap = entry.getValue();
                HandlerMethod handlerMethod = requestMethodHandlerMethodMap.get(requestMethod);
                if (handlerMethod == null) {
                    handlerMethod = requestMethodHandlerMethodMap.get("ALL");
                }
                return handlerMethod;
            }
        }
        return null;
    }
}
