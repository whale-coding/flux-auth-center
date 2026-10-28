package com.flux.common.log.enums;

import com.flux.common.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @ClassName BusinessStatus
 * @Author njq
 * @Date 2026/10/28 20:28
 * @Description 操作状态
 */
@Getter
public enum BusinessStatus implements IBaseEnum<Integer>{
    SUCCESS(0, "成功"),
    FAIL(1, "失败");

    private final Integer value;
    private final String label;

    BusinessStatus(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
