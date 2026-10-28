package com.flux.common.core.enums;

import com.flux.common.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @ClassName GenderEnum
 * @Author njq
 * @Date 2026/10/28 13:52
 * @Description 性别枚举
 */
@Getter
public enum GenderEnum implements IBaseEnum<Integer> {
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer value;
    private final String label;

    GenderEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
