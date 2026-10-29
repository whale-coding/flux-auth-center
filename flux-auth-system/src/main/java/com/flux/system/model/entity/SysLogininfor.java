package com.flux.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName SysLogininfor
 * @Author njq
 * @Date 2026/10/29
 * @Description 系统访问记录 sys_logininfor。
 * 这张表没有 deleted 字段，所以不继承 BaseEntity，否则会拼出不存在的列
 */
@Data
@TableName("sys_logininfor")
public class SysLogininfor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问ID
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 登录状态(0:成功;1:失败)
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private LocalDateTime loginTime;
}
