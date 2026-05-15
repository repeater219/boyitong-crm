package com.boyitong.repository;
import com.boyitong.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByName(String category);
}