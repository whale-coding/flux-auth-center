package com.flux.core.support;

import com.flux.common.core.base.IBaseEnum;
import com.flux.common.mybatis.enums.DataScopeEnum;

import java.util.Collection;

/**
 * @ClassName DataScopeMerger
 * @Author njq
 * @Date 2026/10/29
 * @Description 多角色数据范围合并。
 * DataScopeEnum 的 value 越小范围越大，取最小值即「最宽松的角色说了算」；
 * 没有角色或取值不合法时按 SELF 兜底，只缩小不放大
 */
public final class DataScopeMerger {

    private DataScopeMerger() {
    }

    public static DataScopeEnum merge(Collection<Integer> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return DataScopeEnum.SELF;
        }
        DataScopeEnum merged = null;
        for (Integer value : scopes) {
            DataScopeEnum scope = IBaseEnum.getEnumByValue(value, DataScopeEnum.class);
            if (scope == null) {
                scope = DataScopeEnum.SELF;
            }
            if (merged == null || scope.getValue() < merged.getValue()) {
                merged = scope;
            }
        }
        return merged == null ? DataScopeEnum.SELF : merged;
    }
}
