package com.team2.reservation.restaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.team2.reservation.restaurant.model.RestaurantDao;
import com.team2.reservation.restaurant.model.RestaurantVo;



@Service
public class RestaurantService {
    private final RestaurantDao restaurantDao;
    private static final int PAGE_SIZE = 8; // 페이지당 레스토랑 개수
    private static final int PAGE_DISPLAY_LIMIT = 10; // 표시할 페이지 번호의 최대 개수
    

    @Autowired
    public RestaurantService(RestaurantDao restaurantDao) {
        this.restaurantDao = restaurantDao;
    }
    
   public void list(int page, Model model) {
        int offset = (page - 1) * PAGE_SIZE; // 시작 인덱스 계산
        List<RestaurantVo> restaurantList = restaurantDao.pullList(offset, PAGE_SIZE);
        int totalRestaurants = restaurantDao.getTotalCount(); // 총 레스토랑 개수
        int totalPages = (int) Math.ceil((double) totalRestaurants / PAGE_SIZE);

        // 페이지네이션 계산
        int startPage = ((page - 1) / PAGE_DISPLAY_LIMIT) * PAGE_DISPLAY_LIMIT + 1;
        int endPage = Math.min(startPage + PAGE_DISPLAY_LIMIT - 1, totalPages);

        model.addAttribute("list", restaurantList); // 현재 페이지의 레스토랑 목록
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", totalPages); // 전체 페이지 수
        model.addAttribute("startPage", startPage); // 표시할 시작 페이지 번호
        model.addAttribute("endPage", endPage); // 표시할 마지막 페이지 번호
   }
   
   
   // 검색
    public List<RestaurantVo> searchList(String restName) {
        return restaurantDao.search(restName);
    }
    
    // 최근 등록된 레스토랑 목록
    public List<RestaurantVo> recentRestaurants() {
        return restaurantDao.getRecentRestaurants();
    }
    
    public RestaurantVo getRestaurantById(int restNo) {
        return restaurantDao.getRestaurantById(restNo);
    }

    // 인기 레스토랑 목록
    public List<RestaurantVo> popularRestaurants(){
    	return restaurantDao.getPopularRestaurants();
    }
    
    // 오늘의 추천 목록
    public List<RestaurantVo> recommendRestaurants(){
    	 return restaurantDao.getRecommendRestaurants();
    }

}