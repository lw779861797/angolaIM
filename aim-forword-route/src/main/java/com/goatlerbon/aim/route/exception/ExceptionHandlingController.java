package com.goatlerbon.aim.route.exception;

import com.goatlerbon.aim.common.exception.AIMException;
import com.goatlerbon.aim.common.res.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ControllerAdvice 是增强的 controller
 * 使用 @ControllerAdvice 实现全局异常处理
 */
@ControllerAdvice
public class ExceptionHandlingController {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class) ;

    @ExceptionHandler(AIMException.class)
    @ResponseBody
    public BaseResponse handleAllExceptions(AIMException ex) {
        logger.error("exception", ex);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ex.getErrorCode());
        baseResponse.setMessage(ex.getMessage());
        return baseResponse ;
    }
}
