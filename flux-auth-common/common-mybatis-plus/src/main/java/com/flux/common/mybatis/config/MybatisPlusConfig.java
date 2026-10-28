package com.flux.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.flux.common.mybatis.handler.CustomDataPermissionHandler;
import com.flux.common.mybatis.handler.DataPermissionProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MybatisPlusConfig
 * @Author njq
 * @Date 2026/10/28 15:01
 * @Description mybatis-plus 配置类
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件和数据权限插件。
     * DataPermissionProvider 由业务模块提供，没有的话数据权限不生效（不加条件）
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(ObjectProvider<DataPermissionProvider> dataPermissionProvider) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限插件
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(
                new CustomDataPermissionHandler(dataPermissionProvider)));
        // 分页插件，必须放在最后
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
