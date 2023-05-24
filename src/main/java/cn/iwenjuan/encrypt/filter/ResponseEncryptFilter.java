package cn.iwenjuan.encrypt.filter;

import cn.iwenjuan.encrypt.service.ResponseEncryptService;
import cn.iwenjuan.encrypt.wrappers.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
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

/**
 * @author li1244
 * @date 2023/3/29 11:41
 */
@Component
@WebFilter(urlPatterns = "/**")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ResponseEncryptFilter extends OncePerRequestFilter {

    @Resource
    private ResponseEncryptService responseEncryptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean multipartContent = ServletFileUpload.isMultipartContent(request);
        if (multipartContent) {
            // form表单提交请求，直接放行
            filterChain.doFilter(request, response);
            return;
        }
        if (responseEncryptService.ignoreResponseEncrypt(request)) {
            // 接口忽略请求结果加密，直接放行
            filterChain.doFilter(request, response);
            return;
        }

        ResponseWrapper wrapper = new ResponseWrapper(response);
        // 执行方法，获取响应
        filterChain.doFilter(request, wrapper);
        // 响应内容
        String content = wrapper.getContent();
        // 对响应结果加密
        content = responseEncryptService.encrypt(request, content);
        // 返回响应结果
        try {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
