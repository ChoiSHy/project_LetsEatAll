package com.letseatall.letseatall.data.dto.Review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    private Long mid;
    private Long uid;
    private String title;
    private String content;
    private int score;
    private String img;
}
