package com.flux.common.satoken.aspect;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.flux.common.core.result.ResultCode;
import com.flux.common.satoken.annotation.PreventDuplicateResubmit;
import com.flux.common.satoken.helper.LoginHelper;
import com.flux.common.web.exception.BizException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @ClassName DuplicateSubmitAspect
 * @Author njq
 * @Date 2026/10/28 16:51
 * @Description 防重复提交切面：用 Redisson 的 setIfAbsent 语义占位，占位成功才放行，
 * 业务执行失败时释放占位，让用户可以立即重试
 */
@Slf4j
@Aspect
@Component
public class DuplicateSubmitAspect {

    private static final String KEY_PREFIX = "repeat_submit:";

    private final RedissonClient redissonClient;

    private final ObjectMapper objectMapper;

    public DuplicateSubmitAspect(RedissonClient redissonClient, ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(preventDuplicateResubmit)")
    public Object around(ProceedingJoinPoint joinPoint, PreventDuplicateResubmit preventDuplicateResubmit) throws Throwable {
        long expire = Math.max(preventDuplicateResubmit.expire(), 1);
        String key = buildKey(joinPoint);
        RBucket<String> bucket = redissonClient.getBucket(key);

        if (!bucket.setIfAbsent("1", Duration.ofSeconds(expire))) {
            log.warn("重复提交已拦截: {}", key);
            throw new BizException(ResultCode.REPEAT_SUBMIT_ERROR);
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            // 业务失败不占用防重窗口，否则用户改完参数要等锁过期才能再试
            bucket.delete();
            throw e;
        }
    }

    /**
     * 方法 + 用户 + 请求参数 组成唯一标识，参数不同视为两次正常提交
     */
    private String buildKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Long userId = LoginHelper.getUserId();

        StringBuilder key = new StringBuilder(KEY_PREFIX)
                .append(signature.getDeclaringType().getSimpleName())
                .append('#')
                .append(signature.getMethod().getName())
                .append(':')
                .append(userId == null ? "anonymous" : userId);

        String args = argsToJson(joinPoint.getArgs());
        if (StringUtils.hasText(args)) {
            key.append(':').append(SaSecureUtil.md5(args));
        }
        return key.toString();
    }

    /**
     * 请求/响应对象和上传文件没法序列化，直接排除
     */
    private String argsToJson(Object[] args) {
        Object[] filtered = Stream.of(args)
                .filter(arg -> arg != null
                        && !(arg instanceof ServletRequest)
                        && !(arg instanceof ServletResponse)
                        && !(arg instanceof MultipartFile))
                .toArray();
        if (filtered.length == 0) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(Arrays.asList(filtered));
        } catch (Exception e) {
            // 序列化失败就退化成「方法 + 用户」维度，不影响防重本身
            log.debug("防重提交参数序列化失败，退化为方法级校验: {}", e.getMessage());
            return "";
        }
    }
}
