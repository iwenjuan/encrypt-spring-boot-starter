package cn.iwenjuan.encrypt.filter;

import cn.iwenjuan.encrypt.service.RequestDecryptService;
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

/**
 * @author li1244
 * @date 2023/3/29 11:40
 */
@Component
@WebFilter(urlPatterns = "/**")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestDecryptFilter extends OncePerRequestFilter {

    @Resource
    private RequestDecryptService requestDecryptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(requestDecryptService.decrypt(request), response);
    }

}
