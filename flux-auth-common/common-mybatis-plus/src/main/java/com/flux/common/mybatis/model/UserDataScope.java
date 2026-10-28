package com.flux.common.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @ClassName UserDataScope
 * @Author njq
 * @Date 2026/10/28
 * @Description 当前登录人的数据权限范围，由业务方通过 DataPermissionProvider 提供
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDataScope implements Serializable {

    /**
     * 用户ID，SELF 范围用
     */
    private Long userId;

    /**
     * 部门ID，DEPT 范围用
     */
    private Long deptId;

    /**
     * 数据范围，取值见 {@link com.flux.common.mybatis.enums.DataScopeEnum}
     */
    private Integer dataScope;

    /**
     * 部门ID集合，DEPT_AND_CHILD 用。子部门要业务方自己查出来，框架不碰业务表
     */
    private Set<Long> deptIds;
}
