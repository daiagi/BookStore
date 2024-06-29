package com.bookstore.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class InventoryReservationHandler {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RESERVED_STOCK_PREFIX = "reserved_stock:";
    private static final String RESERVATION_TIMESTAMP_PREFIX = "reservation_timestamp:";
    // time to live in minutes
    private static final int TTL = 10;
    private static final TimeUnit TTL_UNIT = TimeUnit.MINUTES;

    public InventoryReservationHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void reserveStock(int bookId, int amount) {
        increaseReservedStock(bookId, amount);
        setReservationTimestamp(bookId, LocalDateTime.now());
    }

    public void increaseReservedStock(int bookId, int amount) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        String key = RESERVED_STOCK_PREFIX + bookId;
        ops.increment(key, amount);
        redisTemplate.expire(key, TTL, TTL_UNIT);
    }

    public void reduceReservedStock(int bookId, int amount) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = RESERVED_STOCK_PREFIX + bookId;
        ops.increment(key, -amount);
    }

    public int getReservedStock(int bookId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = RESERVED_STOCK_PREFIX + bookId;
        Integer reservedStock = (Integer) ops.get(key);
        return reservedStock != null ? reservedStock : 0;
    }

    public void setReservationTimestamp(int bookId, LocalDateTime timestamp) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = RESERVATION_TIMESTAMP_PREFIX + bookId;
        ops.set(key, timestamp);
        redisTemplate.expire(key, TTL, TTL_UNIT);
    }

    public LocalDateTime getReservationTimestamp(int bookId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String key = RESERVATION_TIMESTAMP_PREFIX + bookId;
        return (LocalDateTime) ops.get(key);
    }
}
