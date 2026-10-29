package com.flux.core.support;

import com.flux.common.mybatis.enums.DataScopeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @ClassName DataScopeMergerTest
 * @Author njq
 * @Date 2026/10/29
 * @Description 多角色数据范围合并的单测
 */
class DataScopeMergerTest {

    @Test
    @DisplayName("没有角色时按 SELF 兜底")
    void emptyReturnsSelf() {
        assertEquals(DataScopeEnum.SELF, DataScopeMerger.merge(Collections.emptyList()));
        assertEquals(DataScopeEnum.SELF, DataScopeMerger.merge(null));
    }

    @Test
    @DisplayName("单角色直接取该角色的范围")
    void singleScope() {
        assertEquals(DataScopeEnum.DEPT, DataScopeMerger.merge(List.of(2)));
    }

    @Test
    @DisplayName("多角色取范围最大的那个")
    void multipleTakesWidest() {
        assertEquals(DataScopeEnum.DEPT_AND_CHILD, DataScopeMerger.merge(List.of(3, 1, 2)));
    }

    @Test
    @DisplayName("所有数据范围优先级最高")
    void allWins() {
        assertEquals(DataScopeEnum.ALL, DataScopeMerger.merge(List.of(3, 0, 4)));
    }

    @Test
    @DisplayName("未知取值和 null 都按 SELF 处理，不放大范围")
    void dirtyValueFallsBackToSelf() {
        assertEquals(DataScopeEnum.SELF, DataScopeMerger.merge(List.of(99)));
        assertEquals(DataScopeEnum.SELF, DataScopeMerger.merge(Arrays.asList(null, 99)));
    }

    @Test
    @DisplayName("脏数据和合法范围同时存在时，只降级脏数据那一条")
    void dirtyValueDoesNotAffectOtherScopes() {
        assertEquals(DataScopeEnum.DEPT, DataScopeMerger.merge(List.of(99, 2)));
    }
}
