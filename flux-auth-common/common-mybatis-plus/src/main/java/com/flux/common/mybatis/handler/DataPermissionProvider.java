package com.flux.common.mybatis.handler;

import com.flux.common.mybatis.model.UserDataScope;

/**
 * @ClassName DataPermissionProvider
 * @Author njq
 * @Date 2026/10/28
 * @Description 数据权限数据源。common-mybatis-plus 不关心用户、部门、角色存在哪，
 * 业务模块实现这个接口告诉框架「当前登录人能看到哪些数据」。
 * <p>
 * 返回 null 表示不加数据权限条件（比如未登录、定时任务）。
 * 需要按部门树过滤时，实现方要把子部门 ID 一并查出来放进 deptIds。
 */
public interface DataPermissionProvider {

    UserDataScope getCurrentScope();
}
