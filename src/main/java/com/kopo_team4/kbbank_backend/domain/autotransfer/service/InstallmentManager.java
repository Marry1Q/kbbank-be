package com.kopo_team4.kbbank_backend.domain.autotransfer.service;

import com.kopo_team4.kbbank_backend.domain.autotransfer.entity.AutoTransfer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 회차 계산 컴포넌트
 * 
 * 기능: 자동이체의 회차 정보를 계산하고 관리
 * 기준: 최종 납입일자 기준으로 회차 계산
 */
@Component
public class InstallmentManager {
    
    /**
     * 자동이체의 회차 정보를 업데이트
     * 
     * @param autoTransfer 업데이트할 자동이체 엔티티
     */
    public void updateInstallmentInfo(AutoTransfer autoTransfer) {
        // 최종 납입일자 기준으로 회차 계산
        LocalDate baseDate = autoTransfer.getLastExecutionDate() != null ? 
            autoTransfer.getLastExecutionDate() : autoTransfer.getContractDate();
        
        int currentInstallment = calculateCurrentInstallment(
            autoTransfer.getContractDate(), 
            baseDate
        );
        
        // 남은 회차 계산
        int remainingInstallments = Math.max(0, 
            autoTransfer.getTotalInstallments() - currentInstallment
        );
        
        // 엔티티 업데이트
        autoTransfer.setCurrentInstallment(currentInstallment);
        autoTransfer.setRemainingInstallments(remainingInstallments);
    }
    
    /**
     * 현재 회차 계산
     * 
     * @param contractDate 계약일
     * @param baseDate 기준일 (최종 납입일자)
     * @return 현재 회차 (계약일 = 1회차)
     */
    public int calculateCurrentInstallment(LocalDate contractDate, LocalDate baseDate) {
        // 계약일부터 기준일까지의 개월 수 계산 (계약일 = 1회차)
        long monthsBetween = ChronoUnit.MONTHS.between(contractDate, baseDate);
        return (int) monthsBetween + 1;
    }
    
    /**
     * 남은 회차 계산
     * 
     * @param totalInstallments 총 회차
     * @param currentInstallment 현재 회차
     * @return 남은 회차
     */
    public int calculateRemainingInstallments(int totalInstallments, int currentInstallment) {
        return Math.max(0, totalInstallments - currentInstallment);
    }
    
    /**
     * 1회차 여부 확인
     * 
     * @param autoTransfer 자동이체 엔티티
     * @return 1회차 여부
     */
    public boolean isFirstInstallment(AutoTransfer autoTransfer) {
        return autoTransfer.getCurrentInstallment() == 1;
    }
}
