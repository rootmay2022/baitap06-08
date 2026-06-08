package com.rainbowforest.productcatalogservice.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.rainbowforest.productcatalogservice.entity.Product;

public interface ProductService {

    //Hỗ trợ phân trang và sắp xếp (Ví dụ: xem 10 sản phẩm mỗi trang)
    Page<Product> getAllProducts(Pageable pageable);

    //Phân trang cho cả tìm kiếm theo Category
    Page<Product> getAllProductsByCategory(String category, Pageable pageable);

    //Dùng Optional để an toàn hơn khi không tìm thấy ID
    Optional<Product> getProductById(Long id);

    List<Product> getAllProductsByName(String name);

    //Đổi tên thành saveProduct (Dùng cho cả Add và Update)
    Product saveProduct(Product product);

    void deleteProduct(Long productId);
    
    // Kiểm tra tồn tại trước khi xử lý logic khác
    boolean exists(Long id);
}