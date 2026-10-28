package com.flux.common.mybatis.handler;

import com.flux.common.mybatis.annotation.DataPermission;
import com.flux.common.mybatis.enums.DataScopeEnum;
import com.flux.common.mybatis.model.UserDataScope;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 数据权限处理器单测：验证注解识别和条件拼装，不需要数据库
 */
class CustomDataPermissionHandlerTest {

    private static final String ANNOTATED_STATEMENT = DemoMapper.class.getName() + ".selectList";
    private static final String PLAIN_STATEMENT = DemoMapper.class.getName() + ".selectById";

    @Test
    @DisplayName("加了 @DataPermission 的方法会拼上数据权限条件")
    void annotatedMethod() {
        CustomDataPermissionHandler handler = handler(provider(DataScopeEnum.DEPT));

        Expression expression = handler.getSqlSegment(userTable(), null, ANNOTATED_STATEMENT);

        assertEquals("u.dept_id = 100", expression.toString());
    }

    @Test
    @DisplayName("没加注解的方法不受影响")
    void plainMethod() {
        CustomDataPermissionHandler handler = handler(provider(DataScopeEnum.DEPT));

        assertNull(handler.getSqlSegment(userTable(), null, PLAIN_STATEMENT));
    }

    @Test
    @DisplayName("ALL 范围不加条件")
    void allScope() {
        CustomDataPermissionHandler handler = handler(provider(DataScopeEnum.ALL));

        assertNull(handler.getSqlSegment(userTable(), null, ANNOTATED_STATEMENT));
    }

    @Test
    @DisplayName("没有 DataPermissionProvider 时不做任何过滤")
    void noProvider() {
        ObjectProvider<DataPermissionProvider> empty =
                new StaticListableBeanFactory().getBeanProvider(DataPermissionProvider.class);
        CustomDataPermissionHandler handler = new CustomDataPermissionHandler(empty);

        assertNull(handler.getSqlSegment(userTable(), null, ANNOTATED_STATEMENT));
    }

    private CustomDataPermissionHandler handler(DataPermissionProvider provider) {
        ObjectProvider<DataPermissionProvider> providerHolder =
                new StaticListableBeanFactory(Map.of("dataPermissionProvider", provider))
                        .getBeanProvider(DataPermissionProvider.class);
        return new CustomDataPermissionHandler(providerHolder);
    }

    private DataPermissionProvider provider(DataScopeEnum dataScope) {
        return () -> new UserDataScope(9L, 100L, dataScope.getValue(), null);
    }

    private Table userTable() {
        Table table = new Table("sys_user");
        table.setAlias(new Alias("u"));
        return table;
    }

    interface DemoMapper {
        @DataPermission(deptAlias = "u")
        List<Object> selectList();

        Object selectById();
    }
}
