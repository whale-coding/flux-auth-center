package com.flux.common.log.enums;

import com.flux.common.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @ClassName BusinessType
 * @Author njq
 * @Date 2026/10/28 20:29
 * @Description 业务操作类型
 */
@Getter
public enum BusinessType implements IBaseEnum<Integer> {
    OTHER(0, "其它"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    GRANT(4, "授权"),
    EXPORT(5, "导出"),
    IMPORT(6, "导入");

    private final Integer value;
    private final String label;

    BusinessType(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
