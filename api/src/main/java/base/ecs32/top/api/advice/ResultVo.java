package base.ecs32.top.api.advice;

import lombok.Data;

@Data
public class ResultVo<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> ResultVo<T> success(T data) {
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.setCode(ResultCode.SUCCESS.getCode());
        resultVo.setMessage(ResultCode.SUCCESS.getMessage());
        resultVo.setData(data);
        return resultVo;
    }

    public static <T> ResultVo<T> fail(ResultCode resultCode) {
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.setCode(resultCode.getCode());
        resultVo.setMessage(resultCode.getMessage());
        return resultVo;
    }
}
