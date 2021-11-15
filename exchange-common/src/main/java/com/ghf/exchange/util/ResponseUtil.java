package com.ghf.exchange.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghf.exchange.dto.Result;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class ResponseUtil {

    private ResponseUtil() {
    }

    /**
     * 直接把result返回给调用方
     *
     * @param response
     * @param result   返回模型
     */
    @SneakyThrows
    public static void send(HttpServletResponse response, Result<?> result) {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter out = response.getWriter();
        out.println(new ObjectMapper().writeValueAsString(result));
        out.flush();
        out.close();
    }
}
