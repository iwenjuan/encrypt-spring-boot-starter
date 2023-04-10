package cn.iwenjuan.encrypt.sample.exception;

import cn.iwenjuan.encrypt.exception.BaseException;
import cn.iwenjuan.encrypt.sample.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author li1244
 * @date 2023/1/3 10:20
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResult exception(Exception e) {
        log.error("【系统异常】：{}", e);
        if (e instanceof IllegalStateException) {
            IllegalStateException illegalStateException = (IllegalStateException) e;
            Throwable cause = illegalStateException.getCause();
            if (cause != null && cause instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) cause;
                Throwable targetException = invocationTargetException.getTargetException();
                if (targetException != null && targetException instanceof BaseException) {
                    BaseException baseException = (BaseException) targetException;
                    return ApiResult.fail(500, baseException.getMessage());
                }
            }
        }
        return ApiResult.fail(500, "系统异常");
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ApiResult httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.info("【请求方法不支持】：{}，{}", request.getRequestURI(), e);
        return ApiResult.fail(500, "请求方法不支持");
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ApiResult noHandlerFoundException(HttpServletRequest request, NoHandlerFoundException e) {
        log.info("【请求接口不存在】：{}，{}", request.getRequestURI(), e);
        return ApiResult.fail(500, "请求接口不存在");
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ApiResult missingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException e) {
        log.info("【缺少请求参数】：{}，{}", request.getRequestURI(), e);
        String message = e.getMessage();
        String[] split = message.split("'");
        String errorMessage = "缺少请求参数：".concat(split[1]);
        return ApiResult.fail(500, errorMessage);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return ApiResult.fail(500, "参数校验失败：" + fieldErrors.get(0).getDefaultMessage());
    }

    @ExceptionHandler(BaseException.class)
    public ApiResult baseException(BaseException e) {
        log.error("【系统业务异常】：{}", e);
        return ApiResult.fail(500, e.getMessage());
    }

}
