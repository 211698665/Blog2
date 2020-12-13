package cn.henu.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 全局异常处理
 */
/*@ControllerAdvice
public class GlobalExceptions {
    @ExceptionHandler(Exception.class) //捕捉所有的异常
    @ResponseBody
    public ModelAndView allException(Exception exception,ModelAndView modelAndView){
        modelAndView.addObject("error",exception);

        return modelAndView;
    }
}*/
