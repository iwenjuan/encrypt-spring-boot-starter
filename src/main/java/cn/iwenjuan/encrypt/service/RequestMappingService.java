package cn.iwenjuan.encrypt.service;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author li1244
 * @date 2023/5/12 11:13
 */
public interface RequestMappingService {

    /**
     * 获取目标Controller的处理方法
     * @param request
     * @return
     */
    HandlerMethod getHandlerMethod(HttpServletRequest request);
}
