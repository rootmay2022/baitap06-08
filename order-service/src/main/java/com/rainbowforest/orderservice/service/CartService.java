package com.rainbowforest.orderservice.service;

import java.util.List;
import com.rainbowforest.orderservice.domain.Item;

public interface CartService {

    void addItemToCart(String cartId, Long productId, Integer quantity);

    // SỬA TẠI ĐÂY: Đổi List<Object> thành List<Item>
    List<Item> getCart(String cartId);

    void changeItemQuantity(String cartId, Long productId, Integer quantity);

    void deleteItemFromCart(String cartId, Long productId);

    boolean checkIfItemIsExist(String cartId, Long productId);

    List<Item> getAllItemsFromCart(String cartId);

    void deleteCart(String cartId);
}