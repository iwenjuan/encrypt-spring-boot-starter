package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.utils.AesUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author li1244
 * @date 2023/3/29 11:31
 */
public class AesUtilsTest {

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String content = "这是明文内容";
//        String content = "name=张三&age=25";
//        String key = "aEsva0zDHECg47P8SuPzmw==";
//        String encrypt = AesUtils.encrypt(content, key);
//        System.out.println("加密结果：" + encrypt);
//        String decrypt = AesUtils.decrypt(encrypt, key);
//        System.out.println("解密结果：" + decrypt);
//        System.out.println(URLEncoder.encode(encrypt, "UTF-8"));

        String content = "/gleaJhDNSY+8Ieui2TGWN59iHtNNm6+SN9rGbklCGhKzSqf86oAoW+6RCi93FkNZbu/ba/JAjwTbB1tPQsohg==";
        System.out.println(AesUtils.decrypt(content, "aEsva0zDHECg47P8SuPzmw=="));

        String conten = "/gleaJhDNSY+8Ieui2TGWN59iHtNNm6+SN9rGbklCGhKzSqf86oAoW+6RCi93FkNf+XFUSTBQ4pDz54g+8ztNWIKu6mbnmuDD8fRLbhgJ8mnJ8dS35EwGYBRs84xbjhb";
        String key = "aEsva0zDHECg47P8SuPzmw==";
        System.out.println(AesUtils.decrypt(conten, key));
    }
}
