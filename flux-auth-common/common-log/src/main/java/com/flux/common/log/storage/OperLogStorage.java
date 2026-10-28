package com.flux.common.log.storage;

import com.flux.common.log.event.OperLogEvent;

/**
 * @ClassName OperLogStorage
 * @Author njq
 * @Date 2026/10/28
 * @Description 操作日志存储。common-log 不碰业务表，想落库就在业务模块实现这个接口并注册成 Bean，
 * 监听器会自动改走实现类；没有实现时日志只打印，不影响业务。
 * <p>
 * 注意：事件里的 operName、deptName 是空的 —— 操作人信息需要实现方自己从登录态补上再入库。
 */
public interface OperLogStorage {

    void save(OperLogEvent event);
}
