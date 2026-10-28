package com.flux.common.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @ClassName DataPermission
 * @Author njq
 * @Date 2026/10/28 15:07
 * @Description Mybatis-Plus数据权限注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /**
     * 用户表的别名
     */
    public String userAlias() default "";

    /**
     * 部门表的别名
     */
    public String deptAlias() default "";

    /**
     * 用户字段名
     */
    public String userField() default "create_by";

    /**
     * 部门字段名
     */
    public String deptField() default "dept_id";
}
