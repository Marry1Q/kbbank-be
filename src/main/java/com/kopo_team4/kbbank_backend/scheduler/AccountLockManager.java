package com.kopo_team4.kbbank_backend.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 계좌별 동시성 제어 매니저
 * 
 * 기능: 계좌별로 락을 관리하여 동시 자동이체 실행을 방지
 * 사용처: 자동이체 스케줄러에서 동일 계좌의 여러 자동이체 처리 시
 */
@Component
@Slf4j
public class AccountLockManager {
    
    private final ConcurrentHashMap<Long, ReentrantLock> accountLocks = new ConcurrentHashMap<>();
    
    /**
     * 계좌별 락을 사용하여 작업 실행
     * 
     * @param accountId 계좌 ID
     * @param task 실행할 작업
     */
    public void executeWithAccountLock(Long accountId, Runnable task) {
        ReentrantLock lock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
        
        try {
            log.debug("계좌 {} 락 획득 시도", accountId);
            
            if (lock.tryLock(30, TimeUnit.SECONDS)) {
                log.debug("계좌 {} 락 획득 성공", accountId);
                try {
                    task.run();
                } finally {
                    lock.unlock();
                    log.debug("계좌 {} 락 해제", accountId);
                }
            } else {
                log.warn("계좌 {} 락 획득 실패 (30초 타임아웃)", accountId);
                throw new RuntimeException("계좌 잠금 획득 실패: " + accountId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("계좌 {} 락 대기 중 인터럽트 발생", accountId, e);
            throw new RuntimeException("계좌 잠금 대기 중 인터럽트", e);
        }
    }
    
    /**
     * 특정 계좌의 락 제거 (메모리 정리용)
     * 
     * @param accountId 계좌 ID
     */
    public void removeLock(Long accountId) {
        accountLocks.remove(accountId);
        log.debug("계좌 {} 락 제거", accountId);
    }
    
    /**
     * 모든 락 제거 (메모리 정리용)
     */
    public void clearAllLocks() {
        int size = accountLocks.size();
        accountLocks.clear();
        log.info("모든 계좌 락 제거 완료 ({}개)", size);
    }
}
