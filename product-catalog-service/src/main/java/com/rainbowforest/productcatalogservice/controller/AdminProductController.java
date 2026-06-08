package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.http.header.HeaderGenerator;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

// NÂNG CẤP: Đã xóa import java.util.Optional vì bạn dùng Functional Style 
// (productService.getProductById(id).map(...)) nên không cần khai báo kiểu Optional trực tiếp.

@RestController
@RequestMapping("/admin")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private HeaderGenerator headerGenerator;

    // 1. LẤY CHI TIẾT SẢN PHẨM
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(
                        product, 
                        headerGenerator.getHeadersForSuccessGetMethod(), 
                        HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        null, 
                        headerGenerator.getHeadersForError(), 
                        HttpStatus.NOT_FOUND));
    }

    // 2. THÊM MỚI SẢN PHẨM
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpServletRequest request) {
        if (product == null) {
            return new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.BAD_REQUEST);
        }

        Product newProduct = productService.saveProduct(product); 
        return new ResponseEntity<>(
                newProduct,
                headerGenerator.getHeadersForSuccessPostMethod(request, newProduct.getId()),
                HttpStatus.CREATED);
    }

    // 3. CẬP NHẬT SẢN PHẨM
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
        if (!productService.exists(id)) {
            return new ResponseEntity<>(null, headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
        }

        product.setId(id);
        Product updatedProduct = productService.saveProduct(product);
        
        return new ResponseEntity<>(
                updatedProduct,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }
    
    // 4. XÓA SẢN PHẨM
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
        if (!productService.exists(id)) {
            return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
        }

        productService.deleteProduct(id);
        return new ResponseEntity<>(
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }
}