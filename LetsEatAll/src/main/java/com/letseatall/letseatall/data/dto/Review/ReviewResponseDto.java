package com.letseatall.letseatall.data.dto.Review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String content;
    private int score;
    private int count;
    private Long mid;
    private String writer;
    private Long uid;
    private String menu;
}
