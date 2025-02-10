package com.team2.reservation.review.model;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ReviewDao {
    // 리뷰 추가
    @Insert("INSERT INTO review (userNo, restNo, reviewContent, reviewScore, createDate) "
            + "VALUES (#{userNo}, #{restNo}, #{reviewContent}, #{reviewScore}, NOW())")
    int addReview(ReviewVo bean);

    // 특정 식당의 모든 리뷰 조회 (userName 포함)
    @Select("SELECT r.*, u.userName FROM review r JOIN users u ON r.userNo = u.userNo "
            + "WHERE r.restNo = #{restNo} ORDER BY r.createDate DESC")
    List<ReviewVo> getReviewsByRestaurant(int restNo);

    // 특정 사용자의 모든 리뷰 조회 (userName 포함)
    @Select("SELECT r.*, u.userName FROM review r JOIN users u ON r.userNo = u.userNo "
            + "WHERE r.userNo = #{userNo} ORDER BY r.createDate DESC")
    List<ReviewVo> getReviewsByUser(int userNo);

    // 사용자가 해당 번호로 리뷰 존재 여부 확인 - @Param 추가
    @Select("SELECT COUNT(*) FROM review WHERE userNo = #{userNo} AND restNo = #{restNo}")
    int checkExistingReview(@Param("userNo") int userNo, @Param("restNo") int restNo);
    
    // 특정 리뷰 조회 (userName 포함)
    @Select("SELECT r.*, u.userName FROM review r JOIN users u ON r.userNo = u.userNo "
            + "WHERE r.reviewNo = #{reviewNo}")
    ReviewVo getReview(int reviewNo);

    // 리뷰 수정
    @Update("UPDATE review SET reviewContent = #{reviewContent}, reviewScore = #{reviewScore} "
            + "WHERE reviewNo = #{reviewNo}")
    int updateReview(ReviewVo bean);

    // 리뷰 삭제
    @Delete("DELETE FROM review WHERE reviewNo = #{reviewNo}")
    int deleteReview(int reviewNo);
}