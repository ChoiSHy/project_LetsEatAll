package com.letseatall.letseatall.data.dto.Review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewDto {
    private Long mid;
    private Long uid;
    private String title;
    private String content;
    private int score;
}
