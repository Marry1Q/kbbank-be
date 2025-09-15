package com.kopo_team4.kbbank_backend.global.exception;

/**
 * 잔액 부족 예외
 * 
 * 자동이체 실행 시 계좌 잔액이 부족한 경우 발생
 */
public class InsufficientBalanceException extends RuntimeException {
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
