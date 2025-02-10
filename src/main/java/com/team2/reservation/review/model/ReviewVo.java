package com.team2.reservation.review.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewVo {
    private int reviewNo;        	// 리뷰 번호
    private int userNo;          	// 사용자 번호
    private int restNo;          	// 식당 번호
    private String userName;			// 사용자 이름
    private String reviewContent; 	// 리뷰 내용
    private int reviewScore;     	// 점수
    private Timestamp createDate; 	// 작성일
}