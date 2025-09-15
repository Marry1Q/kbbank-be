package com.kopo_team4.kbbank_backend.domain.autotransfer.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 스케줄 파싱 컴포넌트
 * 
 * 기능: 자동이체 스케줄 문자열을 파싱하고 다음 실행일을 계산
 * 지원 형식: "매월 25일", "매주 월요일" 등
 */
@Component
public class ScheduleParser {
    
    /**
     * 다음 이체 예정일 계산
     * 
     * @param schedule 스케줄 문자열 (예: "매월 25일")
     * @param currentDate 현재 날짜
     * @return 다음 이체 예정일
     */
    public LocalDate calculateNextTransferDate(String schedule, LocalDate currentDate) {
        if (schedule == null || schedule.trim().isEmpty()) {
            throw new IllegalArgumentException("스케줄이 비어있습니다.");
        }
        
        String trimmedSchedule = schedule.trim();
        
        // "매월 XX일" 형식 파싱
        if (trimmedSchedule.startsWith("매월")) {
            return parseMonthlySchedule(trimmedSchedule, currentDate);
        }
        
        // "매주 XX요일" 형식 파싱
        if (trimmedSchedule.startsWith("매주")) {
            return parseWeeklySchedule(trimmedSchedule, currentDate);
        }
        
        throw new IllegalArgumentException("지원하지 않는 스케줄 형식입니다: " + schedule);
    }
    
    /**
     * 매월 스케줄 파싱 (예: "매월 25일")
     * 
     * @param schedule 스케줄 문자열
     * @param currentDate 현재 날짜
     * @return 다음 이체 예정일
     */
    private LocalDate parseMonthlySchedule(String schedule, LocalDate currentDate) {
        try {
            // "매월 25일" → "25" 추출
            String dayStr = schedule.replace("매월", "").replace("일", "").trim();
            int day = Integer.parseInt(dayStr);
            
            if (day < 1 || day > 31) {
                throw new IllegalArgumentException("일자는 1-31 사이여야 합니다.");
            }
            
            // 현재 월의 해당 일자
            LocalDate thisMonth = currentDate.withDayOfMonth(day);
            
            // 이미 지났으면 다음 달로 설정
            if (thisMonth.isBefore(currentDate) || thisMonth.isEqual(currentDate)) {
                thisMonth = thisMonth.plusMonths(1);
            }
            
            // 해당 월에 해당 일자가 없으면 말일로 설정
            try {
                return thisMonth.withDayOfMonth(day);
            } catch (Exception e) {
                return thisMonth.withDayOfMonth(thisMonth.lengthOfMonth());
            }
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 일자 형식입니다: " + schedule);
        }
    }
    
    /**
     * 매주 스케줄 파싱 (예: "매주 월요일")
     * 
     * @param schedule 스케줄 문자열
     * @param currentDate 현재 날짜
     * @return 다음 이체 예정일
     */
    private LocalDate parseWeeklySchedule(String schedule, LocalDate currentDate) {
        try {
            // "매주 월요일" → "월요일" 추출
            String dayOfWeekStr = schedule.replace("매주", "").trim();
            
            // 요일 매핑
            int targetDayOfWeek = mapDayOfWeek(dayOfWeekStr);
            
            // 현재 요일
            int currentDayOfWeek = currentDate.getDayOfWeek().getValue();
            
            // 다음 주의 해당 요일 계산
            int daysToAdd = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
            if (daysToAdd == 0) {
                daysToAdd = 7; // 같은 요일이면 다음 주로
            }
            
            return currentDate.plusDays(daysToAdd);
            
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 요일 형식입니다: " + schedule);
        }
    }
    
    /**
     * 요일 문자열을 숫자로 매핑
     * 
     * @param dayOfWeekStr 요일 문자열
     * @return 요일 숫자 (1=월요일, 7=일요일)
     */
    private int mapDayOfWeek(String dayOfWeekStr) {
        switch (dayOfWeekStr) {
            case "월요일":
                return 1;
            case "화요일":
                return 2;
            case "수요일":
                return 3;
            case "목요일":
                return 4;
            case "금요일":
                return 5;
            case "토요일":
                return 6;
            case "일요일":
                return 7;
            default:
                throw new IllegalArgumentException("지원하지 않는 요일입니다: " + dayOfWeekStr);
        }
    }
    
    /**
     * 오늘 실행해야 하는지 확인
     * 
     * @param schedule 스케줄 문자열
     * @param today 오늘 날짜
     * @return 실행 여부
     */
    public boolean shouldExecuteToday(String schedule, LocalDate today) {
        try {
            LocalDate nextDate = calculateNextTransferDate(schedule, today);
            return nextDate.isEqual(today);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 해당 월의 말일 계산
     * 
     * @param date 기준 날짜
     * @return 해당 월의 말일
     */
    public LocalDate getMonthEndDate(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }
}
