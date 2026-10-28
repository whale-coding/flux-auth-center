package com.flux.common.satoken.annotation;

import java.lang.annotation.*;

/**
 * @ClassName PreventDuplicateResubmit
 * @Author njq
 * @Date 2026/10/28 16:52
 * @Description 防重提交注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PreventDuplicateResubmit {
    /**
     * 防重提交锁过期时间(秒)
     * 默认5秒内不允许重复提交
     */
    int expire() default 5;
}
