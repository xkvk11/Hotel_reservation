package com.team2.reservation;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.team2.reservation.reserve.service.ReserveService;
import com.team2.reservation.restaurant.model.RestaurantVo;
import com.team2.reservation.restaurant.service.RestaurantService;
import com.team2.reservation.review.model.ReviewVo;
import com.team2.reservation.review.service.ReviewService;
import com.team2.reservation.user.model.UserDao;
import com.team2.reservation.user.model.UserVo;
import com.team2.reservation.user.service.UserService;

@Controller
public class HomeController {
    private final UserService userService;
    private final RestaurantService restService;
    private final ReserveService reserveService;
    private final ReviewService reviewService;

    @Autowired
    public HomeController(RestaurantService restService, ReserveService reserveService, UserService userService,
    		UserDao userDao, ReviewService reviewService) {
        this.restService = restService;
        this.userService = userService; 
        this.reserveService = reserveService;  
        this.reviewService = reviewService;        
    }

    // index page
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        UserVo user = (UserVo) session.getAttribute("loggedInUser"); 
        model.addAttribute("user", user); 
        List<RestaurantVo> recentRestaurants = restService.recentRestaurants();
        model.addAttribute("list", recentRestaurants);
        return "index";
    }

    // 회원가입
    @PostMapping("/")
    public String add(@ModelAttribute UserVo bean) {
        userService.add(bean);
        return "redirect:/";
    }
    
    // 중복 이메일 체킹
    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String userEmail) {
        boolean isAvailable = userService.isEmailAvailable(userEmail);
        return isAvailable ? ResponseEntity.ok("available") : ResponseEntity.ok("exists");
    }
    
    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestParam String userEmail, @RequestParam String userPw, 
            HttpSession session) {
        UserVo user = userService.login(userEmail, userPw);
        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return ResponseEntity.ok().body("success");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }

    
    // mypage
    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        UserVo user = (UserVo) session.getAttribute("loggedInUser");
        if(user == null) return "redirect:/";
        else if (user != null) {
            reserveService.listByUser(user.getUserNo(), model);
            String alertMessage = (String) session.getAttribute("alertMessage");
            String alertType = (String) session.getAttribute("alertType");
            
            if (alertMessage != null) {
                model.addAttribute("alertMessage", alertMessage);
                model.addAttribute("alertType", alertType);
                session.removeAttribute("alertMessage");
                session.removeAttribute("alertType");
            }
        }
        return "mypage";
    }
    
    // mypage - update
    @PostMapping("/mypage/edit")
    @ResponseBody
    public ResponseEntity<String> editReservation(@RequestParam int reserveNo, 
                                                @RequestParam int restNo, 
                                                @RequestParam int headCount, 
                                                @RequestParam String reserveDate,                                              
                                                HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
        
        UserVo user = (UserVo) session.getAttribute("loggedInUser");
        if (user == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", headers, HttpStatus.UNAUTHORIZED);
        }

        try {
            reserveService.updateReservation(reserveNo, restNo, headCount, reserveDate, user.getUserNo());
            return new ResponseEntity<>("예약이 수정되었습니다.", headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("예약을 수정하는 중에 오류가 발생했습니다. 다시 시도해주세요.", headers, HttpStatus.BAD_REQUEST);
        }
    }
    
    // mypage - delete
    @PostMapping("/mypage/delete")
    public String deleteReservation(@RequestParam("reserveNo") int reserveNo) {
        reserveService.deleteReservation(reserveNo);
        return "redirect:/mypage";
    }

    
    
    // restaurant page
    @GetMapping("/restaurant")
    public String showRestaurants(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        restService.list(page, model);
        return "restaurant"; 
    }

    // restaurant - 검색기능
    @GetMapping("/restaurant/search")
    public String searchRestaurants(@RequestParam String restName, Model model) {
        List<RestaurantVo> searchResults = restService.searchList(restName);
        model.addAttribute("restaurants", searchResults);
        model.addAttribute("keyword", restName);
        return "restaurant"; 
    }
    
    // restaurant - 정보
    @GetMapping("/restaurant/{restNo}")
    public String getRestaurantInfo(@PathVariable int restNo, Model model) {
        RestaurantVo restaurant = restService.getRestaurantById(restNo);
        model.addAttribute("restaurant", restaurant);
        return "restaurantInfo";
    }
    
    
    // restaurant - 예약기능
    @PostMapping("/restaurant")
    @ResponseBody
    public ResponseEntity<String> makeReservation(@RequestParam int restNo, 
                                  @RequestParam int headCount, 
                                  @RequestParam String reserveDate, 
                                  @RequestParam String reserveTime, 
                                  HttpSession session) {
                                  
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
        
        UserVo user = (UserVo) session.getAttribute("loggedInUser");
        if (user == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", headers, HttpStatus.UNAUTHORIZED);
        }
        
        try {
            LocalDateTime reservationTime = LocalDateTime.of(
                LocalDate.parse(reserveDate), 
                LocalTime.parse(reserveTime));
                
            reserveService.addReservation(restNo, headCount, reservationTime.toString(), user.getUserNo());
            return new ResponseEntity<>("예약이 완료되었습니다.", headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>("당일에 이미 예약된 레스토랑입니다.", headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("예약 처리 중 오류가 발생했습니다.", headers, HttpStatus.BAD_REQUEST);
        }
    }

    
    
    // 리뷰 추가
    @PostMapping("/review/add")
    public String addReview(@ModelAttribute ReviewVo bean, HttpSession session) {
        UserVo user = (UserVo) session.getAttribute("loggedInUser");
        if (user != null) {
            try {
                bean.setUserNo(user.getUserNo());
                if (bean.getRestNo() == 0) {
                    throw new IllegalArgumentException("식당 정보가 필요합니다.");
                }
                if (bean.getReviewContent() == null || bean.getReviewContent().trim().isEmpty()) {
                    throw new IllegalArgumentException("리뷰 내용을 입력해주세요.");
                }
                if (bean.getReviewScore() <= 0) {
                    bean.setReviewScore(1);
                }
                reviewService.add(bean);
                session.setAttribute("alertType", "success");
                session.setAttribute("alertMessage", "리뷰가 성공적으로 작성되었습니다!");
                
            } catch (IllegalStateException e) {
                session.setAttribute("alertType", "danger");
                session.setAttribute("alertMessage", e.getMessage());
            } catch (Exception e) {
                session.setAttribute("alertType", "danger");
                session.setAttribute("alertMessage", "리뷰 작성 중 오류가 발생했습니다. 필수 정보를 모두 입력했는지 확인해주세요.");
            }
            return "redirect:/mypage";
        }
        return "redirect:/";
    }
    
    
    // 인기 레스토랑
    @GetMapping("/api/popular")
    @ResponseBody
    public List<RestaurantVo> getPopularRestaurants(){
    	return restService.popularRestaurants();
    }
    
    // 오늘의 추천 레스토랑
    @GetMapping("/api/recommend")
    @ResponseBody
    public List<RestaurantVo> getRecommendRestaurants(){
    	return restService.recommendRestaurants();
    }
    
    // 레스토랑 리뷰 확인
    @GetMapping("/api/reviews/{restNo}")
    @ResponseBody
    public ResponseEntity<List<ReviewVo>> getRestaurantReviews(@PathVariable int restNo) {
        try {
            List<ReviewVo> reviews = reviewService.getReviewsByRestaurant(restNo);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}