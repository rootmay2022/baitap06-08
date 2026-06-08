package com.rainbowforest.orderservice.redis;

import java.util.List;

public interface CartRedisRepository {

    // Thêm item vào Redis
    void addItemToCart(String key, Object item);

    // QUAN TRỌNG: Dùng <T> List<T> để khi truyền Item.class vào, 
    // nó sẽ trả về List<Item> thay vì Collection<Object> chung chung.
    <T> List<T> getCart(String key, Class<T> type);

    // Xóa một item cụ thể trong Redis
    void deleteItemFromCart(String key, Object item);

    // Xóa nguyên cái giỏ hàng
    void deleteCart(String key);
}