package com.core.common.handler;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.core.common.bean.RequestError;
import com.core.common.bean.ResponseResult;
import com.core.common.exception.ParamInvalidException;
import com.core.common.exception.ResultException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseResult<Object> exceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        if (e.getMessage().indexOf("提示") != -1) {
         	int start = e.getMessage().indexOf("提示：");
        	if(e.getMessage().indexOf("\r\norg") != -1) {
        		int end = e.getMessage().indexOf("\r\norg");
        		String msg = e.getMessage().substring(start, end);
        		return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        	}else if(e.getMessage().indexOf("\norg") != -1){
        		int end = e.getMessage().indexOf("\norg");
        		String msg = e.getMessage().substring(start, end);
        		return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        	}else{
        		String msg = e.getMessage().substring(start, e.getMessage().length());
        		return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        	}
        }
        return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务内部错误");
    }

    @ExceptionHandler(value = ResultException.class)
    @ResponseBody
    public ResponseResult<Object> resultExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        return ResponseResult.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    @ExceptionHandler(value = NoPermissionException.class)
    @ResponseBody
    public ResponseResult<Object> noPermissionExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        return ResponseResult.fail(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value = IllegalStateException.class)
    @ResponseBody
    public ResponseResult<Object> illegalStateExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        return ResponseResult.fail(HttpStatus.MULTI_STATUS.value(), e.getMessage());
    }

    @ExceptionHandler(value = ParamInvalidException.class)
    @ResponseBody
    public ResponseResult<Object> paramInvalidExceptionHandler(ParamInvalidException e) throws Exception {
        return ResponseResult.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 处理参数绑定异常
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return outputBindingResult(ex.getBindingResult(), ex, headers, status, request);
    }

    /**
     * 处理Controller方法参数绑定异常
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return outputBindingResult(ex.getBindingResult(), ex, headers, status, request);
    }

    /**
     * 输出绑定结果
     * @param bindingResult
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    private ResponseEntity<Object> outputBindingResult(BindingResult bindingResult, Exception ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<RequestError> errors = new ArrayList<>();
        int globalErrorCount = bindingResult.getGlobalErrorCount();
        if (globalErrorCount > 0) {
            for (ObjectError error : bindingResult.getGlobalErrors()) {
                errors.add(new RequestError(error.getObjectName(), error.getCode(), error.getDefaultMessage()));
            }
        }

        int filedErrorCount = bindingResult.getFieldErrorCount();
        if (filedErrorCount > 0) {
            for (ObjectError error : bindingResult.getFieldErrors()) {
                errors.add(new RequestError(error.getObjectName(), error.getCode(), error.getDefaultMessage()));
            }
        }
        return handleExceptionInternal(ex, errors, headers, status, request);
    }
    
}

