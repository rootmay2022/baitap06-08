package com.rainbowforest.orderservice.service;

import com.rainbowforest.orderservice.domain.Order;
import java.util.List;

public interface OrderService {
    // Lấy tất cả đơn hàng cho trang quản trị
    List<Order> findAll();
    
    // Xóa đơn hàng theo ID
    void deleteOrder(Long id);

    // Lưu đơn hàng trực tiếp
    Order saveOrder(Order order);
    
    // Xử lý trọn gói: Checkout
    Order processCheckout(Long userId, String cartId);
}