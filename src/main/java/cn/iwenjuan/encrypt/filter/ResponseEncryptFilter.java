package cn.iwenjuan.encrypt.filter;

import cn.iwenjuan.encrypt.config.EncryptProperties;
import cn.iwenjuan.encrypt.context.SpringApplicationContext;
import cn.iwenjuan.encrypt.domain.EncryptConfig;
import cn.iwenjuan.encrypt.enums.Algorithm;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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

        String requestURI = request.getRequestURI();
        if (ignoreResponseEncrypt(requestURI)) {
            // 此接口不需要对响应结果加密，直接放行
            filterChain.doFilter(request, response);
        } else {
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
            return content;
        }
        Algorithm algorithm = config.getAlgorithm();
        Class<? extends Encipher> encipherClass = algorithm.getEncipher();
        Encipher encipher = SpringApplicationContext.getBean(encipherClass);
        return encipher.encrypt(content, config.getPublicKey(), config.getPrivateKey());
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
