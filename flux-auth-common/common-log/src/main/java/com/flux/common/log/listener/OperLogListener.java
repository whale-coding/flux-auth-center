package com.flux.common.log.listener;

import com.flux.common.log.event.OperLogEvent;
import com.flux.common.log.storage.OperLogStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName OperLogListener
 * @Author njq
 * @Date 2026/10/28
 * @Description 操作日志监听器：有 {@link OperLogStorage} 实现就落库，没有就打印一条，
 * 避免切面发出的事件悄悄丢掉、排查时以为日志功能坏了
 */
@Slf4j
@Component
public class OperLogListener {

    private final ObjectProvider<OperLogStorage> storageProvider;

    public OperLogListener(ObjectProvider<OperLogStorage> storageProvider) {
        this.storageProvider = storageProvider;
    }

    @EventListener
    public void onOperLog(OperLogEvent event) {
        OperLogStorage storage = storageProvider.getIfAvailable();
        if (storage == null) {
            log.info("[操作日志] 未实现 OperLogStorage，仅打印：title={}, method={}, status={}, cost={}ms, operName={}",
                    event.getTitle(), event.getMethod(), event.getStatus(), event.getCostTime(), event.getOperName());
            return;
        }
        try {
            storage.save(event);
        } catch (Exception e) {
            // 落库失败不能影响业务
            log.error("操作日志落库失败: {}", event, e);
        }
    }
}
