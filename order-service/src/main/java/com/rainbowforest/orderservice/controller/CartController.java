package com.rainbowforest.orderservice.controller;

import com.rainbowforest.orderservice.domain.Item;
import com.rainbowforest.orderservice.http.header.HeaderGenerator;
import com.rainbowforest.orderservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/cart") // Gom prefix lại cho sạch code
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private HeaderGenerator headerGenerator;

    // 1. Dùng @CookieValue để Spring tự bóc tách cartId cho bạn, không cần .replace() thủ công
    @GetMapping
    public ResponseEntity<List<Item>> getCart(@CookieValue(value = "cartId") String cartId) {
        
        List<Item> cart = cartService.getAllItemsFromCart(cartId);

        if (cart != null && !cart.isEmpty()) {
            return new ResponseEntity<>(
                    cart,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    // 2. Tinh gọn logic thêm sản phẩm
    @PostMapping(params = {"productId", "quantity"})
    public ResponseEntity<List<Item>> addItemToCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @CookieValue(value = "cartId") String cartId,
            HttpServletRequest request) {

        // Đẩy toàn bộ logic "nếu trống thì thêm, nếu có thì tăng số lượng" vào trong Service
        cartService.addItemToCart(cartId, productId, quantity);
        
        List<Item> cart = cartService.getAllItemsFromCart(cartId);

        return new ResponseEntity<>(
                cart,
                headerGenerator.getHeadersForSuccessPostMethod(request, Long.parseLong(cartId)),
                HttpStatus.CREATED);
    }

    // 3. Xóa sản phẩm
    @DeleteMapping(params = "productId")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam("productId") Long productId,
            @CookieValue(value = "cartId") String cartId) {

        cartService.deleteItemFromCart(cartId, productId);
        
        return new ResponseEntity<>(
                null,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }
}