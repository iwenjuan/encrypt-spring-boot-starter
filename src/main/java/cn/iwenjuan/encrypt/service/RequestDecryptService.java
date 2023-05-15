package cn.iwenjuan.encrypt.service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * @author li1244
 * @date 2023/5/12 11:00
 */
public interface RequestDecryptService {

    /**
     * 参数解密
     * @param request
     * @return
     */
    ServletRequest decrypt(HttpServletRequest request);
}
