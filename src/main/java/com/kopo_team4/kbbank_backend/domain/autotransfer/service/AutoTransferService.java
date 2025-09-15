package com.kopo_team4.kbbank_backend.domain.autotransfer.service;

import com.kopo_team4.kbbank_backend.domain.account.repository.AccountRepository;
import com.kopo_team4.kbbank_backend.domain.autotransfer.controller.AutoTransferController.PaymentInfoResponse;
import com.kopo_team4.kbbank_backend.domain.autotransfer.dto.AutoTransferDto;
import com.kopo_team4.kbbank_backend.domain.autotransfer.entity.AutoTransfer;
import com.kopo_team4.kbbank_backend.domain.autotransfer.repository.AutoTransferRepository;
import com.kopo_team4.kbbank_backend.domain.user.entity.User;
import com.kopo_team4.kbbank_backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AutoTransferService {

    private final AutoTransferRepository autoTransferRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AutoTransferDto.Response create(AutoTransferDto.CreateRequest request) {
        // 1. userCi로 userId 조회
        String userId = userRepository.findByUserCi(request.getUserCi())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자(CI) 입니다."));

        // 2. 계좌번호로 계좌 조회 및 소유권 검증
        Long fromAccountId = accountRepository.findByAccountNum(request.getFromAccountNumber())
                .map(account -> {
                    if (!userId.equals(account.getUserId())) {
                        throw new IllegalArgumentException("해당 계좌의 소유자가 아닙니다.");
                    }
                    return account.getAccountId();
                })
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출금 계좌입니다."));

        // 3. 다음 이체 예정일 계산
        LocalDate nextDate = request.getNextTransferDate() != null
                ? request.getNextTransferDate()
                : calculateNextTransferDate(request.getSchedule());

        // 4. 자동이체 엔티티 생성
        AutoTransfer entity = AutoTransfer.builder()
                .userId(userId)
                .fromAccountId(fromAccountId)
                .fromAccountNumber(request.getFromAccountNumber())
                .toAccountNumber(request.getToAccountNumber())
                .toAccountName(request.getToAccountName())
                .toBankCode(request.getToBankCode())
                .amount(request.getAmount())
                .schedule(request.getSchedule())
                .nextTransferDate(nextDate)
                .memo(request.getMemo())
                .status("ACTIVE")  // 자동이체는 항상 ACTIVE 상태로 생성
                .lastExecutionStatus(request.getStatus() != null ? request.getStatus() : request.getLastExecutionStatus())  // status 값을 lastExecutionStatus로 매핑
                .contractDate(LocalDate.now())
                .totalInstallments(request.getPeriodMonths() != null ? request.getPeriodMonths() : 12)
                .currentInstallment(request.getCurrentInstallment() != null ? request.getCurrentInstallment() : 1)
                .remainingInstallments(request.getRemainingInstallments() != null ? request.getRemainingInstallments() : 
                                      calculateRemainingInstallments(
                                          request.getPeriodMonths() != null ? request.getPeriodMonths() : 12,
                                          request.getCurrentInstallment() != null ? request.getCurrentInstallment() : 1))
                .lastExecutionDate(request.getLastExecutionDate())
                .build();

        // 5. 저장 및 응답
        AutoTransfer saved = autoTransferRepository.save(entity);
        return toResponse(saved);
    }

    /**
     * 특정 계좌로 입금되는 자동이체 목록 조회 (상품별 납입 정보)
     * 
     * @param toAccountNumber 입금 계좌번호
     * @return 해당 계좌로 입금되는 자동이체의 상세 납입 정보 목록
     */
    @Transactional(readOnly = true)
    public List<PaymentInfoResponse> getIncomingTransfers(String toAccountNumber) {
        List<AutoTransfer> autoTransfers = autoTransferRepository.findByToAccountNumber(toAccountNumber);
        
        return autoTransfers.stream()
                .map(this::toPaymentInfoResponse)
                .toList();
    }
    
    /**
     * AutoTransfer 엔티티를 PaymentInfoResponse로 변환
     */
    private PaymentInfoResponse toPaymentInfoResponse(AutoTransfer autoTransfer) {
        // remainingInstallments가 null인 경우 계산
        Integer remainingInstallments = autoTransfer.getRemainingInstallments();
        if (remainingInstallments == null && autoTransfer.getTotalInstallments() != null && autoTransfer.getCurrentInstallment() != null) {
            remainingInstallments = calculateRemainingInstallments(autoTransfer.getTotalInstallments(), autoTransfer.getCurrentInstallment());
        }
        
        return PaymentInfoResponse.builder()
                .autoTransferId(autoTransfer.getAutoTransferId())
                .fromAccountNumber(autoTransfer.getFromAccountNumber())
                .toAccountNumber(autoTransfer.getToAccountNumber())
                .amount(autoTransfer.getAmount())
                .nextPaymentDate(autoTransfer.getNextTransferDate())
                .monthlyAmount(autoTransfer.getAmount())  // 월 납입액은 이체 금액과 동일
                .currentInstallment(autoTransfer.getCurrentInstallment())
                .totalInstallments(autoTransfer.getTotalInstallments())
                .remainingInstallments(remainingInstallments)
                .paymentStatus(autoTransfer.getLastExecutionStatus())
                .isFirstInstallment(autoTransfer.getCurrentInstallment() == 1)
                .lastExecutionDate(autoTransfer.getLastExecutionDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AutoTransferDto.Response> listByUser(AutoTransferDto.SearchRequest request) {
        // 계좌번호로 계좌 조회
        Long fromAccountId = accountRepository.findByAccountNum(request.getFromAccountNumber())
                .map(account -> account.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 출금 계좌입니다."));

        // 해당 계좌에서 출금되는 자동이체 목록 조회
        return autoTransferRepository.findByFromAccountId(fromAccountId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AutoTransferDto.Response get(Long id) {
        AutoTransfer entity = autoTransferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자동이체를 찾을 수 없습니다. id=" + id));
        return toResponse(entity);
    }

    public AutoTransferDto.Response update(Long id, String userId, AutoTransferDto.UpdateRequest request) {
        AutoTransfer entity = autoTransferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자동이체를 찾을 수 없습니다. id=" + id));

        // 소유권 검증 제거 - X-USER-ID가 제공된 경우에만 검증 (선택적)
        if (userId != null && !userId.isEmpty()) {
            validateOwnership(userId, entity.getFromAccountId());
        }

        LocalDate nextDate = request.getNextTransferDate() != null
                ? request.getNextTransferDate()
                : calculateNextTransferDate(request.getSchedule());

        AutoTransfer updated = AutoTransfer.builder()
                .autoTransferId(entity.getAutoTransferId())
                .userId(entity.getUserId())
                .fromAccountId(entity.getFromAccountId())
                .fromAccountNumber(entity.getFromAccountNumber())  // 기존 값 유지
                .toAccountNumber(request.getToAccountNumber())
                .toAccountName(request.getToAccountName())
                .toBankCode(request.getToBankCode())
                .amount(request.getAmount())
                .schedule(request.getSchedule())
                .nextTransferDate(nextDate)
                .memo(request.getMemo())
                .status(entity.getStatus())
                .lastExecutionDate(entity.getLastExecutionDate())  // 기존 값 유지
                .lastExecutionStatus(entity.getLastExecutionStatus())  // 기존 값 유지
                .contractDate(entity.getContractDate())  // 기존 값 유지 - 필수 필드
                .totalInstallments(entity.getTotalInstallments())  // 기존 값 유지
                .currentInstallment(entity.getCurrentInstallment())  // 기존 값 유지
                .remainingInstallments(entity.getRemainingInstallments())  // 기존 값 유지
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        AutoTransfer saved = autoTransferRepository.save(updated);
        return toResponse(saved);
    }

    public void delete(Long id, String userId) {
        AutoTransfer entity = autoTransferRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("자동이체를 찾을 수 없습니다. id=" + id));

        // 소유권 검증 제거 - X-USER-ID가 제공된 경우에만 검증 (선택적)
        if (userId != null && !userId.isEmpty()) {
            validateOwnership(userId, entity.getFromAccountId());
        }
        
        autoTransferRepository.delete(entity);
    }
    

    private void validateOwnership(String userId, Long fromAccountId) {
        // Account.userId == provided userId
        accountRepository.findById(fromAccountId).ifPresentOrElse(acc -> {
            if (!userId.equals(acc.getUserId())) {
                throw new IllegalArgumentException("계좌 소유자가 아닙니다.");
            }
        }, () -> {
            throw new IllegalArgumentException("존재하지 않는 출금 계좌입니다.");
        });
    }

    private LocalDate calculateNextTransferDate(String schedule) {
        // 간단 규칙: "매월 NN일" / "매주 월요일" 등 문자열 기반 처리
        LocalDate today = LocalDate.now();
        try {
            if (schedule.startsWith("매월")) {
                String digits = schedule.replaceAll("[^0-9]", "");
                int day = Math.max(1, Math.min(28, Integer.parseInt(digits.isEmpty() ? "1" : digits)));
                LocalDate nextMonth = today.plusMonths(1);
                return nextMonth.withDayOfMonth(Math.min(day, nextMonth.lengthOfMonth()));
            }
        } catch (Exception ignored) {}
        return today.plusDays(1);
    }

    private AutoTransferDto.Response toResponse(AutoTransfer e) {
        // userId로 userCi 조회
        String userCi = userRepository.findByUserId(e.getUserId())
                .map(User::getUserCi)
                .orElse(null);
        
        // remainingInstallments가 null인 경우 계산
        Integer remainingInstallments = e.getRemainingInstallments();
        if (remainingInstallments == null && e.getTotalInstallments() != null && e.getCurrentInstallment() != null) {
            remainingInstallments = calculateRemainingInstallments(e.getTotalInstallments(), e.getCurrentInstallment());
        }
        
        return AutoTransferDto.Response.builder()
                .autoTransferId(e.getAutoTransferId())
                .userId(e.getUserId())
                .userCi(userCi)  // 조회한 userCi 설정
                .fromAccountId(e.getFromAccountId())
                .toAccountNumber(e.getToAccountNumber())
                .toAccountName(e.getToAccountName())
                .toBankCode(e.getToBankCode())
                .amount(e.getAmount())
                .schedule(e.getSchedule())
                .nextTransferDate(e.getNextTransferDate())
                .memo(e.getMemo())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .lastExecutionStatus(e.getLastExecutionStatus())  // 새로 추가
                .totalInstallments(e.getTotalInstallments())
                .currentInstallment(e.getCurrentInstallment())
                .remainingInstallments(remainingInstallments)
                .lastExecutionDate(e.getLastExecutionDate())
                .build();
    }
    
    /**
     * 남은 회차 계산
     * 
     * @param totalInstallments 총 회차
     * @param currentInstallment 현재 회차
     * @return 남은 회차
     */
    private Integer calculateRemainingInstallments(Integer totalInstallments, Integer currentInstallment) {
        if (totalInstallments == null || currentInstallment == null) {
            return null;
        }
        return Math.max(0, totalInstallments - currentInstallment);
    }
}


