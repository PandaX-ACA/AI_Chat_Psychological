package org.example.aispringboot.common;

import org.example.aispringboot.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(MethodArgumentNotValidException.class)
public Result<String> handlerException(MethodArgumentNotValidException e){
    // ✅把校验错误信息接收保存到变量
    String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(","));

    // 按你想要的格式：message="参数错误"，data放真实校验提示：用户名不能为空
    return Result.error(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMsg(), errorMessage);
}

    //处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handlerBusinessException(BusinessException e){
        //如果异常携带有额外数据
        if(e.getData() != null) {
            return Result.error(e.getCode(), e.getMessage(), e.getData());
        }
        return Result.error(e.getCode(),e.getMessage(),null);
    }
}

