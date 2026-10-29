package com.flux.common.mybatis.enums;

import com.flux.common.core.base.IBaseEnum;
import lombok.Getter;

/**
 * @ClassName DataScopeEnum
 * @Author njq
 * @Date 2026/10/28 15:04
 * @Description 数据权限枚举
 */
@Getter
public enum DataScopeEnum implements IBaseEnum<Integer> {
    /**
     * value 越小，数据权限范围越大
     */
    ALL(0, "所有数据"),
    DEPT_AND_CHILD(1, "部门及子部门数据"),
    DEPT(2, "本部门数据"),
    SELF(3, "本人数据"),
    /**
     * 自定义部门数据，部门集合来自 sys_role_dept
     */
    CUSTOM(4, "自定义部门数据");

    private final Integer value;
    private final String label;

    DataScopeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
