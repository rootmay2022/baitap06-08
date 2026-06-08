package com.rainbowforest.orderservice.controller;

import com.rainbowforest.orderservice.domain.Order;
import com.rainbowforest.orderservice.http.header.HeaderGenerator;
import com.rainbowforest.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HeaderGenerator headerGenerator;

    // 1. Lấy tất cả đơn hàng (Hàm này phục vụ trang OrderList nè)
   @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        // Trả về danh sách rỗng [] với mã 200, frontend sẽ không bị treo
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // 2. Tạo đơn hàng (Checkout)
    @PostMapping("/{userId}")
    public ResponseEntity<Order> saveOrder(
            @PathVariable("userId") Long userId,
            @CookieValue(value = "cartId") String cartId,
            HttpServletRequest request) {
        try {
            String cleanCartId = cartId.replace("cartId=", "");
            Order order = orderService.processCheckout(userId, cleanCartId);
            return new ResponseEntity<>(
                    order,
                    headerGenerator.getHeadersForSuccessPostMethod(request, order.getId()),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.BAD_REQUEST);
        }
    }

    // 3. Xóa đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
        try {
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}