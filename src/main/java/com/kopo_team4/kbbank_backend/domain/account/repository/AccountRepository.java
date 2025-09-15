package com.kopo_team4.kbbank_backend.domain.account.repository;

import com.kopo_team4.kbbank_backend.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(String userId);

    Optional<Account> findByUserIdAndAccountNum(String userId, String accountNum);

    Optional<Account> findByAccountNum(String accountNum);

    /**
     * 계좌번호 존재 여부 확인
     */
    boolean existsByAccountNum(String accountNum);

    /**
     * 계좌 잔액 업데이트
     */
    @Modifying
    @Query("UPDATE Account a SET a.balanceAmt = :newBalance WHERE a.accountId = :accountId")
    int updateAccountBalance(@Param("accountId") Long accountId, @Param("newBalance") BigDecimal newBalance);

    /**
     * 계좌번호로 계좌 정보와 계좌주명(username) 조회 (오픈뱅킹용)
     * Account 엔티티와 User 엔티티를 조인하여 계좌주명을 함께 조회
     */
    @Query("SELECT a, u.username FROM Account a " +
           "JOIN com.kopo_team4.kbbank_backend.domain.user.entity.User u ON a.userId = u.userId " +
           "WHERE a.accountNum = :accountNum")
    Optional<Object[]> findAccountWithUsernameByAccountNum(@Param("accountNum") String accountNum);

    /**
     * 계좌번호와 은행코드로 계좌 정보와 계좌주명 조회 (추가 검증용)
     */
    @Query("SELECT a, u.username FROM Account a " +
           "JOIN com.kopo_team4.kbbank_backend.domain.user.entity.User u ON a.userId = u.userId " +
           "WHERE a.accountNum = :accountNum AND a.bankCodeStd = :bankCode")
    Optional<Object[]> findAccountWithUsernameByAccountNumAndBankCode(@Param("accountNum") String accountNum, 
                                                                      @Param("bankCode") String bankCode);

    /**
     * 계좌 수익률 업데이트 (펀드용)
     */
    @Modifying
    @Query("UPDATE Account a SET a.currentReturnRate = :returnRate, a.lastProfitCalculated = :calculatedAt WHERE a.accountId = :accountId")
    int updateAccountReturnRate(@Param("accountId") Long accountId, 
                               @Param("returnRate") BigDecimal returnRate, 
                               @Param("calculatedAt") LocalDateTime calculatedAt);
    
    /**
     * 계좌 조회 (동시성 제어용 - Pessimistic Lock)
     * 
     * @param accountId 계좌 ID
     * @return 계좌 정보 (락 적용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
    Optional<Account> findByIdWithLock(@Param("accountId") Long accountId);
}