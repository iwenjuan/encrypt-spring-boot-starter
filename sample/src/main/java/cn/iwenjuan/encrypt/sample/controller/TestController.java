package cn.iwenjuan.encrypt.sample.controller;

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
    public Object test1(User user, HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info(JSONObject.toJSONString(parameterMap));
        return user;
    }

    @PostMapping("test2")
    public Object test2(@RequestBody User user) {

        log.info(JSONObject.toJSONString(user));
        return user;
    }
}
