package com.letseatall.letseatall.data.dto.Review;

import lombok.Data;

@Data
public class ReviewDto {
    private Long uid;
    private String writer;
    private Long mid;
    private String menu;
    private String title;
    private String content;
    private int score;
    private Long img;
}
