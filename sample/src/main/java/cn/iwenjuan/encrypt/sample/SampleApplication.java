package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.annotation.EnableEncrypt;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author li1244
 */
@SpringBootApplication
@EnableEncrypt
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
