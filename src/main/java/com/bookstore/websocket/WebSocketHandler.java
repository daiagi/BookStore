package com.bookstore.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



@Component
public class WebSocketHandler extends TextWebSocketHandler {
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
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            Integer userId = claims.get("user_id", Integer.class);
            sessionUserMap.put(session, userId);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Handle incoming messages from clients if needed
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionUserMap.remove(session);
        bookSubscriptions.values().forEach(sessions -> sessions.remove(session));
    }

    public void notifyUsersAboutStockChange(int bookId, int stock) {
        //log the stock change
        System.out.println("BookId: " + bookId + " Stock: " + stock);
        Set<String> userIds = shoppingCartService.getUserIdsWithBookInCart(bookId);

        //log users with book in cart
        System.out.println("Users with book in cart: " + userIds);
        for (Map.Entry<WebSocketSession, Integer> entry : sessionUserMap.entrySet()) {
            //log
            System.out.println("Session: " + entry.getKey() + " User: " + entry.getValue());
            if (userIds.contains(String.valueOf(entry.getValue()))) {

                //log
                System.out.println("User with book in cart: " + entry.getValue());
                WebSocketSession session = entry.getKey();
                sendMessageToSession(session, bookId, stock);
            }
        }
    }

    private void sendMessageToSession(WebSocketSession session, int bookId, int stock) {
        String message;
        try {
            message = objectMapper.writeValueAsString(Map.of("bookId", bookId, "stock", stock));
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
