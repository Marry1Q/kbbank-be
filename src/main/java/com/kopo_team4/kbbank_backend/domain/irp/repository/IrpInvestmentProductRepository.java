package com.kopo_team4.kbbank_backend.domain.irp.repository;

import com.kopo_team4.kbbank_backend.domain.irp.entity.IrpInvestmentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IrpInvestmentProductRepository extends JpaRepository<IrpInvestmentProduct, Long> {
    List<IrpInvestmentProduct> findByIrpId(Long irpId);
    Optional<IrpInvestmentProduct> findByProductId(Long productId);
    List<IrpInvestmentProduct> findByProductName(String productName);
    List<IrpInvestmentProduct> findByBankCodeStd(String bankCodeStd);
}