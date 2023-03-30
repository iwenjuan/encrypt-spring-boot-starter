package cn.iwenjuan.encrypt.filter;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
import cn.iwenjuan.encrypt.exception.EncryptException;
import cn.iwenjuan.encrypt.service.Encipher;
import cn.iwenjuan.encrypt.service.EncryptConfigService;
import cn.iwenjuan.encrypt.utils.ObjectUtils;
import cn.iwenjuan.encrypt.utils.PatternUtils;
import cn.iwenjuan.encrypt.utils.StringUtils;
import cn.iwenjuan.encrypt.wrappers.ResponseWrapper;
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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author li1244
 * @date 2023/3/29 11:41
 */
@Component
@WebFilter(urlPatterns = "/**")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ResponseEncryptFilter extends OncePerRequestFilter {

    private List<Pattern> ignoreResponseEncryptPatterns = null;

    @Resource
    private EncryptProperties properties;

    @Resource
    private EncryptConfigService encryptConfigService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (internalRequest(request)) {
            // 内部请求，不需要对响应结果加密，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        if (ignoreResponseEncrypt(requestURI)) {
            // 此接口不需要对响应结果加密，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        // 对响应结果加密
        ResponseWrapper wrapper = new ResponseWrapper(response);
        // 执行方法，获取响应
        filterChain.doFilter(request, wrapper);
        // 响应内容
        String content = wrapper.getContent();
        // 对响应内容进行加密
        content = encryptResponseContent(request, content);
        try {
            response.reset();
            response.getWriter().write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是内部请求
     * @param request
     * @return
     */
    private boolean internalRequest(HttpServletRequest request) {
        String internalHeader = properties.getInternalHeader();
        if (StringUtils.isBlank(internalHeader)) {
            return false;
        }
        String[] arr = internalHeader.split("=");
        if (arr.length != 2) {
            return false;
        }
        String headerName = arr[0];
        String headerValue = arr[1];
        String requestHeader = request.getHeader(headerName);
        if (StringUtils.isBlank(requestHeader) || !requestHeader.equals(headerValue)) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否忽略响应结果加密
     * @param requestURI
     * @return
     */
    private boolean ignoreResponseEncrypt(String requestURI) {

        List<Pattern> ignoreRequestDecryptPatterns = getIgnoreRequestDecryptPatterns();
        for (Pattern pattern : ignoreRequestDecryptPatterns) {
            if (pattern.matcher(requestURI).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取忽略响应结果加密路径正则
     *
     * @return
     */
    private List<Pattern> getIgnoreRequestDecryptPatterns() {
        if (ignoreResponseEncryptPatterns == null) {
            ignoreResponseEncryptPatterns = new ArrayList<>();
            List<String> ignoreRequestDecryptPaths = properties.getIgnoreResponseEncryptPaths();
            if (ObjectUtils.isNotEmpty(ignoreRequestDecryptPaths)) {
                for (String ignoreRequestDecryptPath : ignoreRequestDecryptPaths) {
                    ignoreResponseEncryptPatterns.add(Pattern.compile(PatternUtils.getPathRegStr(ignoreRequestDecryptPath)));
                }
            }
        }
        return ignoreResponseEncryptPatterns;
    }

    /**
     * 对响应内容进行加密
     *
     * @param request
     * @param content
     * @return
     */
    private String encryptResponseContent(HttpServletRequest request, String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        EncryptConfig config = encryptConfigService.getEncryptConfig(request, properties);
        if (!config.isEnable()) {
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
