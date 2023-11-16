package com.letseatall.letseatall.data.dto.Review;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewPageDto {
    private Long review_id;
    private String content;
    private int score;
    private int like_count;
    private int unlike_count;
    private Long menu_id;
    private String menu_name;
    private String writer;
    private Long user_id;
    private String img_url;
    private String updatedAt;

    public ReviewPageDto(ReviewResponseDto responseDto){
        this.review_id=responseDto.getReview_id();
        this.content = responseDto.getContent();
        this.score = responseDto.getScore();
        this.like_count = responseDto.getLike_count();
        this.unlike_count = responseDto.getUnlike_count();
        this.menu_id = responseDto.getMenu_id();
        this.menu_name=responseDto.getMenu_name();
        this.writer=responseDto.getWriter();
        this.user_id = responseDto.getUser_id();
        this.img_url = responseDto.getImg_url();
        this.updatedAt=responseDto.getUpdatedAt().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"));
    }
}
