package com.bookstore.config;

import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.util.WebUtils;
import java.util.logging.Logger;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Logger LOGGER = Logger.getLogger(WebSocketConfig.class.getName());
    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/inventory")
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        if (request instanceof ServletServerHttpRequest) {
                            HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();

                            // Log the client's IP address and the requested URL
                            String clientIp = httpRequest.getRemoteAddr();
                            String requestedUrl = httpRequest.getRequestURL().toString();
                            LOGGER.info("Client IP: " + clientIp);
                            LOGGER.info("Requested URL: " + requestedUrl);

                            // Log the headers
                            httpRequest.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                                LOGGER.info("Header: " + headerName + " = " + httpRequest.getHeader(headerName));
                            });

                            // Log the cookies
                            Cookie[] cookies = httpRequest.getCookies();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    LOGGER.info("Cookie: " + cookie.getName() + " = " + cookie.getValue());
                                }
                            }

                            // Get the JWT token from the cookie or query parameter
                            Cookie jwtCookie = WebUtils.getCookie(httpRequest, "JWT_TOKEN");
                            if (jwtCookie != null) {
                                attributes.put("JWT_TOKEN", jwtCookie.getValue());
                                LOGGER.info("JWT Token (Cookie): " + jwtCookie.getValue());
                            } else {
                                String token = httpRequest.getParameter("token");
                                if (token != null) {
                                    attributes.put("JWT_TOKEN", token);
                                    LOGGER.info("JWT Token (Query Param): " + token);
                                }
                            }
                        }
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }

                })
                .setAllowedOrigins("*");
    }
}
