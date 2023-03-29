package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.utils.DesUtils;

/**
 * @author li1244
 * @date 2023/3/29 11:32
 */
public class DesUtilsTest {

    public static void main(String[] args) {
        String content = "这是明文内容";
        String key = "aEsva0zDHECg47P8SuPzmw==";
        String encrypt = DesUtils.encrypt(content, key);
        System.out.println("加密结果：" + encrypt);
        String decrypt = DesUtils.decrypt(encrypt, key);
        System.out.println("解密结果：" + decrypt);
    }
}
