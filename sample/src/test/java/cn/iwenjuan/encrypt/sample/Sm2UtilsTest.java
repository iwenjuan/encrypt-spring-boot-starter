package cn.iwenjuan.encrypt.sample;

import cn.iwenjuan.encrypt.utils.Sm2Utils;

import java.util.Map;

/**
 * @author li1244
 * @date 2023/3/29 11:34
 */
public class Sm2UtilsTest {

    public static void main(String[] args) {
        String content = "这是明文内容";
        Map<String, String> map = Sm2Utils.generateKeyPair();
        String publicKey = map.get("publicKey");
        System.out.println("公钥：" + publicKey);
        String privateKey = map.get("privateKey");
        System.out.println("私钥：" + privateKey);
        String encrypt = Sm2Utils.encrypt(content, publicKey);
        System.out.println("加密结果：" + encrypt);
        String decrypt = Sm2Utils.decrypt(encrypt, privateKey);
        System.out.println("解密结果：" + decrypt);
    }
}
