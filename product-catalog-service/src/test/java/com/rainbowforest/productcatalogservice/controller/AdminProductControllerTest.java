package com.rainbowforest.productcatalogservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminProductControllerTest {

    private static final String PRODUCT_NAME = "iPhone 15";
    private static final String PRODUCT_CATEGORY = "Smartphones";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService; // Service đã được giả lập (Mock)

    @Autowired
    private ObjectMapper mapper; // Sử dụng Bean có sẵn của Spring thay vì tạo mới

    @Test
    @DisplayName("Nên trả về 201 khi lưu sản phẩm thành công")
    void shouldReturn201WhenProductIsSaved() throws Exception {
        // 1. Chuẩn bị dữ liệu mẫu (Ghi chú: Đảm bảo tên thuộc tính khớp với Entity)
        Product product = new Product();
        product.setId(1L);
        product.setProductName(PRODUCT_NAME);
        product.setCategory(PRODUCT_CATEGORY);

        String requestJson = mapper.writeValueAsString(product);

        // 2. Giả lập hành vi của Service (Sửa tên hàm thành saveProduct nếu bạn đã nâng cấp Service)
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        // 3. Thực hiện request và kiểm chứng kết quả
        mockMvc.perform(post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated()) // Mong đợi mã 201
                .andExpect(jsonPath("$.productName").value(PRODUCT_NAME))
                .andExpect(jsonPath("$.category").value(PRODUCT_CATEGORY));

        // 4. Xác nhận Service chỉ được gọi đúng 1 lần
        verify(productService, times(1)).saveProduct(any(Product.class));
    }

    @Test
    @DisplayName("Nên trả về 400 khi nội dung JSON trống")
    void shouldReturn400WhenContentIsEmpty() throws Exception {
        mockMvc.perform(post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }
}