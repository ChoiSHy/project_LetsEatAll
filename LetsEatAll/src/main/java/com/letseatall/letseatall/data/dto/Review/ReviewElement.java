package com.letseatall.letseatall.data.dto.Review;

import lombok.Builder;

@Builder
public class ReviewElement {
    private Long id;
    private String title;
    private String content;
    private Long img;
    private int score;
    private int count;
    private Long uid;
    private String writer;
}
