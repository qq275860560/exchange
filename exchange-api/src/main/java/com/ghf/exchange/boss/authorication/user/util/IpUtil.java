package com.ghf.exchange.boss.authorication.user.util;

import com.ghf.exchange.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jiangyuanlin@163.com
 */
@Slf4j
public class IpUtil {

    private IpUtil() {
    }

    /**
     * 获取请求IP地址
     *
     * @return
     */
    public static String getIpAddr() {
        final String unknown = "unknown";
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        if (request == null) {
            return unknown;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        int number15 = 15;
        if (!ObjectUtils.isEmpty(ip) && ip.length() > number15) {
            String comma = ",";
            if (ip.indexOf(comma) > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        //处理获取多个ip地址情况 nginx多层代理会出现多个ip 第一个为真实ip地址
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 调用太平洋网络IP地址查询Web接口（http://whois.pconline.com.cn/），返回ip、地理位置
     */
    @SneakyThrows
    public static String getCityNameByIp(String ip) {

        //万一java获取不了，就用js来获取
        String url = "http://whois.pconline.com.cn/ipJson.jsp?json=true";
        //查指定ip
        if (!ObjectUtils.isEmpty(ip)) {
            url += "&ip=" + ip;
        }

        String body = new RestTemplate().getForEntity(url, String.class).getBody();

        Map<String, Object> map = JsonUtil.parse(body, Map.class);
        String city = (String) map.get("city");

        return city;
    }

}

