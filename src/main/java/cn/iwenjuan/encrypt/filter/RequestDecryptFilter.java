package cn.iwenjuan.encrypt.filter;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.service.Decoder;
import cn.iwenjuan.encrypt.service.EncryptConfigService;
import cn.iwenjuan.encrypt.utils.ObjectUtils;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import cn.iwenjuan.encrypt.wrappers.RequestWrapper;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/3/29 11:40
 */
@Component
@WebFilter(urlPatterns = "/**")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestDecryptFilter extends OncePerRequestFilter {

    private List<Pattern> ignoreRequestDecryptPatterns = null;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptConfigService encryptConfigService;

    @Resource
    private Decoder decoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (ignoreRequestDecrypt(requestURI)) {
            // 此接口不需要对请求参数解密，直接放行
            filterChain.doFilter(request, response);
        } else {
            // 对请求参数进行解密
            RequestWrapper wrapper = decryptRequestContent(request);
            filterChain.doFilter(wrapper, response);
        }
    }

    /**
     * 判断是否忽略请求参数解密
     *
     * @param requestURI
     * @return
     */
    private boolean ignoreRequestDecrypt(String requestURI) {

        List<Pattern> ignoreRequestDecryptPatterns = getIgnoreRequestDecryptPatterns();
        for (Pattern pattern : ignoreRequestDecryptPatterns) {
            if (pattern.matcher(requestURI).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取忽略请求参数解密路径正则
     *
     * @return
     */
    private List<Pattern> getIgnoreRequestDecryptPatterns() {
        if (ignoreRequestDecryptPatterns == null) {
            ignoreRequestDecryptPatterns = new ArrayList<>();
            List<String> ignoreRequestDecryptPaths = properties.getIgnoreRequestDecryptPaths();
            if (ObjectUtils.isNotEmpty(ignoreRequestDecryptPaths)) {
                for (String ignoreRequestDecryptPath : ignoreRequestDecryptPaths) {
                    ignoreRequestDecryptPatterns.add(Pattern.compile(PatternUtils.getPathRegStr(ignoreRequestDecryptPath)));
                }
            }
        }
        return ignoreRequestDecryptPatterns;
    }

    /**
     * 对请求参数进行解密
     *
     * @param request
     * @return
     */
    private RequestWrapper decryptRequestContent(HttpServletRequest request) {

        RequestWrapper requestWrapper = new RequestWrapper(request);
        String appId = request.getHeader(properties.getAppIdHeaderName());
        EncryptConfig config = null;
        if (StringUtils.isBlank(appId)) {
            // 没有请求头，使用默认的加解密配置
            config = getDefaultEncryptConfig();
        } else {
            config = encryptConfigService.getEncryptConfig(appId);
        }
        if (!config.isEnable()) {
            // 不需要加密
            return requestWrapper;
        }
        // URL参数解密
        String parameter = requestWrapper.getParameter(properties.getUrlParameterName());
        if (StringUtils.isNotBlank(parameter)) {
            String decrypt = decoder.decrypt(parameter, config.getAlgorithm(), config.getPublicKey(), config.getPrivateKey());
            if (StringUtils.isNotBlank(decrypt)) {
                Map<String, List<String>> parameterListMap = new HashMap<>(16);
                String[] params = decrypt.split("&");
                for (String param : params) {
                    String[] array = param.split("=");
                    if (array.length == 2) {
                        String parameterName = array[0];
                        List<String> parameterValues = parameterListMap.get(parameterName);
                        if (parameterValues == null) {
                            parameterValues = new ArrayList<>();
                        }
                        parameterValues.add(array[1]);
                        parameterListMap.put(parameterName, parameterValues);
                    }
                }
                Map<String, String[]> parameterMap = new HashMap<>(16);
                for (Map.Entry<String, List<String>> entry : parameterListMap.entrySet()) {
                    List<String> value = entry.getValue();
                    parameterMap.put(entry.getKey(), value.toArray(new String[value.size()]));
                }
                requestWrapper.setParameterMap(parameterMap);
            }
        }
        // body参数解密
        String body = requestWrapper.getBody();
        if (StringUtils.isNotBlank(body)) {
            JSONObject jsonBody = JSONObject.parseObject(body);
            String decrypt = decoder.decrypt(jsonBody.getString(properties.getBodyParameterName()), config.getAlgorithm(), config.getPublicKey(), config.getPrivateKey());
            if (StringUtils.isNotBlank(decrypt)) {
                requestWrapper.setBody(decrypt);
            }
        }
        return requestWrapper;
    }

    /**
     * 获取默认加解密配置
     * @return
     */
    private EncryptConfig getDefaultEncryptConfig() {
        return new EncryptConfig().setEnable(properties.isEnable())
                .setAlgorithm(properties.getAlgorithm())
                .setPublicKey(properties.getPublicKey())
                .setPrivateKey(properties.getPrivateKey());
    }

}
