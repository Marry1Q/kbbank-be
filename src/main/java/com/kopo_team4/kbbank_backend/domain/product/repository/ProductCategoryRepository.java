package com.kopo_team4.kbbank_backend.domain.product.repository;

import com.kopo_team4.kbbank_backend.domain.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    
    Optional<ProductCategory> findByCategoryCode(String categoryCode);
    
    List<ProductCategory> findByRiskLevel(String riskLevel);
    
    List<ProductCategory> findAllByOrderByCategoryCode();
}
