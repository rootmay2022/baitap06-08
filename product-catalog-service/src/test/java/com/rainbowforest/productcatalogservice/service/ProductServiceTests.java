package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    private static final String PRODUCT_NAME = "test";
    private static final Long PRODUCT_ID = 5L;
    private static final String PRODUCT_CATEGORY = "testCategory";

    private List<Product> products;
    private Product product;
    private Page<Product> productPage;
    private Pageable pageable;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp(){
        product = new Product();
        product.setId(PRODUCT_ID);
        product.setProductName(PRODUCT_NAME);
        product.setCategory(PRODUCT_CATEGORY);

        products = new ArrayList<>();
        products.add(product);

        pageable = PageRequest.of(0, 10);
        productPage = new PageImpl<>(products);
    }

    @Test
    @DisplayName("Nên lấy tất cả sản phẩm có hỗ trợ phân trang")
    void get_all_product_test(){
        Mockito.when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        Page<Product> foundProducts = productService.getAllProducts(pageable);

        assertNotNull(foundProducts);
        assertEquals(1, foundProducts.getContent().size());
        assertEquals(PRODUCT_NAME, foundProducts.getContent().get(0).getProductName());

        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Nên lấy được sản phẩm theo ID và trả về Optional")
    void get_one_by_id_test(){
        Mockito.when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        Optional<Product> found = productService.getProductById(PRODUCT_ID);

        assertTrue(found.isPresent());
        assertEquals(PRODUCT_ID, found.get().getId());

        Mockito.verify(productRepository, Mockito.times(1)).findById(PRODUCT_ID);
    }

    @Test
    @DisplayName("Nên lấy sản phẩm theo Category có phân trang")
    void get_all_product_by_category_test(){
        Mockito.when(productRepository.findAllByCategory(eq(PRODUCT_CATEGORY), any(Pageable.class)))
                .thenReturn(productPage);

        Page<Product> foundProducts = productService.getAllProductsByCategory(PRODUCT_CATEGORY, pageable);

        assertEquals(PRODUCT_CATEGORY, foundProducts.getContent().get(0).getCategory());
        Mockito.verify(productRepository, Mockito.times(1)).findAllByCategory(PRODUCT_CATEGORY, pageable);
    }

    @Test
    @DisplayName("Nên tìm sản phẩm theo tên (không phân biệt hoa thường)")
    void get_all_products_by_name_test(){
        // SỬA TẠI ĐÂY: Đổi sang tên hàm mới findAllByProductName...
        Mockito.when(productRepository.findAllByProductNameContainingIgnoreCase(PRODUCT_NAME))
                .thenReturn(products);

        List<Product> foundProducts = productService.getAllProductsByName(PRODUCT_NAME);

        assertFalse(foundProducts.isEmpty());
        assertEquals(PRODUCT_NAME, foundProducts.get(0).getProductName());

        // SỬA TẠI ĐÂY: Verify cũng phải dùng tên hàm mới
        Mockito.verify(productRepository, Mockito.times(1))
                .findAllByProductNameContainingIgnoreCase(PRODUCT_NAME);
    }
}