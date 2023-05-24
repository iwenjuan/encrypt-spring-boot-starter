package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.utils.AesUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author li1244
 * @date 2023/3/29 11:31
 */
public class AesUtilsTest {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String content = "name=张三&age=18";
        String key = "aEsva0zDHECg47P8SuPzmw==";
        String encrypt = AesUtils.encrypt(content, key);
        System.out.println("加密结果：" + encrypt);
        String decrypt = AesUtils.decrypt(encrypt, key);
        System.out.println("解密结果：" + decrypt);
        System.out.println(URLEncoder.encode(encrypt, "UTF-8"));
    }
}
