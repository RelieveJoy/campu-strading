package com.campus.interceptor;

import com.campus.properties.JwtProperties;
import com.campus.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器：从 URL 参数中提取 JWT token 并校验。
 * 客户端连接 ws://host/ws?token=xxx
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            log.warn("WebSocket 握手失败：缺少 token");
            return false;
        }
        String token = query.replaceAll(".*token=([^&]+).*", "$1");
        try {
            JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            return true;
        } catch (Exception e) {
            log.warn("WebSocket 握手失败：token 无效");
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
