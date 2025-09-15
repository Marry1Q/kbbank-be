package com.kopo_team4.kbbank_backend.domain.product.repository;

import com.kopo_team4.kbbank_backend.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByExternalProductId(String externalProductId);
    
    Optional<Product> findByProductCode(String productCode);
    
    Optional<Product> findByProductName(String productName);
    
    List<Product> findByCategoryCategoryCode(String categoryCode);
    
    List<Product> findByProductType(String productType);
    
    List<Product> findByRiskLevel(String riskLevel);
    
    List<Product> findByProductStatus(String productStatus);
    
    @Query("SELECT p FROM Product p WHERE p.productStatus = 'ACTIVE'")
    List<Product> findAllActiveProducts();
    
    @Query("SELECT p FROM Product p WHERE p.category.categoryCode = :categoryCode AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveProductsByCategory(@Param("categoryCode") String categoryCode);
    
    @Query("SELECT p FROM Product p WHERE p.riskLevel = :riskLevel AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveProductsByRiskLevel(@Param("riskLevel") String riskLevel);
    
    @Query("SELECT p FROM Product p WHERE p.productType = :productType AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveProductsByType(@Param("productType") String productType);
    
    @Query("SELECT p FROM Product p WHERE p.expectedReturnRate >= :minRate AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveProductsByMinReturnRate(@Param("minRate") BigDecimal minRate);
    
    @Query("SELECT p FROM Product p WHERE p.minInvestmentAmount <= :amount AND p.maxInvestmentAmount >= :amount AND p.productStatus = 'ACTIVE'")
    List<Product> findActiveProductsByInvestmentAmount(@Param("amount") BigDecimal amount);
    
    // 복합 조건 검색
    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryCode IS NULL OR p.category.categoryCode = :categoryCode) AND " +
           "(:riskLevel IS NULL OR p.riskLevel = :riskLevel) AND " +
           "(:productType IS NULL OR p.productType = :productType) AND " +
           "(:minAmount IS NULL OR p.minInvestmentAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.maxInvestmentAmount <= :maxAmount) AND " +
           "p.productStatus = 'ACTIVE'")
    Page<Product> findProductsByConditions(
            @Param("categoryCode") String categoryCode,
            @Param("riskLevel") String riskLevel,
            @Param("productType") String productType,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );
}
