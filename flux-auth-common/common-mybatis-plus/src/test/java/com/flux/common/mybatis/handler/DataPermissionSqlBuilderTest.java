package com.flux.common.mybatis.handler;

import com.flux.common.mybatis.annotation.DataPermission;
import com.flux.common.mybatis.enums.DataScopeEnum;
import com.flux.common.mybatis.model.UserDataScope;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 数据权限 SQL 生成单测，不需要 Spring 上下文
 */
class DataPermissionSqlBuilderTest {

    private static final Long USER_ID = 9L;
    private static final Long DEPT_ID = 100L;

    @Test
    @DisplayName("ALL 范围不加任何条件")
    void allScope() {
        assertNull(build(scope(DataScopeEnum.ALL, null), aliased(), userTable()));
    }

    @Test
    @DisplayName("DEPT 范围只过滤本部门")
    void deptScope() {
        assertEquals("u.dept_id = 100",
                build(scope(DataScopeEnum.DEPT, null), aliased(), userTable()));
    }

    @Test
    @DisplayName("DEPT_AND_CHILD 范围用过业务方给的部门集合")
    void deptAndChildScope() {
        Set<Long> deptIds = new LinkedHashSet<>(java.util.List.of(100L, 101L, 102L));
        assertEquals("u.dept_id IN (100,101,102)",
                build(scope(DataScopeEnum.DEPT_AND_CHILD, deptIds), aliased(), userTable()));
    }

    @Test
    @DisplayName("DEPT_AND_CHILD 没给部门集合时退化成只看本部门")
    void deptAndChildWithoutIds() {
        assertEquals("u.dept_id IN (100)",
                build(scope(DataScopeEnum.DEPT_AND_CHILD, null), aliased(), userTable()));
    }

    @Test
    @DisplayName("SELF 范围只过滤本人数据")
    void selfScope() {
        assertEquals("u.create_by = 9",
                build(scope(DataScopeEnum.SELF, null), aliased(), userTable()));
    }

    @Test
    @DisplayName("别名对不上时不加条件，避免把条件加到 join 出来的其它表上")
    void aliasMismatch() {
        assertNull(build(scope(DataScopeEnum.DEPT, null), aliased(), table("sys_dept", "d")));
    }

    @Test
    @DisplayName("注解没配别名时只对无别名的单表生效")
    void noAliasAnnotation() {
        assertEquals("dept_id = 100",
                build(scope(DataScopeEnum.DEPT, null), noAlias(), new Table("sys_user")));
        assertNull(build(scope(DataScopeEnum.DEPT, null), noAlias(), userTable()));
    }

    @Test
    @DisplayName("数据范围取值非法时按 SELF 兜底，不放大权限")
    void unknownScopeFallsBackToSelf() {
        UserDataScope scope = new UserDataScope(USER_ID, DEPT_ID, 99, null);
        assertEquals("u.create_by = 9", build(scope, aliased(), userTable()));
    }

    @Test
    @DisplayName("没有登录人时不加条件")
    void nullScope() {
        assertNull(build(null, aliased(), userTable()));
    }

    private String build(UserDataScope scope, DataPermission annotation, Table table) {
        return DataPermissionSqlBuilder.build(scope, annotation, table);
    }

    private Table userTable() {
        return table("sys_user", "u");
    }

    private Table table(String name, String alias) {
        Table table = new Table(name);
        table.setAlias(new Alias(alias));
        return table;
    }

    private UserDataScope scope(DataScopeEnum dataScope, Set<Long> deptIds) {
        return new UserDataScope(USER_ID, DEPT_ID, dataScope.getValue(), deptIds);
    }

    private DataPermission aliased() {
        return annotationOf("annotatedWithAlias");
    }

    private DataPermission noAlias() {
        return annotationOf("annotatedWithoutAlias");
    }

    private DataPermission annotationOf(String methodName) {
        try {
            return getClass().getDeclaredMethod(methodName).getAnnotation(DataPermission.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @DataPermission(deptAlias = "u")
    private void annotatedWithAlias() {
    }

    @DataPermission
    private void annotatedWithoutAlias() {
    }
}
