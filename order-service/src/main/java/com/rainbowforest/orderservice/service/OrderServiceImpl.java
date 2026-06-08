package com.rainbowforest.orderservice.service;

import com.rainbowforest.orderservice.domain.Item;
import com.rainbowforest.orderservice.domain.Order;
import com.rainbowforest.orderservice.domain.User;
import com.rainbowforest.orderservice.feignclient.UserClient;
import com.rainbowforest.orderservice.repository.OrderRepository;
import com.rainbowforest.orderservice.utilities.OrderUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional 
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserClient userClient;

    // --- 1. LẤY TẤT CẢ ĐƠN HÀNG (MỚI THÊM) ---
    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    // --- 2. XÓA ĐƠN HÀNG (MỚI THÊM) ---
    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order processCheckout(Long userId, String cartId) {
        // 1. Lấy danh sách item từ Redis (thông qua CartService)
        List<Item> cart = cartService.getAllItemsFromCart(cartId);
        
        if (cart == null || cart.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống!");
        }

        // 2. Gọi User Service qua FeignClient để lấy thông tin User
        User user = userClient.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy thông tin người dùng!");
        }

        // 3. Khởi tạo và thiết lập thông tin Order
        Order order = new Order();
        order.setUser(user);
        order.setItems(cart);
        order.setTotal(OrderUtilities.countTotalPrice(cart)); // Tính tổng tiền
        order.setOrderedDate(LocalDate.now());
        order.setStatus("PAYMENT_EXPECTED");

        // 4. Lưu Order vào Database (MySQL/PostgreSQL)
        Order savedOrder = orderRepository.save(order);

        // 5. Xóa giỏ hàng trong Redis sau khi tạo đơn hàng thành công
        cartService.deleteCart(cartId);

        return savedOrder;
    }
}