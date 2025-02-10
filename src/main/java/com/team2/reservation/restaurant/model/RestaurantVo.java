package com.team2.reservation.restaurant.model;

import java.sql.Time;
import java.sql.Timestamp;

import com.team2.reservation.reserve.model.ReserveVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantVo {
	private int restNo;
	private String restName, restAddress, restInfo, restMenu, restPhone, restReview, restImage;
	private Time openTime, closeTime;
	private double avgScore;

	
    public String getFormattedPhone() {
        return restPhone != null ? restPhone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3") : null;
    }
	
    public String getOpenTimeStr() {
        return openTime != null ? openTime.toString().substring(0, 5) : null;
    }

    public String getCloseTimeStr() {
        return closeTime != null ? closeTime.toString().substring(0, 5) : null;
    }
}

