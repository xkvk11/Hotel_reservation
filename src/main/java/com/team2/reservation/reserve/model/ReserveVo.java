package com.team2.reservation.reserve.model;

import java.sql.Time;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveVo {
	private int reserveNo;     // 예약 번호
    private int restNo;        // 식당 번호
    private int userNo;        // 사용자 번호
    private int headCount;     // 인원 수
    private String restName;   // 식당 이름
    private String restReview; // 리뷰
    private Timestamp reserveTime;  // 예약 시간
    private Time openTime,closeTime;
    
    public String getReserveTimeStr() {
        return reserveTime != null ? reserveTime.toString().substring(0, 16) : null;
    }
}