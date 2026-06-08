package com.rainbowforest.productcatalogservice.controller;

import com.rainbowforest.productcatalogservice.entity.Product;
import com.rainbowforest.productcatalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * GET CATEGORIES (Đã thêm hàm này để Fix lỗi 400)
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        // Tạo danh mục ảo để Frontend hiển thị và cho phép thêm sản phẩm
        List<Map<String, Object>> categories = new ArrayList<>();
        
        Map<String, Object> cat1 = new HashMap<>();
        cat1.put("id", 1L);
        cat1.put("name", "Điện thoại & Máy tính bảng");
        categories.add(cat1);

        Map<String, Object> cat2 = new HashMap<>();
        cat2.put("id", 2L);
        cat2.put("name", "Laptop & Thiết bị IT");
        categories.add(cat2);

        Map<String, Object> cat3 = new HashMap<>();
        cat3.put("id", 3L);
        cat3.put("name", "Thời trang");
        categories.add(cat3);

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * GET ALL
     */
    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            Pageable pageable) {

        Page<Product> products;

        if (name != null) {
            List<Product> listByName = productService.getAllProductsByName(name);
            products = new PageImpl<>(listByName, pageable, listByName.size());
        } 
        else if (category != null) {
            products = productService.getAllProductsByCategory(category, pageable);
        } 
        else {
            products = productService.getAllProducts(pageable);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * GET BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {

        Optional<Product> productOpt = productService.getProductById(id);

        return productOpt
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * CREATE
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * UPDATE
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        if (productService.exists(id)) {
            product.setId(id);
            Product updatedProduct = productService.saveProduct(product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        if (productService.exists(id)) {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}