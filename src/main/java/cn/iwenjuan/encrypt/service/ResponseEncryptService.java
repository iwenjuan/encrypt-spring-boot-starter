package cn.iwenjuan.encrypt.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author li1244
 * @date 2023/5/15 13:32
 */
public interface ResponseEncryptService {

    /**
     * 判断接口是否忽略请求结果加密
     * @param request
     * @return
     */
    boolean ignoreResponseEncrypt(HttpServletRequest request);

    /**
     * 响应结果加密
     * @param request
     * @param content
     * @return
     */
    String encrypt(HttpServletRequest request, String content);
}
