package com.flux.common.core.base;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @ClassName IBaseEnum
 * @Author njq
 * @Date 2026/10/28 13:44
 * @Description 枚举通用接口
 */
public interface IBaseEnum<T> {
    T getValue();

    String getLabel();

    /**
     * 根据value获取枚举实例
     */
    static <E extends Enum<E> & IBaseEnum<?>> E getEnumByValue(Object value, Class<E> clazz) {
        if (value == null) {
            return null;
        }
        return EnumSet.allOf(clazz).stream()
                .filter(e -> Objects.equals(e.getValue(), value))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据value获取label
     */
    static <E extends Enum<E> & IBaseEnum<?>> String getLabelByValue(Object value, Class<E> clazz) {
        E enumItem = getEnumByValue(value, clazz);
        return enumItem == null ? null : enumItem.getLabel();
    }
}
