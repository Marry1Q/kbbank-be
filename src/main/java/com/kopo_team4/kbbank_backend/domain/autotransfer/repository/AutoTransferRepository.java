package com.kopo_team4.kbbank_backend.domain.autotransfer.repository;

import com.kopo_team4.kbbank_backend.domain.autotransfer.entity.AutoTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AutoTransferRepository extends JpaRepository<AutoTransfer, Long> {

    List<AutoTransfer> findByUserId(String userId);

    List<AutoTransfer> findByFromAccountId(Long fromAccountId);

    List<AutoTransfer> findByStatusAndNextTransferDateLessThanEqual(String status, LocalDate date);
    
    /**
     * 실행 예정인 자동이체 조회 (스케줄러용)
     * 
     * @param status 활성 상태
     * @param date 실행 예정일
     * @return 실행 예정인 자동이체 목록
     */
    @Query("SELECT at FROM AutoTransfer at WHERE at.status = :status AND at.nextTransferDate <= :date")
    List<AutoTransfer> findScheduledTransfers(@Param("status") String status, @Param("date") LocalDate date);
    
    /**
     * 특정 계좌의 활성 자동이체 조회
     * 
     * @param fromAccountId 출금 계좌 ID
     * @param status 활성 상태
     * @return 활성 자동이체 목록
     */
    List<AutoTransfer> findByFromAccountIdAndStatus(Long fromAccountId, String status);
    
    /**
     * 특정 계좌로 입금되는 자동이체 조회
     * 
     * @param toAccountNumber 입금 계좌번호
     * @return 해당 계좌로 입금되는 자동이체 목록
     */
    List<AutoTransfer> findByToAccountNumber(String toAccountNumber);
}


