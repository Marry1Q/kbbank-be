package com.kopo_team4.kbbank_backend.domain.autotransfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kbbank_auto_transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_transfer_id")
    private Long autoTransferId;

    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "from_account_number", nullable = false, length = 50)
    private String fromAccountNumber;

    @Column(name = "to_account_number", nullable = false, length = 50)
    private String toAccountNumber;

    @Column(name = "to_account_name", nullable = false, length = 100)
    private String toAccountName;

    @Column(name = "to_bank_code", nullable = false, length = 3)
    private String toBankCode;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "schedule", nullable = false, length = 50)
    private String schedule;

    @Column(name = "next_transfer_date", nullable = false)
    private LocalDate nextTransferDate;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "status", nullable = false, length = 10)
    private String status; // ACTIVE, INACTIVE

    // 신규 추가 필드들
    @Column(name = "last_execution_date")
    private LocalDate lastExecutionDate;

    @Column(name = "last_execution_status", length = 20)
    private String lastExecutionStatus; // SUCCESS, FAILED, PENDING

    @Column(name = "contract_date", nullable = false)
    private LocalDate contractDate;

    @Column(name = "total_installments", nullable = false)
    private Integer totalInstallments;

    @Column(name = "current_installment", nullable = false)
    @Builder.Default
    private Integer currentInstallment = 1;

    @Column(name = "remaining_installments")
    private Integer remainingInstallments;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}


