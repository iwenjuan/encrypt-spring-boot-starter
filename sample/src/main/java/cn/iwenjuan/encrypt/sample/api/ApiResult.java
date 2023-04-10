package cn.iwenjuan.encrypt.sample.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author li1244
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ApiResult<T> {

    private static final boolean SUCCESS = true;

    private static final boolean FAIL = false;

    private static final int SUCCESS_CODE = 0;

    private static final String SUCCESS_MESSAGE = "成功";

    private static final String FAIL_MESSAGE = "失败";

    private boolean success;

    private int code;

    private String message;

    private T data;

    public static ApiResult success() {
        return success(SUCCESS_MESSAGE, null);
    }

    public static ApiResult success(String message) {
        return success(message, null);
    }

    public static <T> ApiResult success(T data) {
        return success(SUCCESS_MESSAGE, data);
    }

    public static <T> ApiResult success(String message, T data) {
        return new ApiResult().setSuccess(SUCCESS).setCode(SUCCESS_CODE).setMessage(message).setData(data);
    }

    public static ApiResult fail(int code) {
        return fail(code, FAIL_MESSAGE, null);
    }

    public static ApiResult fail(int code, String message) {
        return fail(code, message, null);
    }

    public static <T> ApiResult fail(int code, String message, T data) {
        return new ApiResult().setSuccess(FAIL).setCode(code).setMessage(message).setData(data);
    }

    public boolean isSuccess() {
        return this.success;
    }

}
