package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.utils.Sm4Utils;

/**
 * @author li1244
 * @date 2023/3/29 11:35
 */
public class Sm4UtilsTest {

    public static void main(String[] args) {
        String content = "这是明文内容";
        String key = "sm4demo123456789";
        String encrypt = Sm4Utils.encrypt(content, key);
        System.out.println("加密结果：" + encrypt);
        String decrypt = Sm4Utils.decrypt(encrypt, key);
        System.out.println("解密结果：" + decrypt);
    }
}
