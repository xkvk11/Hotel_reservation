package com.team2.reservation.reserve.model;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ReserveDao {
    
    // User Info
    @Select("SELECT r.restNo, r.restName, res.reserveTime AS reserveTime, res.reserveNo, res.headCount, "
          + "r.openTime, r.closeTime "
          + "FROM restaurant r JOIN reservation res ON r.restNo = res.restNo "
          + "WHERE res.userNo = #{userNo} ORDER BY res.reserveNo")
    List<ReserveVo> pullListByUser(int userNo);
    
    // Reservation Info
    @Select("SELECT r.restNo, r.restName, res.reserveTime AS reserveTime, res.reserveNo, res.headCount, "
            + "r.openTime, r.closeTime "
            + "FROM restaurant r JOIN reservation res ON r.restNo = res.restNo "
            + "WHERE res.reserveNo = #{reserveNo}")
    ReserveVo getList(int reserveNo);
    
    // User Reservation Info
    @Select("SELECT * FROM reservation WHERE userNo = #{userNo} AND restNo = #{restNo} AND DATE(reserveTime) = #{date}")
    List<ReserveVo> findReservationsByUserAndRestaurant(@Param("userNo") int userNo, @Param("restNo") int restNo, @Param("date") LocalDate date);
    
    // Insert Reservation 
    @Insert("INSERT INTO reservation (restNo, userNo, reserveTime, headCount) VALUES (#{restNo}, #{userNo}, #{reserveTime}, #{headCount})")
    int addList(ReserveVo bean);
    
    // Edit Reservation
    @Update("UPDATE reservation SET reserveTime=#{reserveTime}, headCount=#{headCount} WHERE reserveNo=#{reserveNo}")
    int setList(ReserveVo bean);

    // Delete Reservation
    @Delete("DELETE FROM reservation WHERE reserveNo=#{reserveNo}")
    int rmList(int reserveNo);
}
