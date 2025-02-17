package com.mortal.wms.execption;

import com.mortal.wms.business.enums.ResultTypeEnum;
import com.mortal.wms.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.*;

/**
 * 类描述: 全局异常拦截处理器
 *  1.处理自定义异常
 *  2.未知异常统一返回服务器错误
 *  3.已经catch到的异常不会被捕获
 *  4.异常的体系结构中，哪个异常与目标方法抛出的异常血缘关系越紧密，就会被哪个捕捉到。
 * @see ExceptionHandler ：统一处理某一类异常，从而能够减少代码重复率和复杂度
 * @see ControllerAdvice ：异常集中处理，更好的使业务逻辑与异常处理剥离开
 * @see ResponseStatus ：可以将某种异常映射为HTTP状态码 成功则Status Code: 200
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResultResponse handleException(BusinessException e) {
        // 打印异常信息
        log.error("自定义异常:{}", e.getResultTypeEnum().getMessage());
        return new ResultResponse(e.getResultTypeEnum());
    }



//    /**
//     * 其他全局异常在此捕获
//     * @param e
//     * @return
//     */
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Throwable.class)
//    public ResultResponse handleException(Throwable e) {
//        String msg = null;
//        log.error("服务运行异常", e);
//        log.error("异常类型：{}",e.getClass());
//        try {
//            msg = e.getCause().getMessage();
//            log.error("异常说明信息：{}",msg);
//        }catch (Exception logError){
//            log.error(String.valueOf(logError));
//        }
//        if(e instanceof UncategorizedSQLException){
//            if(e.getCause() != null && e.getCause() instanceof Exception){
//                // 数据库存储过程主动抛出的异常，返回通用错误码，提示错误信息
//                return new ResultResponse(ResultTypeEnum.SERVICE_FAILED,e.getCause().getMessage(),null);
//            }
//        }
//        return new ResultResponse(ResultTypeEnum.SERVICE_ERROR,msg == null ? ResultTypeEnum.SERVICE_ERROR.getMessage() : msg,null);
//    }

}