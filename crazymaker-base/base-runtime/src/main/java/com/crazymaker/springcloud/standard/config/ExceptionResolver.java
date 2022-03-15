package com.crazymaker.springcloud.standard.config;


import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * ExceptionResolver
 */
@Slf4j
@RestControllerAdvice
public class ExceptionResolver
{
    /**
     *  其他异常
     */
    private static final String OTHER_EXCEPTION_MESSAGE = "其他异常";
    /**
     *  业务异常
     */
    private static final String BUSINESS_EXCEPTION_MESSAGE = "业务异常";


    /**
     * 业务异常处理
     *
     * @param request 请求体
     * @param e       {@link BusinessException}
     * @return RestOut
     */
    @Order(1)
    @ExceptionHandler(BusinessException.class)
    public RestOut<String> businessException(HttpServletRequest request, BusinessException e)
    {
        log.info(BUSINESS_EXCEPTION_MESSAGE + ":" + e.getErrMsg());
        return RestOut.error(e.getErrMsg());
    }

    /**
     * 最后异常处理
     *
     * @param request 请求体
     * @param e       {@link Exception}
     * @return RestOut
     */
    @Order(2)
    @ExceptionHandler(Exception.class)
    public RestOut<String> finalException(HttpServletRequest request, Exception e)
    {
        e.printStackTrace();
        log.error(OTHER_EXCEPTION_MESSAGE + ":" + e.getMessage());
        return RestOut.error(e.getMessage());
    }
}
