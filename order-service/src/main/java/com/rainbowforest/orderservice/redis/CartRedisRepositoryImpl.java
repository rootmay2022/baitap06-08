package com.rainbowforest.orderservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CartRedisRepositoryImpl implements CartRedisRepository {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Jedis jedis = new Jedis(); // Đảm bảo Redis Server đang chạy ở localhost:6379

    @Override
    public void addItemToCart(String key, Object item) {
        try {
            String jsonObject = objectMapper.writeValueAsString(item);
            jedis.sadd(key, jsonObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> List<T> getCart(String key, Class<T> type) {
        List<T> cart = new ArrayList<>();
        // Lấy tất cả các chuỗi JSON từ Set trong Redis
        for (String smember : jedis.smembers(key)) {
            try {
                // Chuyển từ JSON ngược lại thành Object kiểu T (ví dụ Item)
                cart.add(objectMapper.readValue(smember, type));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cart;
    }

    @Override
    public void deleteItemFromCart(String key, Object item) {
        try {
            String itemCart = objectMapper.writeValueAsString(item);
            jedis.srem(key, itemCart);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCart(String key) {
        jedis.del(key);
    }
}