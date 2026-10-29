package com.flux.system.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @ClassName ClientInfoUtilsTest
 * @Author njq
 * @Date 2026/10/29
 * @Description 登录日志客户端信息解析的单测
 */
class ClientInfoUtilsTest {

    private static final String CHROME_WINDOWS_UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/131.0.0.0 Safari/537.36";

    @Test
    @DisplayName("X-Forwarded-For 取第一个地址")
    void forwardedForTakesFirst() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.9, 10.0.0.1");

        assertEquals("203.0.113.9", ClientInfoUtils.getClientIp(request));
    }

    @Test
    @DisplayName("没有代理头时取 remoteAddr")
    void fallsBackToRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.5");

        assertEquals("192.168.1.5", ClientInfoUtils.getClientIp(request));
    }

    @Test
    @DisplayName("IPv6 本机地址归一化为 127.0.0.1")
    void normalizesIpv6Loopback() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

        assertEquals("127.0.0.1", ClientInfoUtils.getClientIp(request));
    }

    @Test
    @DisplayName("拿不到 IP 时返回 null")
    void noIpReturnsNull() {
        assertNull(ClientInfoUtils.getClientIp(mock(HttpServletRequest.class)));
    }

    @Test
    @DisplayName("识别浏览器和操作系统")
    void parsesBrowserAndOs() {
        assertEquals("Chrome", ClientInfoUtils.getBrowser(CHROME_WINDOWS_UA));
        assertEquals("Windows", ClientInfoUtils.getOs(CHROME_WINDOWS_UA));
        assertEquals("Edge", ClientInfoUtils.getBrowser("Mozilla/5.0 Edg/131.0.0.0"));
        assertEquals("iOS", ClientInfoUtils.getOs("Mozilla/5.0 (iPhone; CPU iPhone OS 18_0 like Mac OS X)"));
        assertEquals("Unknown", ClientInfoUtils.getBrowser(null));
    }

    @Test
    @DisplayName("超长内容按列宽截断")
    void truncatesLongValue() {
        assertEquals("abc", ClientInfoUtils.truncate("abcdef", 3));
        assertEquals("abc", ClientInfoUtils.truncate("abc", 3));
        assertNull(ClientInfoUtils.truncate(null, 3));
    }
}
