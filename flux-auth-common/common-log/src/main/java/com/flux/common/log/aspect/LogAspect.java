package com.flux.common.log.aspect;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.flux.common.log.annotation.Log;
import com.flux.common.log.enums.BusinessStatus;
import com.flux.common.log.event.OperLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @ClassName LogAspect
 * @Author njq
 * @Date 2026/10/28 20:28
 * @Description 操作日志记录处理
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 需要脱敏的字段
     */
    private static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 入库字段最大长度
     */
    private static final int MAX_LENGTH = 2000;

    private final ApplicationEventPublisher eventPublisher;

    public LogAspect(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 环绕通知：统一处理成功/异常、耗时、参数与返回值
     */
    @Around("@annotation(controllerLog)")
    public Object around(ProceedingJoinPoint joinPoint, Log controllerLog) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            try {
                recordLog(joinPoint, controllerLog, result, error, System.currentTimeMillis() - start);
            } catch (Exception e) {
                // 记录日志失败不能影响业务
                log.error("记录操作日志失败", e);
            }
        }
    }

    /**
     * 组装操作日志并发布事件，由监听器落库
     */
    private void recordLog(ProceedingJoinPoint joinPoint, Log controllerLog,
                           Object result, Throwable error, long cost) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes == null ? null : attributes.getRequest();

        OperLogEvent operLog = new OperLogEvent();
        operLog.setTitle(controllerLog.title());
        operLog.setBusinessType(controllerLog.businessType().getValue());
        operLog.setStatus(error == null ? BusinessStatus.SUCCESS.getValue() : BusinessStatus.FAIL.getValue());
        operLog.setCostTime(cost);
        operLog.setOperTime(LocalDateTime.now());
        operLog.setMethod(joinPoint.getTarget().getClass().getName()
                + "." + joinPoint.getSignature().getName() + "()");

        if (request != null) {
            operLog.setOperIp(JakartaServletUtil.getClientIP(request));
            operLog.setOperUrl(truncate(request.getRequestURI(), 255));
            operLog.setRequestMethod(request.getMethod());
        }

        if (error != null) {
            operLog.setErrorMsg(truncate(error.getMessage(), MAX_LENGTH));
        }

        if (controllerLog.isSaveRequestData()) {
            String param = buildRequestParam(joinPoint, request, controllerLog.excludeParamNames());
            operLog.setOperParam(truncate(param, MAX_LENGTH));
        }

        if (controllerLog.isSaveResponseData() && result != null && !(result instanceof byte[])) {
            operLog.setJsonResult(truncate(JSONUtil.toJsonStr(result), MAX_LENGTH));
        }

        // 操作人信息(operName/deptName)由监听器补，common-log 不依赖认证模块
        eventPublisher.publishEvent(operLog);
    }

    /**
     * 优先取 request 参数，没有则取方法入参
     */
    private String buildRequestParam(ProceedingJoinPoint joinPoint, HttpServletRequest request,
                                     String[] excludeParamNames) {
        Map<String, String> paramMap = request == null ? null : JakartaServletUtil.getParamMap(request);
        if (MapUtil.isNotEmpty(paramMap)) {
            MapUtil.removeAny(paramMap, EXCLUDE_PROPERTIES);
            MapUtil.removeAny(paramMap, excludeParamNames);
            return JSONUtil.toJsonStr(paramMap);
        }
        return buildArgsParam(joinPoint.getArgs(), excludeParamNames);
    }

    /**
     * 方法入参转 JSON 字符串，并剔除敏感字段
     */
    private String buildArgsParam(Object[] args, String[] excludeParamNames) {
        StringJoiner joiner = new StringJoiner(" ");
        for (Object arg : args) {
            if (arg == null || isFilterObject(arg)) {
                continue;
            }
            String json = JSONUtil.toJsonStr(arg);
            // 只有对象才需要脱敏，基本类型/字符串原样拼接
            if (json.startsWith("{")) {
                JSONObject jsonObject = JSONUtil.parseObj(json);
                MapUtil.removeAny(jsonObject, EXCLUDE_PROPERTIES);
                MapUtil.removeAny(jsonObject, excludeParamNames);
                json = jsonObject.toString();
            }
            joiner.add(json);
        }
        return joiner.toString();
    }

    /**
     * 截断超长字符串，避免超出数据库字段长度
     */
    private static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    /**
     * 判断是否需要过滤的对象
     */
    private boolean isFilterObject(Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        }
        if (o instanceof Collection<?> collection) {
            return collection.stream().anyMatch(v -> v instanceof MultipartFile);
        }
        if (o instanceof Map<?, ?> map) {
            return map.values().stream().anyMatch(v -> v instanceof MultipartFile);
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest
                || o instanceof HttpServletResponse || o instanceof BindingResult;
    }
}
