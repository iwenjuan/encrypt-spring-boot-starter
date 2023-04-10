package cn.iwenjuan.encrypt.sample.controller;

import cn.iwenjuan.encrypt.sample.api.ApiResult;
import cn.iwenjuan.encrypt.sample.domain.User;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ApiResult test1(User user, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info(JSONObject.toJSONString(parameterMap));
//        int i = 1 / 0;
        return ApiResult.success(user);
    }

    @PostMapping("test2")
    public ApiResult test2(@RequestBody User user) {

        log.info(JSONObject.toJSONString(user));
        return ApiResult.success(user);
    }
}
