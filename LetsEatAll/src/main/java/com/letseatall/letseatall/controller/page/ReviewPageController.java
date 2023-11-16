package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.data.dto.Review.ReviewPageDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("page/review")
public class ReviewPageController {
    private final ReviewService reviewService;
    private final Logger LOGGER = LoggerFactory.getLogger(ReviewPageController.class);

    @GetMapping()
    public String getReview(@RequestParam long id, Model model) {
        ReviewResponseDto rrd = reviewService.getReview(id);
        ReviewPageDto review = new ReviewPageDto(rrd);

        model.addAttribute("review", review);

        return "review/reviewPage";
    }
    @GetMapping("/create")
    public String createReview(@CookieValue(value="token",required = false) String token,
                               @PathVariable(value = "mid") Long id, Model model){
        model.addAttribute("mid",id);
        return "review/ReviewWriter";
    }

    @GetMapping("/modify")
    public String modifyReview(
            @CookieValue(value = "token",required = true)String token,
            @RequestParam long id, Model model) {
        System.out.println(model.getAttribute("review"));
        return "review/ReviewWriter";
    }

    @GetMapping("/remove")
    public String removeReview(
            @CookieValue(value = "token",required = true)String token,
            @RequestParam long id, Model model) {
        LOGGER.info("리뷰 삭제");
        reviewService.deleteReview(id);
        return "main/main";
    }

    @GetMapping("/like")
    public void likeReview(
            @CookieValue(value = "token",required = true)String token,
            @RequestParam long id, Model model) {
        try {
            ReviewResponseDto rrd = reviewService.likeReview(id, 1);
            ReviewPageDto review = new ReviewPageDto(rrd);
            model.addAttribute("review", review);
        } catch (Exception e) {
        }
    }

    @GetMapping("/unlike")
    public void unlikeReview(
            @CookieValue(value = "token",required = true)String token,
            @RequestParam long id, Model model) {
        try {
            ReviewResponseDto rrd = reviewService.likeReview(id, -1);
            ReviewPageDto review = new ReviewPageDto(rrd);
            model.addAttribute("review", review);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
