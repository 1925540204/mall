package com.arbor.mall.exception;

import com.arbor.mall.common.ApiRestResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;


/**
 * 描述：处理统一异常的Handler
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 处理其它异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception: "+ e);
        return ApiRestResponse.error(ArborMallExceptionEnum.SYSTEM_ERROR);
    }

    /**
     * 处理自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(ArborMallException.class)
    @ResponseBody
    public Object handleArborMallException(ArborMallException e){
        log.error("ArborMallException: "+ e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }


    /**
     * 处理参数错误异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNoValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException: "+ e);
        return handleBindingResult(e.getBindingResult());
    }

    /**
     * 将异常转换为ApiRestResponse
     * @param result
     * @return
     */
    private ApiRestResponse handleBindingResult(BindingResult result){
        // 把异常处理为对外暴露的提示
        List<String> list = new ArrayList<>();
        // 判断result里面是否包含错误
        if (result.hasErrors()) {
            List<ObjectError> allErrors = result.getAllErrors();    // 错误的列表
            // 取出错误信息
            for (ObjectError objectError : allErrors) {
                String message = objectError.getDefaultMessage();
                list.add(message);
            }
        }

        if (list.size() == 0){
            return ApiRestResponse.error(ArborMallExceptionEnum.REQUEST_PARAM_ERROR);
        }

        return ApiRestResponse.error(ArborMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(),
                list.toString());
    }


}
