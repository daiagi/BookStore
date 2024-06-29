package com.bookstore.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bookstore.config.WebSocketConfig;
import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = Logger.getLogger(WebSocketConfig.class.getName());
    private final Map<WebSocketSession, Integer> sessionUserMap = new ConcurrentHashMap<>();
    private final Map<Integer, Set<WebSocketSession>> bookSubscriptions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ShoppingCartService shoppingCartService;
    private final JwtTokenProvider jwtTokenProvider;

    public WebSocketHandler(ShoppingCartService shoppingCartService, JwtTokenProvider jwtTokenProvider) {
        this.shoppingCartService = shoppingCartService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = (String) session.getAttributes().get("JWT_TOKEN");
        LOGGER.info("WebSocket connection established. Session ID: " + session.getId());
        LOGGER.info("JWT Token: " + token);
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token)) {
                    Claims claims = jwtTokenProvider.getClaims(token);
                    Integer userId = claims.get("user_id", Integer.class);
                    sessionUserMap.put(session, userId);
                    LOGGER.info("User authenticated. User ID: " + userId);
                } else {
                    LOGGER.warning("Invalid JWT token. Closing session.");
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid JWT token"));
                }
            } catch (Exception e) {
                LOGGER.severe("Exception during token validation: " + e.getMessage());
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Exception during token validation"));
            }
        } else {
            LOGGER.warning("Missing JWT token. Closing session.");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing JWT token"));
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from clients if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOGGER.info("WebSocket connection closed. Session ID: " + session.getId() + ", Status: " + status);
        sessionUserMap.remove(session);
        bookSubscriptions.values().forEach(sessions -> sessions.remove(session));
    }

    public void notifyUsersAboutStockChange(int bookId, int stock) {
        // Log the stock change
        LOGGER.info("Stock change notification. Book ID: " + bookId + ", Stock: " + stock);
        Set<String> userIds = shoppingCartService.getUserIdsWithBookInCart(bookId);

        // Log users with book in cart
        LOGGER.info("Users with book in cart: " + userIds);
        for (Map.Entry<WebSocketSession, Integer> entry : sessionUserMap.entrySet()) {
            // Log each session and user ID
            LOGGER.info("Session: " + entry.getKey().getId() + ", User: " + entry.getValue());
            if (userIds.contains(String.valueOf(entry.getValue()))) {
                // Log user with book in cart
                LOGGER.info("User with book in cart: " + entry.getValue());
                WebSocketSession session = entry.getKey();
                sendMessageToSession(session, bookId, stock);
            }
        }
    }

    private void sendMessageToSession(WebSocketSession session, int bookId, int stock) {
        try {
            String message = objectMapper.writeValueAsString(Map.of("bookId", bookId, "stock", stock));
            session.sendMessage(new TextMessage(message));
            LOGGER.info("Sent message to session ID: " + session.getId() + ", Message: " + message);
        } catch (IOException e) {
            LOGGER.severe("Error sending message to session ID: " + session.getId() + ", Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
