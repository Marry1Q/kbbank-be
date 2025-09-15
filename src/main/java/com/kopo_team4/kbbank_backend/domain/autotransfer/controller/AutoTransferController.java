package com.kopo_team4.kbbank_backend.domain.autotransfer.controller;

import com.kopo_team4.kbbank_backend.domain.autotransfer.dto.AutoTransferDto;
import com.kopo_team4.kbbank_backend.domain.autotransfer.service.AutoTransferService;
import com.kopo_team4.kbbank_backend.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
// Note: Use fully-qualified name for Swagger ApiResponse to avoid name clash
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auto-transfers")
@RequiredArgsConstructor
@Slf4j
public class AutoTransferController {

    private final AutoTransferService autoTransferService;

    @PostMapping
    @Operation(
            summary = "자동이체 등록",
            description = "사용자 CI로 사용자 식별 후 출금계좌 소유권 검증을 거쳐 자동이체를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AutoTransferDto.CreateRequest.class),
                            examples = @ExampleObject(value = "{\n  \"userCi\": \"CIxxxxxxxx...\",\n  \"fromAccountNumber\": \"110-123-456789\",\n  \"toAccountNumber\": \"110-123-456789\",\n  \"toAccountName\": \"김철수\",\n  \"toBankCode\": \"081\",\n  \"amount\": 500000,\n  \"schedule\": \"매월 25일\",\n  \"memo\": \"Plan1Q 자동이체\"\n}")
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<ApiResponse<AutoTransferDto.Response>> create(@Valid @RequestBody AutoTransferDto.CreateRequest request) {
        try {
            AutoTransferDto.Response response = autoTransferService.create(request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("자동이체 등록 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("자동이체 등록 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/list")
    @Operation(
            summary = "자동이체 목록 조회", 
            description = "출금 계좌번호로 해당 계좌에서 출금되는 자동이체 목록을 조회합니다.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "fromAccountNumber",
                            description = "출금 계좌번호",
                            required = true,
                            example = "110-123-456789",
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = AutoTransferDto.Response.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"message\": \"자동이체 목록 조회 성공\",\n" +
                                                    "  \"data\": [\n" +
                                                    "    {\n" +
                                                    "      \"autoTransferId\": 1,\n" +
                                                    "      \"userId\": \"user123\",\n" +
                                                    "      \"fromAccountId\": 1,\n" +
                                                    "      \"toAccountNumber\": \"110-987-654321\",\n" +
                                                    "      \"toAccountName\": \"김철수\",\n" +
                                                    "      \"toBankCode\": \"081\",\n" +
                                                    "      \"amount\": 500000,\n" +
                                                    "      \"schedule\": \"매월 25일\",\n" +
                                                    "      \"nextTransferDate\": \"2024-02-25\",\n" +
                                                    "      \"memo\": \"Plan1Q 자동이체\",\n" +
                                                    "      \"status\": \"ACTIVE\",\n" +
                                                    "      \"createdAt\": \"2024-01-01T10:00:00\",\n" +
                                                    "      \"updatedAt\": \"2024-01-01T10:00:00\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"ERROR\",\n" +
                                                    "  \"message\": \"존재하지 않는 출금 계좌입니다.\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"ERROR\",\n" +
                                                    "  \"message\": \"자동이체 목록 조회 중 오류가 발생했습니다.\"\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<AutoTransferDto.Response>>> list(@RequestParam String fromAccountNumber) {
        try {
            AutoTransferDto.SearchRequest request = AutoTransferDto.SearchRequest.builder()
                    .fromAccountNumber(fromAccountNumber)
                    .build();
            List<AutoTransferDto.Response> response = autoTransferService.listByUser(request);
            return ResponseEntity.ok(ApiResponse.success("자동이체 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("자동이체 목록 조회 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("자동이체 목록 조회 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "자동이체 상세 조회", description = "자동이체 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AutoTransferDto.Response>> detail(@PathVariable Long id) {
        try {
            AutoTransferDto.Response response = autoTransferService.get(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("자동이체 상세 조회 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("자동이체 상세 조회 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/incoming")
    @Operation(
            summary = "상품별 자동이체 납입 정보 조회",
            description = "특정 상품 계좌번호로 해당 상품에 입금되는 자동이체의 납입 진행 상황을 조회합니다. " +
                    "Plan1Q 상품 가입 시 해당 상품 계좌로 입금되는 모든 자동이체의 납입 현황을 확인할 수 있습니다.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "toAccountNumber",
                            description = "상품 계좌번호 (입금 계좌번호)",
                            required = true,
                            example = "110-123-456789",
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    schema = @Schema(implementation = PaymentInfoResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"SUCCESS\",\n" +
                                                    "  \"message\": \"상품별 자동이체 납입 정보 조회 성공\",\n" +
                                                    "  \"data\": [\n" +
                                                    "    {\n" +
                                                    "      \"autoTransferId\": 1,\n" +
                                                    "      \"fromAccountNumber\": \"110-987-654321\",\n" +
                                                    "      \"toAccountNumber\": \"110-123-456789\",\n" +
                                                    "      \"amount\": 500000,\n" +
                                                    "      \"nextPaymentDate\": \"2024-02-25\",\n" +
                                                    "      \"currentInstallment\": 3,\n" +
                                                    "      \"totalInstallments\": 12,\n" +
                                                    "      \"remainingInstallments\": 9,\n" +
                                                    "      \"paymentStatus\": \"SUCCESS\",\n" +
                                                    "      \"isFirstInstallment\": false,\n" +
                                                    "      \"lastExecutionDate\": \"2024-01-25\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"ERROR\",\n" +
                                                    "  \"message\": \"계좌번호가 올바르지 않습니다.\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"status\": \"ERROR\",\n" +
                                                    "  \"message\": \"상품별 자동이체 납입 정보 조회 중 오류가 발생했습니다.\"\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<PaymentInfoResponse>>> getIncomingTransfers(
            @RequestParam String toAccountNumber) {
        try {
            List<PaymentInfoResponse> response = autoTransferService.getIncomingTransfers(toAccountNumber);
            return ResponseEntity.ok(ApiResponse.success("상품별 자동이체 납입 정보 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("상품별 자동이체 납입 정보 조회 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("상품별 자동이체 납입 정보 조회 중 오류가 발생했습니다."));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "자동이체 수정", description = "자동이체 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<AutoTransferDto.Response>> update(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @Valid @RequestBody AutoTransferDto.UpdateRequest request) {
        try {
            AutoTransferDto.Response response = autoTransferService.update(id, userId, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("자동이체 수정 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("자동이체 수정 중 오류가 발생했습니다."));
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "자동이체 삭제", description = "자동이체를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-USER-ID", required = false) String userId) {
        try {
            autoTransferService.delete(id, userId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("자동이체 삭제 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("자동이체 삭제 중 오류가 발생했습니다."));
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "납입 정보 응답")
    public static class PaymentInfoResponse {
        @Schema(description = "자동이체 ID")
        private Long autoTransferId;
        
        @Schema(description = "출금 계좌번호")
        private String fromAccountNumber;
        
        @Schema(description = "입금 계좌번호")
        private String toAccountNumber;
        
        @Schema(description = "이체 금액")
        private BigDecimal amount;
        
        @Schema(description = "다음 납입일")
        private LocalDate nextPaymentDate;
        
        @Schema(description = "이번달 납입액")
        private BigDecimal monthlyAmount;
        
        @Schema(description = "남은 회차")
        private Integer remainingInstallments;
        
        @Schema(description = "현재 회차")
        private Integer currentInstallment;
        
        @Schema(description = "총 회차")
        private Integer totalInstallments;
        
        @Schema(description = "납입 상태")
        private String paymentStatus;
        
        @Schema(description = "1회차 여부")
        private Boolean isFirstInstallment;
        
        @Schema(description = "최종 실행일")
        private LocalDate lastExecutionDate;
    }
}


