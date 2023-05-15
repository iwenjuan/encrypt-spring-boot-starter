package cn.iwenjuan.encrypt.enums;

/**
 * @author li1244
 * @date 2023/5/11 15:56
 */
public enum EncryptStrategy {

    /**
     * 仅请求加密
     */
    REQUEST,
    /**
     * 仅响应加密
     */
    RESPONSE,
    /**
     * 请求和响应加密
     */
    REQUEST_AND_RESPONSE;
}
