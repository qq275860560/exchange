package com.ghf.exchange.util;

import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

/**
 * @author jiangyuanlin@163.com
 */
public class ModelMapperUtil {

    /**
     * 转换
     *
     * @param source      源
     * @param targetClass 目标类型
     * @return
     */
    @SneakyThrows
    public static <Target> Target map(Object source, Class<Target> targetClass) {
        if (source == null) {
            return null;
        }
        Target target = targetClass.newInstance();
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
