package com.flux.common.core.enums;

import com.flux.common.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @ClassName StatusEnum
 * @Author njq
 * @Date 2026/10/28 13:55
 * @Description 状态枚举
 */
@Getter
public enum StatusEnum implements IBaseEnum<Integer> {
    ENABLE(1, "启用"),
    DISABLE (0, "禁用");

    private final Integer value;
    private final String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
