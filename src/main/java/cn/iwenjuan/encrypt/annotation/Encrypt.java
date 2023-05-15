package cn.iwenjuan.encrypt.annotation;

import cn.iwenjuan.encrypt.enums.EncryptStrategy;

import java.lang.annotation.*;

/**
 * @author li1244
 * @date 2023/5/11 15:03
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypt {

    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 加解密策略
     * @return
     */
    EncryptStrategy strategy() default EncryptStrategy.REQUEST_AND_RESPONSE;
}
