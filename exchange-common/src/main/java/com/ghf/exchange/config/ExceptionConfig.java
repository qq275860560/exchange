package com.ghf.exchange.config;

import com.ghf.exchange.dto.Result;
import com.ghf.exchange.exception.ResultCodeException;
import com.ghf.exchange.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author jiangyuanlin@163.com
 */
@Lazy
@Configuration
@Slf4j
public class ExceptionConfig implements WebMvcConfigurer {

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, (request, response, o, e) -> {
            handleResponse(response, e);
            return null;
        });
    }

    public static void handleResponse(HttpServletResponse response, Exception e) {
        int status = HttpStatus.BAD_REQUEST.value();
        String msg;
        if (e instanceof ResultCodeException) {
            status = ((ResultCodeException) e).getCode();
            msg = ((ResultCodeException) e).getMsg();
        } else if (e instanceof BindException) {
            msg = ((BindException) e).getBindingResult().getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        } else if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            StringJoiner exceptionMessage = new StringJoiner(",");
            if (bindingResult.hasErrors()) {
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                fieldErrors.forEach(error -> {
                    exceptionMessage.add(error.getDefaultMessage());
                });
            }
            msg = exceptionMessage.toString();
        } else {
            log.error("", e);
            msg = ObjectUtils.isEmpty(e.getMessage()) ? ObjectUtils.isEmpty(e.getCause()) ? "" : e.getCause().getMessage() : e.getMessage();
        }
        Result<Void> result = new Result<>(status, msg, null);
        ResponseUtil.send(response, result);
    }

}