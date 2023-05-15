# encrypt-spring-boot-starter

## 介绍
1. 接口请求加解密，实现请求参数解密、响应结果加密
2. 服务之间内部调用，配置spring.encrypt.internal-header，不需要加解密
3. 默认支持AES、DES、RSA、SM2、SM4算法
4. 支持自定义算法，需要实现Decoder、Encipher（并注入到Spring的IOC容器），并配置自定义解密器（spring.encrypt.decoder）和加密器（spring.encrypt.encipher）
5. 支持提供第三方调用接口时进行独立的加解密配置，需实现EncryptConfigService接口，根据app-id-header-name配置获取请求头appId，根据appId查询加解密配置
6. 目前只测试了SpringBoot 2.7.X版本

## 使用说明

### 1、maven引入依赖
~~~
<dependency>
    <groupId>cn.iwenjuan</groupId>
    <artifactId>encrypt-spring-boot-starter</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
~~~
### 2、启动类添加@EnableEncrypt注解
~~~
@SpringBootApplication
@EnableEncrypt
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

}
~~~
### 3、application.yml配置示例
~~~
spring:
  # 接口加解密配置
  encrypt:
    # 是否启用
    enable: true
    # 模式：filter（过滤器，通过ignoreRequestDecryptPaths、ignoreResponseEncryptPaths配置不需要加解密的接口）；annotation（注解，通过@Encrypt标记需要加解密的接口）
    model: annotation
    # 内部调用请求头，示例：inner=yes，当请求头包含inner，并且其值为yes时，不做加解密处理
    internal-header: inner=yes
    # 加解密算法，支持AES、DES、RSA、SM2、SM4，支持自定义算法，需要实现Decoder、Encipher
    algorithm: aes
    # 自定义解密器
    decoder:
    # 自定义加密器
    encipher:
    # 公钥
    public-key:
    # 私钥
    private-key: aEsva0zDHECg47P8SuPzmw==
    # 忽略请求参数解密接口
    ignore-request-decrypt-paths:
    # 忽略响应结果加密接口
    ignore-response-encrypt-paths:
    # appId请求头名称，提供给第三方调用接口时，根据此请求头值获取接口加解密配置，需实现EncryptConfigService接口
    app-id-header-name: app-id
    # url密文参数名称
    url-parameter-name: en
    # 请求body密文参数名称
    body-parameter-name: data
~~~
### 4、自定义加解密算法，spring.encrypt.algorithm配置成custom

###### 实现Decoder类（注入Spring的IOC容器），spring.encrypt.decoder配置为自定义的类
~~~
/**
 * 解密
 * @param content       密文
 * @param publicKey     公钥
 * @param privateKey    私钥
 * @return
 */
String decrypt(String content, String publicKey, String privateKey);
~~~
###### 实现Encipher类（注入Spring的IOC容器），spring.encrypt.encipher配置为自定义的类
~~~
/**
 * 加密
 * @param content       明文
 * @param publicKey     公钥
 * @param privateKey    私钥
 * @return
 */
String encrypt(String content, String publicKey, String privateKey);
~~~
### 5、第三方调用接口加解密配置

###### 实现EncryptService类（注入Spring的IOC容器），默认实现是返回不需要加密配置
> appId参数来源：根据app-id-header-name配置的请求头名称，从request获取的请求头
~~~
/**
 * 获取加解密配置
 * @param appId
 * @return
 */
EncryptConfig getEncryptConfig(String appId);
~~~
> 接口返回值EncryptConfig类说明
~~~
/**
 * 应用ID
 */
private String appId;
/**
 * 是否启用
 */
private boolean enable = false;
/**
 * 加密算法
 */
private Algorithm algorithm;
/**
 * 自定义解密器
 */
private Class<? extends Decoder> decoder;
/**
 * 自定义加密器
 */
private Class<? extends Encipher> encipher;
/**
 * 公钥
 */
private String publicKey;
/**
 * 私钥
 */
private String privateKey;
~~~
## 注意事项

### 选择SM2、SM4国密算法需要额外引入相关依赖
~~~
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.69</version>
</dependency>
~~~
