package com.flux.common.mybatis.handler;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.flux.common.mybatis.base.BaseEntity;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基础实体映射与审计字段填充的单测，不需要数据库
 */
class BaseEntityMappingTest {

    private static final Long USER_ID = 9L;

    private TableInfo tableInfo;

    @BeforeEach
    void initTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "");
        tableInfo = TableInfoHelper.initTableInfo(assistant, DemoEntity.class);
    }

    @Test
    @DisplayName("继承 BaseEntity 的表会启用插入和更新填充")
    void fillEnabled() {
        assertTrue(tableInfo.isWithInsertFill());
        assertTrue(tableInfo.isWithUpdateFill());
    }

    @Test
    @DisplayName("deleted 字段启用逻辑删除，删除时写 UNIX_TIMESTAMP()")
    void logicDeleteEnabled() {
        assertTrue(tableInfo.isWithLogicDelete());
        assertEquals("deleted=UNIX_TIMESTAMP()", tableInfo.getLogicDeleteSql(false, false));
        assertEquals("deleted=0", tableInfo.getLogicDeleteSql(false, true));
    }

    @Test
    @DisplayName("新增时填充创建人、更新人和时间")
    void insertFill() {
        DemoEntity entity = new DemoEntity();
        handler(USER_ID).insertFill(SystemMetaObject.forObject(entity));

        assertEquals(USER_ID, entity.getCreateBy());
        assertEquals(USER_ID, entity.getUpdateBy());
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getUpdateTime());
    }

    @Test
    @DisplayName("更新时覆盖更新人，保留创建人")
    void updateFill() {
        DemoEntity entity = new DemoEntity();
        entity.setCreateBy(1L);
        entity.setUpdateBy(1L);

        handler(USER_ID).updateFill(SystemMetaObject.forObject(entity));

        assertEquals(1L, entity.getCreateBy());
        assertEquals(USER_ID, entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }

    @Test
    @DisplayName("没有登录人时不填审计字段")
    void noLoginUser() {
        DemoEntity entity = new DemoEntity();
        handler(null).insertFill(SystemMetaObject.forObject(entity));

        assertNull(entity.getCreateBy());
        assertNull(entity.getUpdateBy());
        assertNotNull(entity.getCreateTime());
    }

    private MyMetaObjectHandler handler(Long userId) {
        StaticListableBeanFactory beanFactory = userId == null
                ? new StaticListableBeanFactory()
                : new StaticListableBeanFactory(Map.of("auditUserProvider", (AuditUserProvider) () -> userId));
        return new MyMetaObjectHandler(beanFactory.getBeanProvider(AuditUserProvider.class));
    }

    static class DemoEntity extends BaseEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
