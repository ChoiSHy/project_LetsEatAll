package com.letseatall.letseatall.data.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewResponseDto {
    private Long id;
    private Long uid;
    private String writer;
    private Long mid;
    private String menu;
    private String title;
    private String content;
    private Long img;
    private int score;
    private int count;
}
