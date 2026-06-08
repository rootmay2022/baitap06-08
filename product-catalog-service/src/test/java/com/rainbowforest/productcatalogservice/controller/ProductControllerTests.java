package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

    private static final String PRODUCT_NAME = "iPhone 15";
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_CATEGORY = "Smartphones";

    private Product product;
    private Page<Product> productPage;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setProductName(PRODUCT_NAME);
        product.setCategory(PRODUCT_CATEGORY);

        productPage = new PageImpl<>(Collections.singletonList(product));
    }

    @Test
    @DisplayName("Nên trả về danh sách sản phẩm phân trang khi request hợp lệ")
    void shouldReturnPagedProductsWhenValidRequest() throws Exception {
        when(productService.getAllProducts(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.content[0].productName").value(PRODUCT_NAME));
    }

    @Test
    @DisplayName("Nên trả về 404 khi không tìm thấy sản phẩm theo ID")
    void shouldReturn404WhenProductNotFound() throws Exception {
        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Nên trả về chi tiết sản phẩm khi ID tồn tại")
    void shouldReturnProductDetailWhenIdExists() throws Exception {
        when(productService.getProductById(PRODUCT_ID)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/products/{id}", PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.productName").value(PRODUCT_NAME));
    }

    @Test
    @DisplayName("Nên lọc sản phẩm theo category có phân trang")
    void shouldFilterByCategoryWithPaging() throws Exception {
        when(productService.getAllProductsByCategory(eq(PRODUCT_CATEGORY), any(Pageable.class)))
                .thenReturn(productPage);

        mockMvc.perform(get("/products")
                .param("category", PRODUCT_CATEGORY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category").value(PRODUCT_CATEGORY));
    }
}