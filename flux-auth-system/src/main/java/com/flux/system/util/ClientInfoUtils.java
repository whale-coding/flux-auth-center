package com.flux.system.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * @ClassName ClientInfoUtils
 * @Author njq
 * @Date 2026/10/29
 * @Description 从请求里取登录日志需要的 IP、浏览器、操作系统。
 * 只做粗粒度识别，识别不出来返回 Unknown，不为了日志引入额外的 UA 解析库
 */
public final class ClientInfoUtils {

    private static final String UNKNOWN = "Unknown";

    private static final int MAX_IP_LENGTH = 128;
    private static final int MAX_UA_ITEM_LENGTH = 50;

    private ClientInfoUtils() {
    }

    /**
     * 客户端 IP：优先代理头，取 X-Forwarded-For 的第一个地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.hasText(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }
        if (!StringUtils.hasText(ip)) {
            return null;
        }
        int comma = ip.indexOf(',');
        if (comma > 0) {
            ip = ip.substring(0, comma);
        }
        ip = ip.trim();
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return truncate(ip, MAX_IP_LENGTH);
    }

    /**
     * 浏览器类型
     */
    public static String getBrowser(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }
        String ua = userAgent.toLowerCase(Locale.ROOT);
        String browser;
        if (ua.contains("edg/")) {
            browser = "Edge";
        } else if (ua.contains("opr/") || ua.contains("opera")) {
            browser = "Opera";
        } else if (ua.contains("chrome") || ua.contains("crios")) {
            browser = "Chrome";
        } else if (ua.contains("firefox") || ua.contains("fxios")) {
            browser = "Firefox";
        } else if (ua.contains("safari")) {
            browser = "Safari";
        } else if (ua.contains("msie") || ua.contains("trident")) {
            browser = "IE";
        } else {
            browser = UNKNOWN;
        }
        return truncate(browser, MAX_UA_ITEM_LENGTH);
    }

    /**
     * 操作系统
     */
    public static String getOs(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }
        String ua = userAgent.toLowerCase(Locale.ROOT);
        String os;
        if (ua.contains("windows")) {
            os = "Windows";
        } else if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) {
            os = "iOS";
        } else if (ua.contains("android")) {
            os = "Android";
        } else if (ua.contains("mac os x") || ua.contains("macintosh")) {
            os = "macOS";
        } else if (ua.contains("linux")) {
            os = "Linux";
        } else {
            os = UNKNOWN;
        }
        return truncate(os, MAX_UA_ITEM_LENGTH);
    }

    /**
     * 按数据库列宽截断，避免超长直接抛 SQL 异常
     */
    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
