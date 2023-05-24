package cn.iwenjuan.encrypt.sample.controller;

import cn.iwenjuan.encrypt.annotation.Encrypt;
import cn.iwenjuan.encrypt.enums.EncryptStrategy;
import cn.iwenjuan.encrypt.sample.api.ApiResult;
import cn.iwenjuan.encrypt.sample.domain.User;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author li1244
 * @date 2023/3/29 13:40
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestController {

    @GetMapping("test1")
    @Encrypt()
    public ApiResult test1(User user, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info(JSONObject.toJSONString(parameterMap));
        return ApiResult.success(user);
    }

    @PostMapping("test2")
    @Encrypt()
    public ApiResult test2(@RequestBody List<User> user) {

        log.info(JSONObject.toJSONString(user));
        return ApiResult.success(user);
    }

    @PostMapping("test3")
    public ApiResult test3(MultipartHttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info(JSONObject.toJSONString(parameterMap));
        return ApiResult.success(parameterMap);
    }
}
