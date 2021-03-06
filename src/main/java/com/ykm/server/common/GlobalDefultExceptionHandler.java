package com.ykm.server.common;

import com.ykm.server.common.exceptions.AuthorityException;
import com.ykm.server.common.exceptions.BusinessException;
import com.ykm.server.common.exceptions.MessageCode;
import com.ykm.server.consts.ResponseCode;
import com.ykm.server.controller.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages="com.ykm.server.controller")
@ResponseBody
@RestController
public class GlobalDefultExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    //声明要捕获的异常
    @ExceptionHandler(value = {AuthorityException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Object unAuthorityExcepitonHandler(AuthorityException e) {
        logger.error(e.getMessage(),e);
        return new ResponseDto<>(null, ResponseCode.AuthFail);
    }

    /**
     * @param e
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Object catchException(Exception e) {
        logger.error(e.getMessage(),e);
        return new ResponseDto<>(null, ResponseCode.Fail);
    }

    /**
     * @param e
     */
    @ExceptionHandler(value = BusinessException.class)
    public Object catchBusinessException(BusinessException e, HttpServletRequest request) {
        logger.error(e.getMessage(),e);
        if(e.getCode() == MessageCode.PRIZE_ILLEGAL_0.msgCode() ||
                e.getCode() == MessageCode.PRIZE_ILLEGAL_1.msgCode() ||
                e.getCode() == MessageCode.PRIZE_ILLEGAL_2.msgCode()
        ){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("/prize/error");
            modelAndView.addObject("code", e.getCode());
            modelAndView.addObject("msg", e.getMessage());
            return modelAndView;
        }
        return new ResponseDto<>(null, e.getCode(), e.getMessage());
    }
}
