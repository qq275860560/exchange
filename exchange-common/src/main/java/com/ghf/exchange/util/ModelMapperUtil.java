package com.ghf.exchange.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author jiangyuanlin@163.com
 */
public class ModelMapperUtil {

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    static {
        MODEL_MAPPER.getConfiguration().setSourceNamingConvention(NamingConventions.JAVABEANS_ACCESSOR)
                .setDestinationNamingConvention(NamingConventions.JAVABEANS_MUTATOR)
                .setSourceNameTransformer(NameTransformers.JAVABEANS_ACCESSOR)
                .setDestinationNameTransformer(NameTransformers.JAVABEANS_MUTATOR)
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * 转换为列表
     *
     * @param sourceList  源列表
     * @param targetClass 目标类型
     * @return
     */
    public static <Source, Target> List<Target> mapForList(Collection<Source> sourceList, Class<Target> targetClass) {
        if (sourceList == null) {
            return new ArrayList<>(16);
        }
        List<Target> items = new ArrayList<Target>(sourceList.size());

        for (Source source : sourceList) {
            Target item = map(source, targetClass);
            items.add(item);
        }

        return items;
    }

    /**
     * 转换
     *
     * @param source      源
     * @param targetClass 目标类型
     * @return
     */
    public static <Target> Target map(Object source, Class<Target> targetClass) {
        if (source == null) {
            return null;
        }
        return MODEL_MAPPER.map(source, targetClass);
    }

}
