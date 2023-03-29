package cn.iwenjuan.encrypt.annotation;

import cn.iwenjuan.encrypt.config.EncryptConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author li1244
 * @date 2023/3/29 10:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EncryptConfiguration.class)
@Documented
public @interface EnableEncrypt {
}
